package io.capistudio.deckamushi

import androidx.compose.ui.window.ComposeUIViewController
import io.capistudio.deckamushi.core.network.createHttpClient
import io.capistudio.deckamushi.data.local.VersionCacheFactory
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.local.db.DatabaseDriverFactory
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.di.AppDependencies
import io.capistudio.deckamushi.domain.UpdateCardDataUseCase

fun MainViewController() = ComposeUIViewController {
    val cache = VersionCacheFactory().create()
    val dbProvider = AppDatabaseProvider(DatabaseDriverFactory())
    val api = DeckamushiDataApi(createHttpClient())
    val useCase = UpdateCardDataUseCase(api, cache, dbProvider)
    val deps = AppDependencies(useCase)
    App(deps = deps)
}