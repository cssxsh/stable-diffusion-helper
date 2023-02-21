package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*

@Serializable
public data class HyperNetworkInfo(
    @SerialName("name")
    val name: String,
    @SerialName("path")
    val path: String? = null
)