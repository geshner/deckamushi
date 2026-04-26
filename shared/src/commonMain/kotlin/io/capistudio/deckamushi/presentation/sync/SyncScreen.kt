package io.capistudio.deckamushi.presentation.sync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.sync.SyncContract.Action

@Composable
fun SyncScreen(
    state: SyncContract.State,
    onAction: (Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StatusIndicator(state.status)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = statusLabel(state.status),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (state.lastSeededVersion != null || state.lastSeededCount != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (state.lastSeededVersion != null) {
                            SyncStatRow(label = "Last version", value = state.lastSeededVersion)
                        }
                        if (state.lastSeededVersion != null && state.lastSeededCount != null) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                        if (state.lastSeededCount != null) {
                            SyncStatRow(label = "Cards imported", value = state.lastSeededCount.toString())
                        }
                    }
                }
            }
        }

        Button(
            enabled = !state.isWorking,
            onClick = { onAction(Action.SyncClicked) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sync Card Database")
        }
    }
}

@Composable
private fun StatusIndicator(status: SyncStatus) {
    val containerColor: Color
    val icon: ImageVector?
    val tint: Color

    when (status) {
        SyncStatus.IDLE -> {
            containerColor = MaterialTheme.colorScheme.surfaceVariant
            icon = Icons.Default.Refresh
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        }
        SyncStatus.WORKING -> {
            containerColor = MaterialTheme.colorScheme.surfaceVariant
            icon = null
            tint = Color.Unspecified
        }
        SyncStatus.UP_TO_DATE -> {
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
            icon = Icons.Default.Check
            tint = MaterialTheme.colorScheme.onTertiaryContainer
        }
        SyncStatus.SEEDED -> {
            containerColor = MaterialTheme.colorScheme.primaryContainer
            icon = Icons.Default.Check
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        }
        SyncStatus.ERROR -> {
            containerColor = MaterialTheme.colorScheme.errorContainer
            icon = Icons.Default.Warning
            tint = MaterialTheme.colorScheme.onErrorContainer
        }
    }

    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        if (status == SyncStatus.WORKING) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = tint
            )
        }
    }
}

@Composable
private fun SyncStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun statusLabel(status: SyncStatus) = when (status) {
    SyncStatus.IDLE -> "Ready to sync"
    SyncStatus.WORKING -> "Syncing..."
    SyncStatus.UP_TO_DATE -> "Up to date"
    SyncStatus.SEEDED -> "Database seeded"
    SyncStatus.ERROR -> "Sync failed"
}