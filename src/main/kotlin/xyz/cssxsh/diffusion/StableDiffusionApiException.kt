package xyz.cssxsh.diffusion

public class StableDiffusionApiException(public val info: StableDiffusionApiErrorInfo) : IllegalStateException() {
    override val message: String get() = info.detail.toString()
}