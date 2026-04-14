package io.capistudio.deckamushi.data.local

import platform.Foundation.NSUserDefaults

actual class VersionCacheFactory {
    actual fun create(): VersionCache = IosVersionCache()
}

private class IosVersionCache(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : VersionCache {
    private val versionETagKey = "version_etag"
    private val cardsVersionKey = "cards_version"

    override suspend fun getVersionETag(): String? =
        defaults.stringForKey(versionETagKey)

    override suspend fun setVersionETag(etag: String?) {
        if (etag == null) {
            defaults.removeObjectForKey(versionETagKey)
        } else {
            defaults.setObject(etag, forKey = versionETagKey)
        }
    }

    override suspend fun getCardsVersion(): String =
        defaults.stringForKey(cardsVersionKey) ?: ""

    override suspend fun setCardsVersion(version: String) {
        defaults.setObject(version, forKey = cardsVersionKey)
    }
}
