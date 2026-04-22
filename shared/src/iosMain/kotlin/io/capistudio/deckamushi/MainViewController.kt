package io.capistudio.deckamushi

import androidx.compose.ui.window.ComposeUIViewController
import io.capistudio.deckamushi.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController {
        App()
    }
}
