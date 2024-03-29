package xyz.cssxsh.mirai.diffusion

import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import okhttp3.internal.toHexString
import xyz.cssxsh.diffusion.*
import xyz.cssxsh.mirai.diffusion.config.*
import java.io.File
import java.time.*
import kotlin.coroutines.*

public object StableDiffusionListener : SimpleListenerHost() {

    @PublishedApi
    internal var client: StableDiffusionClient = StableDiffusionClient(config = StableDiffusionConfig)

    @PublishedApi
    internal val configFolder: File by lazy {
        try {
            StableDiffusionHelper.configFolder
        } catch (_: Exception) {
            File("out")
        }
    }

    @PublishedApi
    internal val dataFolder: File by lazy {
        try {
            StableDiffusionHelper.dataFolder
        } catch (_: Exception) {
            File("out")
        }
    }

    @PublishedApi
    internal val json: Json = Json {
        prettyPrint = true
    }

    @PublishedApi
    internal val logger: MiraiLogger = MiraiLogger.Factory.create(this::class.java)

    @PublishedApi
    internal val mutex: Mutex = Mutex()

    @PublishedApi
    internal var last: Long = -1

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        val cause = exception.cause as? StableDiffusionApiException ?: exception
        logger.warning(context[CoroutineName]?.name, cause)
    }

    @PublishedApi
    internal val reload: Permission by StableDiffusionPermissions

    @EventHandler
    public fun MessageEvent.reload() {
        if (toCommandSender().hasPermission(reload).not()) return
        val content = message.contentToString()
        """(?i)^(?:reload-sd|重载SD)""".toRegex().find(content) ?: return

        with(StableDiffusionHelper) {
            StableDiffusionConfig.reload()
            logger.info("config reloaded")
            client = StableDiffusionClient(config = StableDiffusionConfig)
        }
        launch {
            subject.sendMessage("配置已重载")
        }
    }

    @PublishedApi
    internal val txt2img: Permission by StableDiffusionPermissions

    @EventHandler
    public fun MessageEvent.txt2img() {
        if (toCommandSender().hasPermission(txt2img).not()) return
        val content = message.contentToString()
        val match = """(?i)^t2i\s*(\d*)""".toRegex().find(content) ?: return
        val (seed0) = match.destructured
        val next = content.substringAfter('\n', "").ifEmpty { return }
        val seed1 = seed0.toLongOrNull() ?: -1L

        logger.info("t2i for $sender with seed: $seed1")
        val sd = client
        val out = dataFolder

        launch(CoroutineName("txt2img")) {

            subject.sendMessage("正在努力绘画，请稍等...")

            mutex.withLock {
                val interval = System.currentTimeMillis() - last
                delay(StableDiffusionConfig.cd - interval)
                last = System.currentTimeMillis()
            }

            val response = sd.generateTextToImage {

                TextToImageConfig.push(this)

                seed = seed1

                prompt = next.replace("""#(\S+)""".toRegex()) { match ->
                    val (name) = match.destructured
                    styles += name

                    ""
                }.replace("""(\S+)=(".+?"|\S+)""".toRegex()) { match ->
                    val (key, value) = match.destructured
                    val primitive = when {
                        value.toLongOrNull() != null -> JsonPrimitive(value.toLong())
                        value.toDoubleOrNull() != null -> JsonPrimitive(value.toDouble())
                        value.toBooleanStrictOrNull() != null -> JsonPrimitive(value.toBoolean())
                        else -> JsonPrimitive(value.removeSurrounding("\""))
                    }
                    val target = when(key) {
                        "sampler" -> "sampler_name"
                        "count" -> "n_iter"
                        "hr" -> "enable_hr"
                        "negative" -> "negative_prompt"
                        else -> key
                    }
                    raw[target] = primitive

                    ""
                }
                if (styles.isEmpty().not()) logger.info("t2i for $sender with styles: $styles")
                if (raw.isEmpty().not()) logger.info("t2i for $sender with ${JsonObject(raw)}")
            }

            val message = when (TextToImageConfig.detailedOutput) {
                true -> buildForwardMessage {
                    val info = Json.decodeFromString(JsonObject.serializer(), response.info)

                    sender says {
                        response.images.mapIndexed { index, image ->
                            val temp = out.resolve(
                                "${LocalDate.now()}/${seed1}.${
                                    response.hashCode().toHexString()
                                }.${index}.png"
                            )
                            temp.parentFile.mkdirs()
                            temp.writeBytes(image.decodeBase64Bytes())
                            add(subject.uploadImage(temp))
                        }
                    }
                    sender says {
                        appendLine("seed=${info["seed"]}")
                        appendLine("height=${info["height"]}")
                        appendLine("width=${info["width"]}")
                        appendLine("steps=${info["steps"]}")
                        appendLine("cfg_scale=${info["cfg_scale"]}")
                        appendLine("sampler=${info["sampler_name"]}")
                        appendLine("batch_size=${info["batch_size"]}")
                        (info["styles"] as? JsonArray).orEmpty().forEach { style ->
                            appendLine("#${style.jsonPrimitive.content}")
                        }
                    }
                    sender says {
                        appendLine("prompt:")
                        appendLine(info["prompt"]?.jsonPrimitive?.content)
                    }
                    sender says {
                        appendLine("negative prompt:")
                        appendLine(info["negative_prompt"]?.jsonPrimitive?.content)
                    }
                }
                false -> response.images.mapIndexed { index, image ->
                    val temp =
                        out.resolve("${LocalDate.now()}/${seed1}.${response.hashCode().toHexString()}.${index}.png")
                    temp.parentFile.mkdirs()
                    temp.writeBytes(image.decodeBase64Bytes())

                    subject.uploadImage(temp)
                }.toMessageChain()
            }

            subject.sendMessage(message)
            subject.sendMessage(At(sender))
        }
    }

    @PublishedApi
    internal val img2img: Permission by StableDiffusionPermissions

    @EventHandler
    public fun MessageEvent.img2img() {
        if (toCommandSender().hasPermission(img2img).not()) return
        val content = message.findIsInstance<PlainText>()?.content ?: return
        val match = """(?i)^i2i\s*(\d*)""".toRegex().find(content) ?: return
        val (seed0) = match.destructured
        val next = content.substringAfter('\n', "").ifEmpty { return }
        val seed1 = seed0.toLongOrNull() ?: -1L

        logger.info("i2i for $sender with seed: $seed1")
        val sd = client
        val out = dataFolder

        launch(CoroutineName("img2img")) {

            subject.sendMessage("正在执行图生图，请稍等...")

            mutex.withLock {
                val interval = System.currentTimeMillis() - last
                delay(StableDiffusionConfig.cd - interval)
                last = System.currentTimeMillis()
            }

            val images = message.filterIsInstance<Image>().associateWith { image ->
                ImageFileHolder.load(image)
            }

            val response = sd.generateImageToImage {

                ImageToImageConfig.push(this)

                seed = seed1

                images {
                    for ((_, file) in images) {
                        addBase64(file)
                    }
                }

                prompt = next.replace("""#(\S+)""".toRegex()) { match ->
                    val (name) = match.destructured
                    styles += name

                    ""
                }.replace("""(\S+)=(".+?"|\S+)""".toRegex()) { match ->
                    val (key, value) = match.destructured
                    val primitive = when {
                        value.toLongOrNull() != null -> JsonPrimitive(value.toLong())
                        value.toDoubleOrNull() != null -> JsonPrimitive(value.toDouble())
                        value.toBooleanStrictOrNull() != null -> JsonPrimitive(value.toBoolean())
                        else -> JsonPrimitive(value.removeSurrounding("\""))
                    }
                    val target = when(key) {
                        "sampler" -> "sampler_name"
                        "count" -> "n_iter"
                        "hr" -> "enable_hr"
                        "negative" -> "negative_prompt"
                        else -> key
                    }
                    raw[target] = primitive

                    ""
                }
                if (styles.isEmpty().not()) logger.info("t2i for $sender with styles: $styles")
                if (raw.isEmpty().not()) logger.info("t2i for $sender with ${JsonObject(raw)}")
            }

            val message = when (ImageToImageConfig.detailedOutput) {
                true -> buildForwardMessage {
                    val info = Json.decodeFromString(JsonObject.serializer(), response.info)

                    sender says response.images.mapIndexed { index, image ->
                        val temp =
                            out.resolve("${LocalDate.now()}/${seed1}.${response.hashCode().toHexString()}.${index}.png")
                        temp.parentFile.mkdirs()
                        temp.writeBytes(image.decodeBase64Bytes())

                        subject.uploadImage(temp)
                    }.toMessageChain()

                    sender says {
                        appendLine("seed=${info["seed"]}")
                        appendLine("height=${info["height"]}")
                        appendLine("width=${info["width"]}")
                        appendLine("steps=${info["steps"]}")
                        appendLine("cfg_scale=${info["cfg_scale"]}")
                        appendLine("sampler=${info["sampler_name"]}")
                        appendLine("batch_size=${info["batch_size"]}")
                        (info["styles"] as? JsonArray).orEmpty().forEach { style ->
                            appendLine("#${style.jsonPrimitive.content}")
                        }
                    }

                    sender says {
                        appendLine("prompt:")
                        appendLine(info["prompt"]?.jsonPrimitive?.content)
                    }

                    sender says {
                        appendLine("negative prompt:")
                        appendLine(info["negative_prompt"]?.jsonPrimitive?.content)
                    }

                    sender says {
                        appendLine("原图:")
                        for ((image, _) in images) {
                            append(image)
                        }
                    }

                }
                false -> response.images.mapIndexed { index, image ->
                    val temp =
                        out.resolve("${LocalDate.now()}/${seed1}.${response.hashCode().toHexString()}.${index}.png")
                    temp.parentFile.mkdirs()
                    temp.writeBytes(image.decodeBase64Bytes())

                    subject.uploadImage(temp)
                }.toMessageChain()
            }

            subject.sendMessage(message)
            subject.sendMessage(At(sender))
        }
    }

    @EventHandler
    public fun MessageEvent.extra() {
        if (toCommandSender().hasPermission(img2img).not()) return
        val content = message.findIsInstance<PlainText>()?.content ?: return
        """(?i)^extra""".toRegex().find(content) ?: return
        val config = content.substringAfter('\n', "").ifEmpty { return }

        logger.info("extra for $sender")
        val sd = client
        val out = dataFolder

        launch(CoroutineName("extra")) {

            subject.sendMessage("正在执行附加功能，请稍等...")

            mutex.withLock {
                val interval = System.currentTimeMillis() - last
                delay(StableDiffusionConfig.cd - interval)
                last = System.currentTimeMillis()
            }

            val images = message.filterIsInstance<Image>().associateWith { image ->
                ImageFileHolder.load(image)
            }

            val response = sd.extraBatchImage {

                images {
                    for ((_, file) in images) {
                        addBase64(file)
                    }
                }

                """(\S+)=(".+?"|\S+)""".toRegex().findAll(config).forEach { match ->
                    val (key, value) = match.destructured
                    val primitive = when {
                        value.toLongOrNull() != null -> JsonPrimitive(value.toLong())
                        value.toDoubleOrNull() != null -> JsonPrimitive(value.toDouble())
                        value.toBooleanStrictOrNull() != null -> JsonPrimitive(value.toBoolean())
                        else -> JsonPrimitive(value.removeSurrounding("\""))
                    }
                    val target = when(key) {
                        "1" -> "upscaler_1"
                        "2" -> "upscaler_2"
                        "3" -> "extras_upscaler_2_visibility"
                        "4" -> "gfpgan_visibility"
                        "5" -> "codeformer_visibility"
                        "6" -> "codeformer_weight"
                        "mode" -> "resize_mode"
                        "resize" -> "upscaling_resize"
                        "width" -> "upscaling_resize_w"
                        "height" -> "upscaling_resize_h"
                        "crop" -> "upscaling_crop"
                        "GFPGAN" -> "gfpgan_visibility"
                        else -> key
                    }
                    raw[target] = primitive
                }
                if (raw.isEmpty().not()) logger.info("extra for $sender with ${JsonObject(raw)}")
            }

            val message = response.images.mapIndexed { index, image ->
                val temp =
                    out.resolve("${LocalDate.now()}/${0}.${response.hashCode().toHexString()}.${index}.png")
                temp.parentFile.mkdirs()
                temp.writeBytes(image.decodeBase64Bytes())

                subject.uploadImage(temp)
            }.toMessageChain()

            subject.sendMessage(message)
            subject.sendMessage(At(sender))
        }
    }

    @PublishedApi
    internal val styles: Permission by StableDiffusionPermissions

    @EventHandler
    public fun MessageEvent.styles() {
        if (toCommandSender().hasPermission(styles).not()) return
        val content = message.contentToString()
        """(?i)^(?:styles|风格)""".toRegex().find(content) ?: return

        logger.info("styles for $sender")
        val sd = client

        launch(CoroutineName("styles")) {
            val info = sd.getPromptStyles()
            val message = buildString {
                for (style in info) {
                    appendLine(style.name)
                }
                ifEmpty {
                    appendLine("内容为空")
                }
            }

            subject.sendMessage(message)
        }
    }

    @EventHandler
    public fun MessageEvent.style() {
        if (toCommandSender().hasPermission(styles).not()) return
        val content = message.contentToString()
        val match = """(?i)^(?:style|风格)\s+(.+)""".toRegex().find(content) ?: return
        val (name) = match.destructured

        logger.info("query style for $sender")
        val sd = client

        launch(CoroutineName("style")) {
            val style = sd.getPromptStyles().find { it.name == name }
            val message = buildString {
                if (style != null) {
                    appendLine("=== Prompt ===")
                    appendLine(style.prompt)
                    appendLine("=== Negative Prompt ===")
                    appendLine(style.negativePrompt)
                }
                ifEmpty {
                    appendLine("内容为空")
                }
            }

            subject.sendMessage(message)
        }
    }

    @PublishedApi
    internal val samplers: Permission by StableDiffusionPermissions

    @EventHandler
    public fun MessageEvent.samplers() {
        if (toCommandSender().hasPermission(samplers).not()) return
        val content = message.contentToString()
        """(?i)^(?:samplers|采样器)""".toRegex().find(content) ?: return

        logger.info("samplers for $sender")
        val sd = client

        launch(CoroutineName("samplers")) {
            val info = sd.getSamplers()
            val message = buildString {
                for (sampler in info) {
                    appendLine("sampler_name: ${sampler.name}")
                    appendLine("aliases: ${sampler.aliases}")
                }
                ifEmpty {
                    appendLine("内容为空")
                }
            }

            subject.sendMessage(message)
        }
    }

    @PublishedApi
    internal val upscalers: Permission by StableDiffusionPermissions

    @EventHandler
    public fun MessageEvent.upscalers() {
        if (toCommandSender().hasPermission(upscalers).not()) return
        val content = message.contentToString()
        """(?i)^(?:upscalers|升频器)""".toRegex().find(content) ?: return

        logger.info("upscalers for $sender")
        val sd = client

        launch(CoroutineName("upscalers")) {
            val info = sd.getUpScalers()
            val message = buildString {
                for (upscaler in info) {
                    appendLine(upscaler.name)
                }
                ifEmpty {
                    appendLine("内容为空")
                }
            }

            subject.sendMessage(message)
        }
    }

    @PublishedApi
    internal val models: Permission by StableDiffusionPermissions

    @EventHandler
    public fun MessageEvent.models() {
        if (toCommandSender().hasPermission(models).not()) return
        val content = message.contentToString()
        """(?i)^(?:models|模型集)""".toRegex().find(content) ?: return

        logger.info("models for $sender")
        val sd = client

        launch(CoroutineName("models")) {
            val info = sd.getSDModels()
            val message = buildString {
                for (model in info) {
                    appendLine(model.title)
                }
                ifEmpty {
                    appendLine("内容为空")
                }
            }

            subject.sendMessage(message)
        }
    }

    @EventHandler
    public fun MessageEvent.model() {
        if (toCommandSender().hasPermission(models).not()) return
        val content = message.contentToString()
        val match = """(?i)^(?:model|模型)\s+(.+)""".toRegex().find(content) ?: return
        val (title) = match.destructured

        logger.info("set model for $sender")
        val sd = client

        launch(CoroutineName("model")) {
            val info = sd.options {
                put("sd_model_checkpoint", title)
                put("CLIP_stop_at_last_layers", 2)
            }

            if (info == JsonNull) {
                subject.sendMessage("$title 模型已设置")
            } else {
                subject.sendMessage(info.toString())
            }
        }
    }
}