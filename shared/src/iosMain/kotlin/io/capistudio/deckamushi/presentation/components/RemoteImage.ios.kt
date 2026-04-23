package io.capistudio.deckamushi.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import deckamushi.shared.generated.resources.Res
import deckamushi.shared.generated.resources.card_placeholder
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun RemoteImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier,
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
        placeholder = painterResource(Res.drawable.card_placeholder),
        error = painterResource(Res.drawable.card_placeholder),
    )
}