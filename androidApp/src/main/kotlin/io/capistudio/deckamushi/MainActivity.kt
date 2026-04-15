package io.capistudio.deckamushi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import io.capistudio.deckamushi.core.network.createHttpClient
//import io.capistudio.deckamushi.App
import io.capistudio.deckamushi.data.local.VersionCacheFactory
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.local.db.DatabaseDriverFactory
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.di.AppDependencies
import io.capistudio.deckamushi.domain.repository.CardRepositoryImpl
import io.capistudio.deckamushi.domain.usecase.GetCardsCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsFoundByNameCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsPageUseCase
import io.capistudio.deckamushi.domain.usecase.SearchCardByNameUseCase
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.cards.CardsBrowserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)



        setContent {

            val deps = remember {
                val cache = VersionCacheFactory(this).create()
                val dbProvider = AppDatabaseProvider(DatabaseDriverFactory(this))
                val api = DeckamushiDataApi(createHttpClient())
                val useCase = UpdateCardDataUseCase(api, cache, dbProvider)
                val repository = CardRepositoryImpl(dbProvider)
                val viewModel = CardsBrowserViewModel(
                    getCardsPageUseCase = GetCardsPageUseCase(repository),
                    getCardsCountUseCase = GetCardsCountUseCase(repository),
                    searchCardByNameUseCase = SearchCardByNameUseCase(repository),
                    getCardsFoundByNameCountUseCase = GetCardsFoundByNameCountUseCase(repository),
                )
                AppDependencies(updateCardDataUseCase = useCase, viewModel)
            }
            App(deps)
        }
    }
}

