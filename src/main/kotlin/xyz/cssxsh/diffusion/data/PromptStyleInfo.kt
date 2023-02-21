package xyz.cssxsh.diffusion.data

import kotlinx.serialization.*

@Serializable
public data class PromptStyleInfo(
    @SerialName("name")
    val name: String,
    @SerialName("prompt")
    val prompt: String? = null,
    @SerialName("negative_prompt")
    val negativePrompt: String? = null
)