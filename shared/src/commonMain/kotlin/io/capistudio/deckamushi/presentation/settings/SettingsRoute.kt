package io.capistudio.deckamushi.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.capistudio.deckamushi.presentation.components.CollectEffects
import io.capistudio.deckamushi.presentation.components.rememberFilePicker
import io.capistudio.deckamushi.presentation.components.rememberShareLauncher
import io.capistudio.deckamushi.presentation.settings.SettingsContract.Action
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoute(
    showSnackbar: (String) -> Unit,
) {
    val vm: SettingsViewModel = koinViewModel()
    val state by vm.state.collectAsState()

    val shareLauncher = rememberShareLauncher(
        onComplete = { showSnackbar("Collection exported successfully") }
    )

    val filePicker = rememberFilePicker { json ->
        vm.dispatch(Action.FilePickResult(json))
    }

    CollectEffects(vm.effects) { effect ->
        when (effect) {
            is SettingsContract.Effect.ShareCollection -> shareLauncher(effect.json)
            is SettingsContract.Effect.ShowMessage -> showSnackbar(effect.message)
        }
    }

    SettingsScreen(
        settingsState = state,
        onExportClick = { vm.dispatch(Action.ExportClick) },
        onSettingsAction = vm::dispatch,
        filePicker = filePicker,
    )
}