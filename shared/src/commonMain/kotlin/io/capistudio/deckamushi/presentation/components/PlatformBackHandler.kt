package io.capistudio.deckamushi.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformBackHandler(onBack: () -> Unit)