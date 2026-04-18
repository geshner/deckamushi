package io.capistudio.deckamushi.presentation.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter

@Composable
actual fun RemoteImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier,
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier.background(Color.LightGray),
        onState = { state ->
            when (state) {
                is AsyncImagePainter.State.Error -> {
                    // Temporary: make errors visually obvious
                    // (this still keeps the gray background, but you can print/log if needed)
                    println("Coil error: ${state.result.throwable.message}")
                }
                else -> Unit
            }
        }
    )
}
