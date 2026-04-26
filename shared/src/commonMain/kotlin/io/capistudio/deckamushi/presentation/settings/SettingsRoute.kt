package io.capistudio.deckamushi.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.capistudio.deckamushi.presentation.components.CollectEffects
import io.capistudio.deckamushi.presentation.sync.SyncContract
import io.capistudio.deckamushi.presentation.sync.SyncViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoute(
    showSnackbar: (String) -> Unit,
) {
    val syncVm: SyncViewModel = koinViewModel()
    val syncState by syncVm.state.collectAsState()

    CollectEffects(syncVm.effects) { effect ->
        when (effect) {
            is SyncContract.Effect.ShowMessage -> showSnackbar(effect.message)
            else -> Unit
        }
    }

    SettingsScreen(
        syncState = syncState,
        onSyncAction = syncVm::dispatch,
        onExportClick = { /* Step 4 */ },
        onImportClick = { /* Step 5 */ },
    )
}
