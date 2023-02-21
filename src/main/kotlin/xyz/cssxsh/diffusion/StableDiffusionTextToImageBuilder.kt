package xyz.cssxsh.diffusion

import kotlinx.serialization.json.*
import kotlin.random.*

public class StableDiffusionTextToImageBuilder {

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
    public var styles: List<String> = listOf("anime")

    // region Sampling

    /**
     * TODO: docs
     */
    @StableDiffusionDSL
    public var samplerIndex: String = "Euler"

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

    /**
     * TODO: docs
     */
    @StableDiffusionDSL
    public var firstPhaseWidth: Int = 0

    /**
     * TODO: docs
     */
    @StableDiffusionDSL
    public var firstPhaseHeight: Int = 0

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

    /**
     * TODO: docs
     */
    @StableDiffusionDSL
    public var eta: Double = 0.0

    /**
     * TODO: docs
     */
    @StableDiffusionDSL
    public var sChurn: Double = 0.0

    /**
     * TODO: docs
     */
    @StableDiffusionDSL
    public var sTMax: Double = 0.0

    /**
     * TODO: docs
     */
    @StableDiffusionDSL
    public var sTMin: Double = 0.0

    /**
     * TODO: docs
     */
    @StableDiffusionDSL
    public var sNoise: Double = 1.0

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

    // region Hires. fix

    /**
     * Hires. fix
     */
    @StableDiffusionDSL
    public var enableHR: Boolean = false

    /**
     * Upscale by
     */
    @StableDiffusionDSL
    public var hrScale: Double = 2.0

    /**
     * Upscaler
     */
    @StableDiffusionDSL
    public var hrUpscaler: String = "Latent"

    /**
     * Hires Steps
     */
    @StableDiffusionDSL
    public var hrSecondPassSteps: Int = 0

    /**
     * Resize width to
     */
    @StableDiffusionDSL
    public var hrResizeX: Int? = null

    /**
     * Resize width to
     */
    @StableDiffusionDSL
    public var hrResizeY: Int? = null

    /**
     * Denoising strength
     */
    @StableDiffusionDSL
    public var denoisingStrength: Double = 0.7

    // endregion

    public val raw: MutableMap<String, JsonElement> = HashMap()

    @PublishedApi
    internal fun JsonObjectBuilder.push() {
        put("height", height)
        put("width", width)

        put("prompt", prompt)
        put("negative_prompt", negativePrompt)

        putJsonArray("styles") { for (style in styles) add(style) }

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

        put("eta", eta)

//        put("firstphase_width", firstPhaseWidth)
//        put("firstphase_height", firstPhaseHeight)

//        put("s_churn", sChurn)
//        put("s_tmax", sTMax)
//        put("s_tmin", sTMin)
//        put("s_noise", sNoise)

//        putJsonObject("override_settings") {}
//        put("override_settings_restore_afterwards", true)

//        put("sampler_index", samplerIndex)
        put("sampler_name", samplerName)

        if (enableHR) {
            put("enable_hr", true)
            put("hr_second_pass_steps", hrSecondPassSteps)
            put("denoising_strength", denoisingStrength)
            put("hr_upscaler", hrUpscaler)

            put("hr_scale", hrScale)
//            put("hr_resize_x", hrResizeX)
//            put("hr_resize_y", hrResizeY)
        }

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