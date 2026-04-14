package io.capistudio.deckamushi.data.remote

sealed interface RemoteResult<out T> {
    data class Success<T>(
        val data: T,
        val eTag: String? = null,
    ) : RemoteResult<T>

    data class NotModified(
        val eTag: String? = null,
    ) : RemoteResult<Nothing>

    data class HttpError(
        val code: Int,
        val message: String,
    ) : RemoteResult<Nothing>

    data class NetworkError(
        val message: String,
    ) : RemoteResult<Nothing>
}