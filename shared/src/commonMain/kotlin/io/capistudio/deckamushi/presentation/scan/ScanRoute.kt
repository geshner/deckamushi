package io.capistudio.deckamushi.presentation.scan

import androidx.compose.runtime.Composable

@Composable
expect fun ScanRoute(
    showSnackbar: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToResults: (String) -> Unit,
)