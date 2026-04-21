package io.capistudio.deckamushi.di

import io.capistudio.deckamushi.core.network.createHttpClient
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
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.cards.CardsBrowserViewModel
import io.capistudio.deckamushi.presentation.collection.CollectionViewModel
import io.capistudio.deckamushi.presentation.detail.CardDetailViewModel
import io.capistudio.deckamushi.presentation.sync.SyncViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// Declare the expectation for platform-specific dependencies
expect fun platformModule(): Module

private val sharedModule = module {
    //Platform-independent logic )Ktor
    single { createHttpClient() }
    singleOf(::DeckamushiDataApi)


    // Repository
    singleOf(::CardRepositoryImpl) { bind<CardRepository>() }

    // Use cases
    factoryOf(::GetCardByIdUseCase)
    factoryOf(::GetCardsCountUseCase)
    factoryOf(::GetCardsFoundByNameCountUseCase)
    factoryOf(::GetCardsPageUseCase)
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

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            platformModule(),
            sharedModule
        )
    }
}