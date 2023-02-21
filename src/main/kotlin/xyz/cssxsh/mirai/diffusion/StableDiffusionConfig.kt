package xyz.cssxsh.mirai.diffusion

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.diffusion.*

public object StableDiffusionConfig : ReadOnlyPluginConfig("client"), StableDiffusionClientConfig {

    @ValueName("base_url")
    @ValueDescription("基本网址")
    override val baseUrl: String by value("http://127.0.0.1:7860")

    @ValueName("dns_over_https")
    @ValueDescription("DNS")
    override val doh: String by value("https://public.dns.iij.jp/dns-query")

    @ValueName("ipv6")
    @ValueDescription("是否使用 IPv6")
    override val ipv6: Boolean by value(true)

    @ValueName("timeout")
    @ValueDescription("API超时时间")
    override val timeout: Long by value(180_000L)

    @ValueName("proxy")
    @ValueDescription("代理")
    override val proxy: String by value("")

}