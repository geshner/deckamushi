package io.capistudio.deckamushi.presentation.components

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(onBack: () -> Unit) {
    // iOS handles back via swipe gesture natively — no-op here
}