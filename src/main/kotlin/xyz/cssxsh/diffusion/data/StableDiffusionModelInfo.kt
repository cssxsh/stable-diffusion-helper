package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*

@Serializable
public data class StableDiffusionModelInfo(
    @SerialName("config")
    val config: String? = null,
    @SerialName("filename")
    val filename: String,
    @SerialName("hash")
    val hash: String? = null,
    @SerialName("model_name")
    val modelName: String,
    @SerialName("sha256")
    val sha256: String? = null,
    @SerialName("title")
    val title: String
)