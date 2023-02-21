package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*

@Serializable
public data class RealEsrganModelInfo(
    @SerialName("name")
    val name: String,
    @SerialName("path")
    val path: String? = null,
    @SerialName("scale")
    val scale: Int = 0
)