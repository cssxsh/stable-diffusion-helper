package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class SamplerInfo(
    @SerialName("aliases")
    val aliases: List<String>,
    @SerialName("name")
    val name: String,
    @SerialName("options")
    val options: Map<String, String>
)