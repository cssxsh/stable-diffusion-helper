package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*

@Serializable
public data class FaceRestorerInfo(
    @SerialName("name")
    val name: String = "",
    @SerialName("cmd_dir")
    val cmdDir: String? = null,
)