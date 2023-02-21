package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*

@Serializable
public data class EmbeddingsResponse(
    @SerialName("loaded")
    val loaded: Map<String, EmbeddingItem>,
    @SerialName("skipped")
    val skipped: Map<String, EmbeddingItem>
)