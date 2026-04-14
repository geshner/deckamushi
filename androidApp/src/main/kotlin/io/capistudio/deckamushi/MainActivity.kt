package io.capistudio.deckamushi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
//import io.capistudio.deckamushi.App
import io.capistudio.deckamushi.data.local.VersionCacheFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(versionCacheFactory = VersionCacheFactory(this))
        }
    }
}

