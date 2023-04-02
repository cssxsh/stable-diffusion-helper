package xyz.cssxsh.diffusion

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import xyz.cssxsh.diffusion.data.*

public class StableDiffusionClient(@PublishedApi internal val config: StableDiffusionClientConfig) {
    @PublishedApi
    internal val http: HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(json = Json)
        }
        install(HttpTimeout) {
            socketTimeoutMillis = config.timeout
            connectTimeoutMillis = config.timeout
            requestTimeoutMillis = null
        }
        defaultRequest {
            url(config.baseUrl)
        }
        HttpResponseValidator {
            validateResponse { response ->
                when (response.status) {
                    HttpStatusCode.NotFound -> throw StableDiffusionApiException(info = response.body())
                    HttpStatusCode.MethodNotAllowed -> throw StableDiffusionApiException(info = response.body())
                    HttpStatusCode.UnprocessableEntity -> throw StableDiffusionApiException(info = response.body())
                }
                if (response.status.value in 400..499) throw ClientRequestException(response, response.body())
                if (response.status.value in 500..599) throw ServerResponseException(response, response.body())
            }
        }
        BrowserUserAgent()
        ContentEncoding()
        engine {
            config {
                doh(urlString = config.doh, ipv6 = config.ipv6)
                proxy(urlString = config.proxy)
            }
        }
    }

    public suspend fun memory(): MemoryResponse {
        return http.get {
            url("/sdapi/v1/memory")
        }.body()
    }

    // region Generate

    public suspend fun generateTextToImage(builder: StableDiffusionTextToImageBuilder): ImageResponse {
        return http.post {
            url("/sdapi/v1/txt2img")

            setBody(body = builder.build())
            contentType(ContentType.Application.Json)
        }.body()
    }

    public suspend inline fun generateTextToImage(block: StableDiffusionTextToImageBuilder.() -> Unit): ImageResponse {
        val builder = StableDiffusionTextToImageBuilder().apply(block)
        return generateTextToImage(builder = builder)
    }

    public suspend fun generateImageToImage(builder: StableDiffusionImageToImageBuilder): ImageResponse {
        return http.post {
            url("/sdapi/v1/img2img")

            setBody(body = builder.build())
            contentType(ContentType.Application.Json)
        }.body()
    }

    public suspend inline fun generateImageToImage(block: StableDiffusionImageToImageBuilder.() -> Unit): ImageResponse {
        val builder = StableDiffusionImageToImageBuilder().apply(block)
        return generateImageToImage(builder = builder)
    }

    public suspend fun extraSingleImage(info: Any): ProgressResponse {
        return http.post {
            url("/sdapi/v1/extra-single-image")

            setBody(body = info)
            contentType(ContentType.Application.Json)
        }.body()
    }

    public suspend fun extraBatchImage(info: Any): ProgressResponse {
        return http.post {
            url("/sdapi/v1/extra-batch-images")

            setBody(body = info)
            contentType(ContentType.Application.Json)
        }.body()
    }

    // endregion

    // region Parser

    /**
     * @param image The base64 encoded PNG image
     */
    public suspend fun parserPngInfo(image: String): PNGInfoResponse {
        return http.post {
            url("/sdapi/v1/png-info")

            setBody(body = buildJsonObject {
                put("image", image)
            })
            contentType(ContentType.Application.Json)
        }.body()
    }

    // endregion

    // region Progress

    public suspend fun progress(skipCurrentImage: Boolean = false): ProgressResponse {
        return http.get {
            url("/sdapi/v1/progress")

            parameter("skip_current_image", skipCurrentImage)
        }.body()
    }

    // endregion

    // region Interrogate

    public suspend fun interrogate(info: Any): ProgressResponse {
        return http.get {
            url("/sdapi/v1/progress")
        }.body()
    }

    // endregion

    // region Interrupt

    public suspend fun interrupt(info: Any): ProgressResponse {
        return http.get {
            url("/sdapi/v1/interrupt")
        }.body()
    }

    // endregion

    // region Skip

    public suspend fun skip(info: Any): ProgressResponse {
        return http.get {
            url("/sdapi/v1/skip")
        }.body()
    }

    // endregion

    // region Config

    public suspend fun options(): JsonObject {
        return http.get {
            url("/sdapi/v1/options")
        }.body()
    }

    public suspend fun options(builder: JsonObjectBuilder.() -> Unit): JsonElement {
        return http.post {
            url("/sdapi/v1/options")

            setBody(body = buildJsonObject(builder))
            contentType(ContentType.Application.Json)
        }.body()
    }

    // endregion

    // region Arguments

    public suspend fun getSamplers(): List<SamplerInfo> {
        return http.get {
            url("/sdapi/v1/samplers")
        }.body()
    }

    public suspend fun getUpScalers(): List<UpScalerInfo> {
        return http.get {
            url("/sdapi/v1/upscalers")
        }.body()
    }

    public suspend fun getSDModels(): List<StableDiffusionModelInfo> {
        return http.get {
            url("/sdapi/v1/sd-models")
        }.body()
    }

    public suspend fun getHyperNetworks(): List<HyperNetworkInfo> {
        return http.get {
            url("/sdapi/v1/hypernetworks")
        }.body()
    }

    public suspend fun getFaceRestorers(): List<FaceRestorerInfo> {
        return http.get {
            url("/sdapi/v1/face-restorers")
        }.body()
    }

    public suspend fun getRealEsrganModels(): List<RealEsrganModelInfo> {
        return http.get {
            url("/sdapi/v1/realesrgan-models")
        }.body()
    }

    public suspend fun getPromptStyles(): List<PromptStyleInfo> {
        return http.get {
            url("/sdapi/v1/prompt-styles")
        }.body()
    }

    public suspend fun getEmbeddings(): EmbeddingsResponse {
        return http.get {
            url("/sdapi/v1/embeddings")
        }.body()
    }

    // endregion
}