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

@Composable
fun HomeScreen(
    onOpenCards: () -> Unit,
    onOpenCollection: () -> Unit,
    onOpenSync: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Deckamushi", style = MaterialTheme.typography.headlineSmall)

        Button(onClick = onOpenCards) { Text("All Cards") }
        Button(onClick = onOpenCollection) { Text("My Collection") }
        Button(onClick = onOpenSync) { Text("Sync Data") }
    }
}