package io.capistudio.deckamushi.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Shared image-loading abstraction for card art.
 *
 * Platforms are free to provide different implementations (for example full remote loading on
 * Android and temporary placeholder behavior on iOS) while keeping the shared UI API stable.
 */
@Composable
expect fun RemoteImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
)