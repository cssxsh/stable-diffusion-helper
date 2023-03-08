package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class TextToImageResponseInfo(
    @SerialName("prompt")
    val prompt: String,
    @SerialName("negative_prompt")
    val negativePrompt: String,
    @SerialName("seed")
    val seed: Long,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("sampler_name")
    val samplerName: String,
    @SerialName("cfg_scale")
    val cfgScale: Double,
    @SerialName("steps")
    val steps: Int,
    @SerialName("batch_size")
    val batchSize: Int,
    @SerialName("styles")
    val styles: List<String>,
)