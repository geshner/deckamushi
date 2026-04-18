package io.capistudio.deckamushi.presentation.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun RemoteImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier,
) {
    // Placeholder for now (keeps iOS compiling).
    // Later we can plug Kamel or another KMP image loader.
    androidx.compose.foundation.layout.Box(
        modifier = modifier.background(Color.LightGray)
    )
}