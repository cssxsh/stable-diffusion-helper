package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*

@Serializable
public data class EmbeddingItem(
    @SerialName("step")
    val step: Int = 0,
    @SerialName("sd_checkpoint")
    val sdCheckpoint: String = "",
    @SerialName("sd_checkpoint_name")
    val sdCheckpointName: String = "",
    @SerialName("shape")
    val shape: Int,
    @SerialName("vectors")
    val vectors: Int
)