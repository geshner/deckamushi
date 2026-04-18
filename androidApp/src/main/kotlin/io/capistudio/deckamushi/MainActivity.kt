package io.capistudio.deckamushi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.capistudio.deckamushi.core.network.createHttpClient
import io.capistudio.deckamushi.data.local.VersionCacheFactory
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.local.db.DatabaseDriverFactory
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.di.initKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Koin must be started before any Composables call koinInject().
        // Doing this inside LaunchedEffect can race with composition.
        val cache = VersionCacheFactory(this).create()
        val dbProvider = AppDatabaseProvider(DatabaseDriverFactory(this))
        val api = DeckamushiDataApi(createHttpClient())
        initKoin(cache, dbProvider, api)



        setContent {
            App()
        }
    }
}

