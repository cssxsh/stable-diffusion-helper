package xyz.cssxsh.diffusion


public interface StableDiffusionClientConfig {
    public val baseUrl: String
    public val proxy: String
    public val doh: String
    public val ipv6: Boolean
    public val timeout: Long
}