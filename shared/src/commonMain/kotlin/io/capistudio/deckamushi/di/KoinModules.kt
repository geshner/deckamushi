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
import io.capistudio.deckamushi.domain.usecase.GetOwnedQuantityUseCase
import io.capistudio.deckamushi.domain.usecase.IncrementOwnedUseCase
import io.capistudio.deckamushi.domain.usecase.SearchCardByNameUseCase
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.cards.CardsBrowserViewModel
import io.capistudio.deckamushi.presentation.detail.CardDetailViewModel
import io.capistudio.deckamushi.presentation.sync.SyncViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val sharedModule = module {
    //Repository
    single<CardRepository> { CardRepositoryImpl(dbProvider = get()) }

    //Usecases
    factory { GetCardByIdUseCase(repository = get()) }
    factory { GetCardsCountUseCase(repository = get()) }
    factory { GetCardsFoundByNameCountUseCase(repository = get()) }
    factory { GetCardsPageUseCase(repository = get()) }
    factory { SearchCardByNameUseCase(repository = get()) }
    factory { GetOwnedQuantityUseCase(repository = get()) }
    factory { IncrementOwnedUseCase(repository = get()) }
    factory { DecrementOwnedUseCase(repository = get()) }
    factory { UpdateCardDataUseCase(api = get(), cache = get(), dbProvider = get()) }

    //ViewModels
    factory {
        CardsBrowserViewModel(
            getCardsPageUseCase = get(),
            getCardsCountUseCase = get(),
            searchCardByNameUseCase = get(),
            getCardsFoundByNameCountUseCase = get()
        )
    }
    factory { SyncViewModel(updateCardDataUseCase = get()) }
    factory { (cardId: String) -> CardDetailViewModel(
        cardId = cardId,
        getCardByIdUseCase = get(),
        getOwnedQuantityUseCase = get(),
        incrementOwnedUseCase = get(),
        decrementOwnedUseCase = get()
    ) }
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