package io.capistudio.deckamushi.domain.util

sealed interface DomainResult<out T> {
    data class Success<out T>(val data: T) : DomainResult<T>
    data class Error(val message: String, val throwable: Throwable? = null) : DomainResult<Nothing>
    data object Loading : DomainResult<Nothing>
}

// Extension to make it easy to use in ViewModels
inline fun <T> DomainResult<T>.onSuccess(action: (T) -> Unit): DomainResult<T> {
    if (this is DomainResult.Success) action(data)
    return this
}

inline fun <T> DomainResult<T>.onFailure(action: (String, e: Throwable?) -> Unit): DomainResult<T> {
    if (this is DomainResult.Error) action(message, throwable)
    return this
}

// Helpers for the UseCase/Repository layer to wrap results safely
inline fun <T> domainResult(block: () -> T): DomainResult<T> {
    return try {
        DomainResult.Success(block())
    } catch (e: Exception) {
        DomainResult.Error(e.message ?: "Unknown Error", e)
    }
}
