package io.capistudio.deckamushi.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingMedium

@Composable
fun HomeScreen(
    onOpenCards: () -> Unit,
    onOpenCollection: () -> Unit,
    onOpenSync: () -> Unit,
    onOpenScanner: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(paddingMedium, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = onOpenCards) { Text("All Cards") }
        Button(onClick = onOpenCollection) { Text("My Collection") }
        Button(onClick = onOpenSync) { Text("Sync Data") }
        Button(onClick = onOpenScanner) { Text("Scanner") }
    }
}