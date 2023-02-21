package xyz.cssxsh.diffusion.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.diffusion.*
import xyz.cssxsh.diffusion.data.*

public class TextToImageApi(@PublishedApi internal val client: StableDiffusionClient) {

    public suspend fun generate(info: kotlinx.serialization.json.JsonObject): ProgressResponse {
        return client.http.post {
            url {
                takeFrom("/sdapi/v1/txt2img")
            }

            setBody(body = info)
            contentType(ContentType.Application.Json)
        }.body()
    }

}