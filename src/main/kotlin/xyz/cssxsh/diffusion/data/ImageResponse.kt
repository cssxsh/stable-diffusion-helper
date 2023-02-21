package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class ImageResponse(
    @SerialName("images")
    val images: List<String> = emptyList(),
    @SerialName("info")
    val info: String,
    @SerialName("parameters")
    val parameters: JsonObject
)