package io.capistudio.deckamushi.presentation.scan

import androidx.compose.runtime.Composable

/**
 * Platform-specific scanner route entry point.
 *
 * Android provides permission handling and a live camera/OCR pipeline. Other platforms may render
 * placeholder behavior while keeping the shared navigation contract intact.
 */
@Composable
expect fun ScanRoute(
    showSnackbar: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToResults: (String) -> Unit,
)