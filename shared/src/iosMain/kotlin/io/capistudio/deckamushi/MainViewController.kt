package io.capistudio.deckamushi

import androidx.compose.ui.window.ComposeUIViewController
import io.capistudio.deckamushi.core.network.createHttpClient
import io.capistudio.deckamushi.data.local.VersionCacheFactory
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.local.db.DatabaseDriverFactory
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.di.AppDependencies
import io.capistudio.deckamushi.domain.repository.CardRepositoryImpl
import io.capistudio.deckamushi.domain.usecase.GetCardsCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsPageUseCase
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.cards.CardsBrowserViewModel

fun MainViewController() = ComposeUIViewController {
    val cache = VersionCacheFactory().create()
    val dbProvider = AppDatabaseProvider(DatabaseDriverFactory())
    val api = DeckamushiDataApi(createHttpClient())
    val updateUseCase = UpdateCardDataUseCase(api, cache, dbProvider)

    val repository = CardRepositoryImpl(dbProvider)
    val cardsVm = CardsBrowserViewModel(
        getCardsPageUseCase = GetCardsPageUseCase(repository),
        getCardsCountUseCase = GetCardsCountUseCase(repository),
    )

    val deps = AppDependencies(
        updateCardDataUseCase = updateUseCase,
        cardsBrowserViewModel = cardsVm,
    )

    App(deps = deps)
}