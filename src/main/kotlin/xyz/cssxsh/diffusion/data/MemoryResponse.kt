package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*

@Serializable
public data class MemoryResponse(
    @SerialName("cuda")
    val cuda: Map<String, Map<String, Long>>,
    @SerialName("ram")
    val ram: Map<String, Double>,
)