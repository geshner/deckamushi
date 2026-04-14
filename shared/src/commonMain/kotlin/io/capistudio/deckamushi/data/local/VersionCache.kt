package io.capistudio.deckamushi.data.local

interface VersionCache {
    suspend fun getVersionETag(): String?
    suspend fun setVersionETag(etag: String?)

    suspend fun getCardsVersion(): String
    suspend fun setCardsVersion(version: String)
}

expect class VersionCacheFactory {
    fun create(): VersionCache
}