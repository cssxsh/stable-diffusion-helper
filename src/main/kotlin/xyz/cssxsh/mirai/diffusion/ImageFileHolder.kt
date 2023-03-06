package xyz.cssxsh.mirai.diffusion

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import java.io.*
import java.util.*

public object ImageFileHolder {
    @PublishedApi
    internal val cacheFolder: File by lazy {
        try {
            StableDiffusionHelper.resolveDataFile("cache")
        } catch (_: Exception) {
            File("out")
        }
    }

    private val mutex = Mutex()

    private val images: MutableMap<String, Deferred<File>> = WeakHashMap()

    private val http: HttpClient = HttpClient(OkHttp) {
        ContentEncoding()
    }

    private suspend fun raw(filename: String, urlString: String): File {
        val source = runInterruptible {
            cacheFolder.mkdirs()
            cacheFolder.resolve(filename)
        }
        if (source.exists().not()) {
            val bytes = http.get(urlString).body<ByteArray>()

            source.writeBytes(bytes)
        }

        return source
    }

    public suspend fun load(image: Image): File {
        cacheFolder.mkdirs()
        val filename = buildString {
            image.md5.joinTo(this) { "%02x".format(it) }
            append('.')
            append(image.imageType.name.lowercase())
        }
        val deferred = mutex.withLock {
            if (images[filename]?.isActive != true) {
                images.remove(filename)?.onAwait
            }
            images.getOrPut(filename) {
                supervisorScope {
                    async {
                        raw(filename, image.queryUrl())
                    }
                }
            }
        }

        return deferred.await()
    }
}