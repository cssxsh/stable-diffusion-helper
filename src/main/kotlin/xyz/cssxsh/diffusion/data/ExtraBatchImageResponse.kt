package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class ExtraBatchImageResponse(
    @SerialName("images")
    val images: List<String> = emptyList(),
    @SerialName("html_info")
    val html: String
)