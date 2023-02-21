package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*

@Serializable
public data class UpScalerInfo(
    @SerialName("name")
    val name: String,
    @SerialName("model_name")
    val modelName: String? = null,
    @SerialName("model_path")
    val modelPath: String? = null,
    @SerialName("model_url")
    val modelUrl: String? = null,
    @SerialName("scale")
    val scale: Double = 0.0
)