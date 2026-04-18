package io.capistudio.deckamushi.di

import io.capistudio.deckamushi.data.local.VersionCache
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.domain.repository.CardRepository
import io.capistudio.deckamushi.domain.repository.CardRepositoryImpl
import io.capistudio.deckamushi.domain.usecase.GetCardByIdUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsFoundByNameCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsPageUseCase
import io.capistudio.deckamushi.domain.usecase.SearchCardByNameUseCase
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.cards.CardsBrowserViewModel
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
    factory { UpdateCardDataUseCase(api = get(), cache = get(), dbProvider = get()) }

    factory {
        CardsBrowserViewModel(
            getCardsPageUseCase = get(),
            getCardsCountUseCase = get(),
            searchCardByNameUseCase = get(),
            getCardsFoundByNameCountUseCase = get()
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