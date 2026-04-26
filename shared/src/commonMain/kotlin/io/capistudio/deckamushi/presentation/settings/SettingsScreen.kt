package io.capistudio.deckamushi.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.sync.SyncContract
import io.capistudio.deckamushi.presentation.sync.SyncStatus

@Composable
fun SettingsScreen(
    syncState: SyncContract.State,
    onSyncAction: (SyncContract.Action) -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsSection(title = "Card Database") {
            SyncSettingsContent(
                state = syncState,
                onSyncClick = { onSyncAction(SyncContract.Action.SyncClicked) }
            )
        }

        SettingsSection(title = "Collection Backup") {
            SettingsActionRow(
                icon = Icons.Default.Upload,
                title = "Export Collection",
                subtitle = "Share your owned cards as a backup file",
                onClick = onExportClick
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            SettingsActionRow(
                icon = Icons.Default.Download,
                title = "Import Collection",
                subtitle = "Restore owned cards from a backup file",
                onClick = onImportClick
            )
        }
    }
}

@Composable
private fun SyncSettingsContent(
    state: SyncContract.State,
    onSyncClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SyncStatusDot(state.status)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = syncStatusLabel(state.status),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (state.lastSeededVersion != null) {
                Text(
                    text = "Version ${state.lastSeededVersion}" +
                        (state.lastSeededCount?.let { " · $it cards" } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            enabled = !state.isWorking,
            onClick = onSyncClick
        ) {
            if (state.isWorking) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Sync Now")
            }
        }
    }
}

@Composable
private fun SyncStatusDot(status: SyncStatus) {
    val color = when (status) {
        SyncStatus.IDLE -> MaterialTheme.colorScheme.onSurfaceVariant
        SyncStatus.WORKING -> MaterialTheme.colorScheme.primary
        SyncStatus.UP_TO_DATE -> MaterialTheme.colorScheme.tertiary
        SyncStatus.SEEDED -> MaterialTheme.colorScheme.primary
        SyncStatus.ERROR -> MaterialTheme.colorScheme.error
    }
    Surface(
        modifier = Modifier.size(10.dp),
        shape = CircleShape,
        color = color,
        content = {}
    )
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

private fun syncStatusLabel(status: SyncStatus) = when (status) {
    SyncStatus.IDLE -> "Ready to sync"
    SyncStatus.WORKING -> "Syncing..."
    SyncStatus.UP_TO_DATE -> "Up to date"
    SyncStatus.SEEDED -> "Database seeded"
    SyncStatus.ERROR -> "Sync failed"
}