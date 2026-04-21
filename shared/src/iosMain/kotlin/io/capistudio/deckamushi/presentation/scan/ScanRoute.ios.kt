package io.capistudio.deckamushi.presentation.scan

import androidx.compose.runtime.Composable

@Composable
actual fun ScanRoute(
    showSnackbar: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToResults: (String) -> Unit,
) {
    CameraPreview(
        onTextDetected = { }
    ) // already shows "Scanner not available on this platform"
}