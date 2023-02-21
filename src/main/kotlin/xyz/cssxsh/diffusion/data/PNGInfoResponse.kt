package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class PNGInfoResponse(
    @SerialName("info")
    val info: String,
    @SerialName("items")
    val items: JsonObject
)