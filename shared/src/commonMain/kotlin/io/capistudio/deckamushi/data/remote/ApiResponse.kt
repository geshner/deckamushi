package io.capistudio.deckamushi.data.remote

import io.ktor.http.HttpStatusCode

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T, val eTag: String?) : ApiResponse<T>()
    object NotModified : ApiResponse<Nothing>()
    data class Error(val statusCode: HttpStatusCode, val message: String) : ApiResponse<Nothing>()
}