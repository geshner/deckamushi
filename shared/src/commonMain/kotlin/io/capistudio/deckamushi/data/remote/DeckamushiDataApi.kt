package io.capistudio.deckamushi.data.remote

import io.capistudio.deckamushi.BuildKonfig
import io.capistudio.deckamushi.data.remote.dto.CardDto
import io.capistudio.deckamushi.data.remote.dto.VersionDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.etag

class DeckamushiDataApi(
    private val client: HttpClient,
){

    private suspend fun githubGet(path: String, eTag: String? = null): HttpResponse {
        val url = BuildKonfig.BASE_URL.trimEnd('/') + "/" + path

        return client.get(url) {
            header(HttpHeaders.Authorization, "Bearer ${BuildKonfig.API_KEY}")
            header(HttpHeaders.Accept, "application/vnd.github.raw+json")
            header(HttpHeaders.UserAgent, "Deckamushi")

            if (!eTag.isNullOrBlank()) {
                header(HttpHeaders.IfNoneMatch, eTag)
            }
        }
    }

    suspend fun fetchVersion(etag: String? = null): RemoteResult<VersionDto> {
        val path = "version.json"
        return try {
            val response = githubGet(path, etag)
            val responseETag = response.headers[HttpHeaders.ETag] ?: etag

            when (response.status) {
                HttpStatusCode.NotModified -> RemoteResult.NotModified(responseETag)
                HttpStatusCode.OK -> RemoteResult.Success(response.body(), responseETag)
                else -> {
                    val errorBody = runCatching { response.bodyAsText() }.getOrNull()
                    RemoteResult.HttpError(
                        code = response.status.value,
                        message = "fetchVersion failed with HTTP ${response.status}",
                    )
                }
            }
        } catch (e: Exception) {
            RemoteResult.NetworkError("fetchVersion failed: ${e.message}")
        }
    }

    suspend fun fetchCards(): RemoteResult<List<CardDto>> {
        val path = "cards.json"
        return try {
            val response = githubGet(path)

            when (response.status) {
                HttpStatusCode.OK -> RemoteResult.Success(
                    data = response.body()
                )
                else -> {
                    val errorBody = runCatching { response.bodyAsText() }.getOrNull()
                    RemoteResult.HttpError(
                        code = response.status.value,
                        message = "fetchCards failed with HTTP ${response.status}${if (errorBody != null) ": $errorBody" else ""}",
                    )
                }
            }
        } catch (e: Exception) {
            RemoteResult.NetworkError("fetchCards failed: ${e.message}")
        }
    }
}
