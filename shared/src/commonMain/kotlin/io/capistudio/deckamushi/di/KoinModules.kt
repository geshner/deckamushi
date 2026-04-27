package io.capistudio.deckamushi.di

import io.capistudio.deckamushi.core.network.createHttpClient
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.domain.repository.CardRepository
import io.capistudio.deckamushi.domain.repository.CardRepositoryImpl
import io.capistudio.deckamushi.domain.usecase.DecrementOwnedUseCase
import io.capistudio.deckamushi.domain.usecase.ExportCollectionUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardByIdUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsByBaseIdUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsFoundByNameCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsPageUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedCardsUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedQuantityUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedTotalUseCase
import io.capistudio.deckamushi.domain.usecase.ImportCollectionUseCase
import io.capistudio.deckamushi.domain.usecase.IncrementOwnedUseCase
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.cards.CardListViewModel
import io.capistudio.deckamushi.presentation.collection.CollectionViewModel
import io.capistudio.deckamushi.presentation.detail.CardDetailViewModel
import io.capistudio.deckamushi.presentation.scan.ScanResultsViewModel
import io.capistudio.deckamushi.presentation.scan.ScanViewModel
import io.capistudio.deckamushi.presentation.settings.SettingsViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Supplies platform-only dependencies that shared code cannot construct for itself, such as the
 * SQLDelight driver setup or platform-backed local storage.
 */
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
    factoryOf(::GetCardsByBaseIdUseCase)
    factoryOf(::ExportCollectionUseCase)
    factoryOf(::ImportCollectionUseCase)

    // ViewModels
    viewModelOf(::CardListViewModel)
    viewModelOf(::CollectionViewModel)
    viewModelOf(::ScanViewModel)
    viewModelOf(::SettingsViewModel)

    // Detail depends on route arguments, so Koin parameters are used instead of a no-arg binding.
    viewModel { (cardId: String, fromScan: Boolean) ->
        CardDetailViewModel(
            cardId = cardId,
            fromScan = fromScan,
            getCardByIdUseCase = get(),
            getOwnedQuantityUseCase = get(),
            incrementOwnedUseCase = get(),
            decrementOwnedUseCase = get()
        )
    }

    // Scan results are keyed by scanned base id so the route provides that parameter.
    viewModel { (baseId: String) ->
        ScanResultsViewModel(
            baseId = baseId,
            getCardsByBaseIdUseCase = get()
        )
    }
}

/**
 * Starts Koin for the shared app.
 *
 * Shared bindings live in `sharedModule`; platform bindings come from `platformModule()`. The
 * optional config block lets the host app provide platform context before modules are loaded.
 */
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            platformModule(),
            sharedModule
        )
    }
}