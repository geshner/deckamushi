package io.capistudio.deckamushi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import io.capistudio.deckamushi.core.network.createHttpClient
//import io.capistudio.deckamushi.App
import io.capistudio.deckamushi.data.local.VersionCacheFactory
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.local.db.DatabaseDriverFactory
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.di.AppDependencies
import io.capistudio.deckamushi.domain.UpdateCardDataUseCase

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
                AppDependencies(updateCardDataUseCase = useCase)
            }
            App(deps)
        }
    }
}

