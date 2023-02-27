package xyz.cssxsh.diffusion

import io.ktor.util.*
import kotlinx.serialization.json.*
import java.io.File
import kotlin.random.*

public class StableDiffusionImageToImageBuilder {

    // region Keywords

    /**
     * Prompt
     */
    @StableDiffusionDSL
    public var prompt: String = ""

    /**
     * Negative prompt
     */
    @StableDiffusionDSL
    public var negativePrompt: String = ""

    /**
     * Prompt
     */
    @StableDiffusionDSL
    public fun prompt(block: StringBuilder.() -> Unit) {
        prompt = buildString(block)
    }

    /**
     * Negative prompt
     */
    @StableDiffusionDSL
    public fun negative(block: StringBuilder.() -> Unit) {
        negativePrompt = buildString(block)
    }

    // endregion

    /**
     * Height
     */
    @StableDiffusionDSL
    public var height: Int = 540

    /**
     * Width
     */
    @StableDiffusionDSL
    public var width: Int = 360

    /**
     * Styles
     */
    @StableDiffusionDSL
    public var styles: List<String> = emptyList()

    // region Sampling

    /**
     * Sampling method
     */
    @StableDiffusionDSL
    public var samplerName: String = "Euler a"

    /**
     * Sampling method
     */
    @StableDiffusionDSL
    public fun sampler(name: String) {
        samplerName = name
    }

    /**
     * Sampling steps
     */
    @StableDiffusionDSL
    public var steps: Int = 32

    /**
     * Sampling steps
     */
    @StableDiffusionDSL
    public fun steps(value: Int?) {
        steps = value ?: return
    }

    // endregion

    /**
     * CFG Scale
     */
    @StableDiffusionDSL
    public var cfgScale: Double = 7.0

    // region Batch

    /**
     * Batch size
     */
    @StableDiffusionDSL
    public var batchSize: Int = 1

    /**
     * Batch count
     */
    @StableDiffusionDSL
    public var nIter: Int = 1

    // endregion

    /**
     * Restore faces
     */
    @StableDiffusionDSL
    public var restoreFaces: Boolean = false

    /**
     * Tiling
     */
    @StableDiffusionDSL
    public var tiling: Boolean = false

    // region Script

    /**
     * Script
     */
    @StableDiffusionDSL
    public var scriptName: String = "None"

    /**
     * Script
     */
    @StableDiffusionDSL
    public var scriptArgs: List<String> = emptyList()

    // endregion

    // region Seed

    /**
     * Seed
     */
    @StableDiffusionDSL
    public var seed: Long = Random.nextUInt().toLong()

    /**
     * Variation Seed
     */
    @StableDiffusionDSL
    public var subSeed: Long = -1

    /**
     * Variation Strength
     */
    @StableDiffusionDSL
    public var subSeedStrength: Double = 0.0

    /**
     * Resize seed From Width
     */
    @StableDiffusionDSL
    public var seedResizeFromWidth: Int = 0

    /**
     * Resize seed From Height
     */
    @StableDiffusionDSL
    public var seedResizeFromHeight: Int = 0

    // endregion

    /**
     * Init Images
     */
    @StableDiffusionDSL
    public var initImages: List<String> = emptyList()

    /**
     * Init Images
     */
    @StableDiffusionDSL
    public fun image(block: MutableList<String>.() -> Unit) {
        initImages = buildList(block)
    }

    /**
     * Init Images
     */
    @StableDiffusionDSL
    public fun MutableList<String>.addBase64(file: File): Boolean {
        return add(base64(file = file))
    }

    /**
     * Init Images
     */
    @StableDiffusionDSL
    public fun base64(file: File): String {
        val type = when (val extension = file.extension) {
            "jpg"  -> "jpeg"
            else -> extension
        }
        return "data:image/${type};base64,${file.readBytes().encodeBase64()}"
    }

    /**
     * Denoising strength
     */
    @StableDiffusionDSL
    public var denoisingStrength: Double = 0.7

    /**
     * Resize Mode
     */
    @StableDiffusionDSL
    public var resizeMode: ResizeMode = ResizeMode.JUST_RESIZE

    /**
     * Maskmask
     */
    @StableDiffusionDSL
    public var mask: String = ""

    @StableDiffusionDSL
    public fun mask(file: File) {
        mask = base64(file = file)
    }

    @StableDiffusionDSL
    public var maskBlur: Int = 4

    public val raw: MutableMap<String, JsonElement> = HashMap()

    @PublishedApi
    internal fun JsonObjectBuilder.push() {
        put("height", height)
        put("width", width)

        put("prompt", prompt)
        put("negative_prompt", negativePrompt)

        putJsonArray("styles") { for (style in styles) add(style) }

        check(initImages.isNotEmpty()) { "不能为空" }
        putJsonArray("init_images") { for (image in initImages) add(image) }

        put("resize_mode", resizeMode.ordinal)

        if (mask.isNotEmpty()) {
            put("mask", mask)
            put("mask_blur", maskBlur)
        }

        put("seed", seed)
        if (subSeed != -1L) {
            put("subseed", subSeed)
            put("subseed_strength", subSeedStrength)
            put("seed_resize_from_h", seedResizeFromHeight)
            put("seed_resize_from_w", seedResizeFromWidth)
        }

        put("batch_size", batchSize)
        put("n_iter", nIter)
        put("steps", steps)
        put("cfg_scale", cfgScale)
        put("restore_faces", restoreFaces)
        put("tiling", tiling)

        put("sampler_name", samplerName)

        put("denoising_strength", denoisingStrength)

        if (scriptName.isNotEmpty() && scriptName != "None") {
            put("script_name", scriptName)
            putJsonArray("script_args") { for (style in scriptArgs) add(style) }
        }

        for ((key, value) in raw) {
            put(key, value)
        }
    }

    public fun build(): JsonObject = buildJsonObject { push() }
}