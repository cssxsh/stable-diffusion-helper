package xyz.cssxsh.diffusion

import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.UnknownHostException

internal fun OkHttpClient.Builder.doh(urlString: String, ipv6: Boolean) {
    dns(object : Dns {
        private val doh = DnsOverHttps.Builder()
            .client(OkHttpClient())
            .url(urlString.toHttpUrl())
            .includeIPv6(ipv6)
            .build()

        @Throws(UnknownHostException::class)
        override fun lookup(hostname: String): List<InetAddress> {
            return try {
                doh.lookup(hostname)
            } catch (_: UnknownHostException) {
                Dns.SYSTEM.lookup(hostname)
            }
        }
    })
}

internal fun OkHttpClient.Builder.proxy(urlString: String) {
    val url = urlString.toHttpUrlOrNull() ?: return
    val proxy = when (url.scheme) {
        "socks" -> Proxy(Proxy.Type.SOCKS, InetSocketAddress(url.host, url.port))
        "http" -> Proxy(Proxy.Type.HTTP, InetSocketAddress(url.host, url.port))
        else -> Proxy.NO_PROXY
    }
    proxy(proxy)
}