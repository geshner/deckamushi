package io.capistudio.deckamushi.di

import io.capistudio.deckamushi.data.local.VersionCache
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.domain.repository.CardRepository
import io.capistudio.deckamushi.domain.repository.CardRepositoryImpl
import io.capistudio.deckamushi.domain.usecase.DecrementOwnedUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardByIdUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsFoundByNameCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsPageUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedCardsUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedQuantityUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedTotalUseCase
import io.capistudio.deckamushi.domain.usecase.IncrementOwnedUseCase
import io.capistudio.deckamushi.domain.usecase.SearchCardByNameUseCase
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.cards.CardsBrowserViewModel
import io.capistudio.deckamushi.presentation.collection.CollectionViewModel
import io.capistudio.deckamushi.presentation.detail.CardDetailViewModel
import io.capistudio.deckamushi.presentation.sync.SyncViewModel
import org.koin.core.module.dsl.*
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val sharedModule = module {
    // Repository
    singleOf(::CardRepositoryImpl) { bind<CardRepository>() }

    // Use cases
    factoryOf(::GetCardByIdUseCase)
    factoryOf(::GetCardsCountUseCase)
    factoryOf(::GetCardsFoundByNameCountUseCase)
    factoryOf(::GetCardsPageUseCase)
    factoryOf(::SearchCardByNameUseCase)
    factoryOf(::GetOwnedQuantityUseCase)
    factoryOf(::IncrementOwnedUseCase)
    factoryOf(::DecrementOwnedUseCase)
    factoryOf(::UpdateCardDataUseCase)
    factoryOf(::GetOwnedCardsUseCase)
    factoryOf(::GetOwnedTotalUseCase)

    // ViewModels
    viewModelOf(::CardsBrowserViewModel)
    viewModelOf(::SyncViewModel)
    viewModelOf(::CollectionViewModel)

    viewModel { (cardId: String) ->
        CardDetailViewModel(
            cardId = cardId,
            getCardByIdUseCase = get(),
            getOwnedQuantityUseCase = get(),
            incrementOwnedUseCase = get(),
            decrementOwnedUseCase = get()
        )
    }
}

fun initKoin(
    cache: VersionCache,
    dbProvider: AppDatabaseProvider,
    api: DeckamushiDataApi,
) {
    val platformModule = module {
        single { cache }
        single { dbProvider }
        single { api }
    }

    startKoin {
        modules(platformModule, sharedModule)
    }
}