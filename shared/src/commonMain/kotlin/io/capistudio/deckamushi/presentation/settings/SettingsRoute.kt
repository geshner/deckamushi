package io.capistudio.deckamushi.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.capistudio.deckamushi.presentation.components.CollectEffects
import io.capistudio.deckamushi.presentation.components.rememberShareLauncher
import io.capistudio.deckamushi.presentation.sync.SyncContract
import io.capistudio.deckamushi.presentation.sync.SyncViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoute(
    showSnackbar: (String) -> Unit,
) {
    val syncVm: SyncViewModel = koinViewModel()
    val syncState by syncVm.state.collectAsState()

    val settingsVm: SettingsViewModel = koinViewModel()
    val settingState by settingsVm.state.collectAsState()

    val shareLauncher = rememberShareLauncher(
        onComplete = { showSnackbar("Collection exported successfully") }
    )

    CollectEffects(syncVm.effects) { effect ->
        when (effect) {
            is SyncContract.Effect.ShowMessage -> showSnackbar(effect.message)
            else -> Unit
        }
    }

    CollectEffects(settingsVm.effects) { effect ->
        when (effect) {
            is SettingsContract.Effect.ShareCollection -> {
                shareLauncher(effect.json)
            }
            is SettingsContract.Effect.ShowMessage -> {
                showSnackbar(effect.message)
            }
        }
    }

    SettingsScreen(
        syncState = syncState,
        onSyncAction = syncVm::dispatch,
        onExportClick = {
            settingsVm.dispatch(SettingsContract.Action.ExportClick)
        },
        onImportClick = { /* Step 5 */ },
    )
}
