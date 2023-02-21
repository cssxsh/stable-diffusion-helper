package xyz.cssxsh.mirai.diffusion

import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import okhttp3.internal.toHexString
import xyz.cssxsh.diffusion.*
import java.io.File
import java.time.*
import kotlin.coroutines.*
import kotlin.random.*

public object StableDiffusionListener : SimpleListenerHost() {

    @PublishedApi
    internal var client: StableDiffusionClient = StableDiffusionClient(config = StableDiffusionConfig)

    @PublishedApi
    internal var configFolder: File = File(".")

    @PublishedApi
    internal var dataFolder: File = File(".")

    @PublishedApi
    internal val json: Json = Json {
        prettyPrint = true
    }

    @PublishedApi
    internal val logger: MiraiLogger = MiraiLogger.Factory.create(this::class.java)

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        val cause = exception.cause as? StableDiffusionApiException ?: exception
        logger.warning(cause)
    }

    @EventHandler
    public fun MessageEvent.reload() {
        val content = message.contentToString()
        if (content != "重载SD") return

        with(StableDiffusionHelper) {
            StableDiffusionConfig.reload()
            logger.info("config reloaded")
            client = StableDiffusionClient(config = StableDiffusionConfig)
        }
    }


    @EventHandler
    public fun MessageEvent.txt2img() {
        val content = message.contentToString()
        val match = """(?i)t2i\s*(\d*)""".toRegex().find(content) ?: return
        val (seed0) = match.destructured
        val next = content.substringAfter('\n', "").ifEmpty { return }
        val seed1 = seed0.toLongOrNull() ?: Random.nextUInt().toLong()

        logger.info("t2i for $sender with seed: $seed1")
        val sd = client
        val out = dataFolder

        launch {
            val info = sd.generateTextToImage {
                seed = seed1

                prompt = next.replace("""#(\S+)""".toRegex()) { match ->
                    val (name) = match.destructured
                    styles += name

                    ""
                }.replace("""(\S+)=(\S+)""".toRegex()) { match ->
                    val (key, value) = match.destructured
                    val primitive = when {
                        value.toLongOrNull() != null -> JsonPrimitive(value.toLong())
                        value.toDoubleOrNull() != null -> JsonPrimitive(value.toDouble())
                        value.toBooleanStrictOrNull() != null -> JsonPrimitive(value.toBoolean())
                        else -> JsonPrimitive(value)
                    }
                    raw[key] = primitive

                    ""
                }
                if (styles.isEmpty().not()) logger.info("t2i for $sender with styles: $styles")
                if (raw.isEmpty().not()) logger.info("t2i for $sender with ${JsonObject(raw)}")
            }
            val message = info.images.mapIndexed { index, image ->
                val temp = out.resolve("${LocalDate.now()}/${seed1}.${info.hashCode().toHexString()}.${index}.png")
                temp.parentFile.mkdirs()
                temp.writeBytes(image.decodeBase64Bytes())

                subject.uploadImage(temp)
            }.toMessageChain()

            subject.sendMessage(message)
        }
    }
}