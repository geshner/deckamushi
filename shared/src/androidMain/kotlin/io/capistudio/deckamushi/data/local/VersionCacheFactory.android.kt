package io.capistudio.deckamushi.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val STORE_NAME = "deckamushi_prefs"
private val Context.datastore by preferencesDataStore(name = STORE_NAME)

class AndroidVersionCache(
    private val context: Context,
) : VersionCache {

    private val versionETagKey = stringPreferencesKey("version_etag")
    private val cardsVersionKey = stringPreferencesKey("cards_version")

    override suspend fun getVersionETag(): String? =
        context.datastore.data
            .map { prefs -> prefs[versionETagKey] }
            .first()

    override suspend fun setVersionETag(etag: String?) {
        context.datastore.edit { prefs ->
            if (etag == null) {
                prefs.remove(versionETagKey)
            } else prefs[versionETagKey] = etag
        }
    }

    override suspend fun getCardsVersion(): String =
        context.datastore.data
            .map { prefs -> prefs[cardsVersionKey] ?: "" }
            .first()

    override suspend fun setCardsVersion(version: String) {
        context.datastore.edit { prefs ->
            prefs[cardsVersionKey] = version
        }
    }
}