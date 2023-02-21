package xyz.cssxsh.diffusion

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class StableDiffusionApiErrorInfo(
    @SerialName("detail")
    val detail: JsonElement
)