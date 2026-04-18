package io.capistudio.deckamushi.presentation.sync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.sync.SyncContract.Action

@Composable
fun SyncScreen(
    state: SyncContract.State,
    onAction: (SyncContract.Action) -> Unit,
) {
    val status = when (state.status) {
        SyncStatus.IDLE -> "Idle"
        SyncStatus.WORKING -> "Working..."
        SyncStatus.UP_TO_DATE -> "Up to date"
        SyncStatus.SEEDED -> "Seeded"
        SyncStatus.ERROR -> "Error"
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("cards.json → seed DB")
        Spacer(Modifier.height(12.dp))

        Text("Status: $status")
        Text("Last seeded version: ${state.lastSeededVersion ?: "<none>"}")
        Text("Rows written: ${state.lastSeededCount?.toString() ?: "<none>"}")

        Spacer(Modifier.height(12.dp))

        Button(
            enabled = !state.isWorking,
            onClick = { onAction(Action.SyncClicked) }
        ) {
            Text("Sync / Seed cards")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { onAction(Action.GoToListClicked) }) {
            Text("Go To List")
        }
    }
}