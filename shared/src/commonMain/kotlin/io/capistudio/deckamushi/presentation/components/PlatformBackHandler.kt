package io.capistudio.deckamushi.presentation.components

import androidx.compose.runtime.Composable

/**
 * Shared wrapper for platform back handling.
 *
 * This keeps common routes free from direct Android APIs while still allowing system/gesture back
 * to participate in viewmodel-driven navigation decisions.
 */
@Composable
expect fun PlatformBackHandler(onBack: () -> Unit)