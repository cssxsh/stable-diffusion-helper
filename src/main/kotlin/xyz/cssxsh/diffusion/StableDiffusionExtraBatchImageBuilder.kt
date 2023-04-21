package xyz.cssxsh.diffusion

import io.ktor.util.*
import kotlinx.serialization.json.*
import java.io.File

public class StableDiffusionExtraBatchImageBuilder {

    // region Upscaler

    /**
     * Resize Mode
     */
    @StableDiffusionDSL
    public var resizeMode: Int = 0

    /**
     * Upscaling Resize
     */
    @StableDiffusionDSL
    public var resize: Double = 2.0

    /**
     * Upscaling Resize Width
     */
    @StableDiffusionDSL
    public var width: Double = 512.0

    /**
     * Upscaling Resize Height
     */
    @StableDiffusionDSL
    public var height: Double = 512.0

    /**
     * Upscaling Crop
     */
    @StableDiffusionDSL
    public var crop: Boolean = true

    // endregion

    // region Upscaler

    /**
     * Upscaler 1
     */
    @StableDiffusionDSL
    public var upscaler1: String = "None"

    /**
     * Upscaler 2
     */
    @StableDiffusionDSL
    public var upscaler2: String = "None"

    /**
     * Extras Upscaler 2 Visibility
     */
    @StableDiffusionDSL
    public var extrasUpscaler2Visibility: Double = 0.0

    /**
     * Upscale First
     */
    @StableDiffusionDSL
    public var upscaleFirst: Boolean = false

    // endregion

    // region CodeFormer

    /**
     * CodeFormer Weight
     */
    @StableDiffusionDSL
    public var codeFormerWeight: Double = 0.0

    /**
     * CodeFormer Visibility
     */
    @StableDiffusionDSL
    public var codeFormerVisibility: Double = 0.0

    // endregion

    // region GFPGAN

    /**
     * GFPGAN Visibility
     */
    @StableDiffusionDSL
    public var gfpganVisibility: Double = 0.0

    // endregion

    /**
     * Init Images
     */
    @StableDiffusionDSL
    public var images: Map<String, String> = emptyMap()

    /**
     * Init Images
     */
    @StableDiffusionDSL
    public fun images(block: MutableMap<String, String>.() -> Unit) {
        images = buildMap(block)
    }

    /**
     * Init Images
     */
    @StableDiffusionDSL
    public fun MutableMap<String, String>.addBase64(file: File): String? {
        return put(file.name, base64(file))
    }

    /**
     * Init Images
     */
    @StableDiffusionDSL
    public fun base64(file: File): String {
        val type = when (val extension = file.extension) {
            "jpg" -> "jpeg"
            else -> extension
        }
        return "data:image/${type};base64,${file.readBytes().encodeBase64()}"
    }

    @PublishedApi
    internal val raw: MutableMap<String, JsonElement> = HashMap()

    @PublishedApi
    internal fun JsonObjectBuilder.push() {

        put("resize_mode", resizeMode)
        when (resizeMode) {
            0 -> {
                put("upscaling_resize", resize)
            }
            1 -> {
                put("upscaling_resize_w", width)
                put("upscaling_resize_h", height)
                put("upscaling_crop", crop)
            }
            else -> throw IllegalArgumentException("resize_mode: $resizeMode")
        }
        // show_extras_results

        put("upscaler_1", upscaler1)
        put("upscaler_2", upscaler2)
        put("extras_upscaler_2_visibility", extrasUpscaler2Visibility)
        put("upscale_first", upscaleFirst)

        put("codeformer_weight", codeFormerWeight)
        put("codeformer_visibility", codeFormerVisibility)

        put("gfpgan_visibility", gfpganVisibility)

        check(images.isEmpty().not()) { "image list is empty." }
        putJsonArray("imageList") {
            for ((name, data) in images) {
                addJsonObject {
                    put("name", name)
                    put("data", data)
                }
            }
        }

        for ((key, value) in raw) {
            put(key, value)
        }
    }

    public fun build(): JsonObject = buildJsonObject { push() }
}