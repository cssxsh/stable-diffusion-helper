package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class ProgressResponse(
    @SerialName("progress")
    val progress: Double,
    @SerialName("eta_relative")
    val etaRelative: Double,
    @SerialName("state")
    val state: JsonObject,
    @SerialName("current_image")
    val currentImage: String?,
    @SerialName("textinfo")
    val textInfo: String?
)