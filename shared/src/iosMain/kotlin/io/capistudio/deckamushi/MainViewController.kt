package io.capistudio.deckamushi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import io.capistudio.deckamushi.core.network.createHttpClient
import io.capistudio.deckamushi.data.local.VersionCacheFactory
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.local.db.DatabaseDriverFactory
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.di.initKoin

fun MainViewController() = ComposeUIViewController {
    val cache = VersionCacheFactory().create()
    val dbProvider = AppDatabaseProvider(DatabaseDriverFactory())
    val api = DeckamushiDataApi(createHttpClient())

    LaunchedEffect(Unit) {
        initKoin(cache, dbProvider, api)
    }

    App()
}