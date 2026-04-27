package io.capistudio.deckamushi.presentation.settings

import io.capistudio.deckamushi.domain.usecase.ExportCollectionUseCase
import io.capistudio.deckamushi.domain.usecase.ImportCollectionUseCase
import io.capistudio.deckamushi.domain.usecase.ImportMode
import io.capistudio.deckamushi.domain.util.onFailure
import io.capistudio.deckamushi.domain.util.onSuccess
import io.capistudio.deckamushi.presentation.mvi.Mvi
import io.capistudio.deckamushi.presentation.settings.SettingsContract.Effect

class SettingsViewModel(
    private val exportCollectionUseCase: ExportCollectionUseCase,
    private val importCollectionUseCase: ImportCollectionUseCase,
) : Mvi<SettingsContract.State, SettingsContract.Action, SettingsContract.Effect>(
    initialState = SettingsContract.State()
) {
    override suspend fun handleAction(action: SettingsContract.Action) {
        when (action) {
            SettingsContract.Action.ExportClick -> exportCollection()
            is SettingsContract.Action.FilePickResult -> setState {
                copy(
                    pendingImportJson = action.json,
                    showImportDialog = true
                )
            }

            is SettingsContract.Action.ImportConfirmed -> importCollection(action.mode)

            SettingsContract.Action.ImportCancelled -> setState {
                copy(
                    showImportDialog = false,
                    pendingImportJson = null
                )
            }
        }

    }

    private suspend fun exportCollection() {
        setState { copy(isExporting = true) }
        exportCollectionUseCase().onSuccess { json ->
                setState { copy(isExporting = false) }
                emitEffect(SettingsContract.Effect.ShareCollection(json))
            }.onFailure { message, e ->
                setState { copy(isExporting = false) }
                emitEffect(SettingsContract.Effect.ShowMessage(message))
            }
    }

    private suspend fun importCollection(mode: ImportMode) {
        val json = state.value.pendingImportJson ?: return
        setState { copy(
            showImportDialog = false,
            pendingImportJson = null,
            isImporting = true
        )}

        importCollectionUseCase(json, mode)
            .onSuccess { count ->
                setState { copy(isImporting = false) }
                emitEffect(SettingsContract.Effect.ShowMessage("$count cards imported successfully"))
            }
            .onFailure { message, e ->
                setState { copy(isImporting = false) }
                emitEffect(Effect.ShowMessage(message))
            }
    }

}