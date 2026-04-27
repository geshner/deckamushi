package io.capistudio.deckamushi.presentation.settings

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.capistudio.deckamushi.domain.usecase.ExportCollectionUseCase
import io.capistudio.deckamushi.domain.usecase.ImportCollectionUseCase
import io.capistudio.deckamushi.domain.usecase.ImportMode
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.domain.util.onFailure
import io.capistudio.deckamushi.domain.util.onSuccess
import io.capistudio.deckamushi.presentation.mvi.Mvi
import io.capistudio.deckamushi.presentation.settings.SettingsContract.Effect
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val exportCollectionUseCase: ExportCollectionUseCase,
    private val importCollectionUseCase: ImportCollectionUseCase,
    private val updateCardDataUseCase: UpdateCardDataUseCase,
) : Mvi<SettingsContract.State, SettingsContract.Action, Effect>(
    initialState = SettingsContract.State()
) {
    private val log = Logger.withTag("SettingsVM")

    override suspend fun handleAction(action: SettingsContract.Action) {
        when (action) {
            SettingsContract.Action.ExportClick -> exportCollection()
            is SettingsContract.Action.FilePickResult -> setState {
                copy(pendingImportJson = action.json, showImportDialog = true)
            }
            is SettingsContract.Action.ImportConfirmed -> importCollection(action.mode)
            SettingsContract.Action.ImportCancelled -> setState {
                copy(showImportDialog = false, pendingImportJson = null)
            }
            SettingsContract.Action.SyncClick -> sync()
        }
    }

    private suspend fun exportCollection() {
        setState { copy(isExporting = true) }
        exportCollectionUseCase()
            .onSuccess { json ->
                setState { copy(isExporting = false) }
                emitEffect(Effect.ShareCollection(json))
            }
            .onFailure { message, _ ->
                setState { copy(isExporting = false) }
                emitEffect(Effect.ShowMessage(message))
            }
    }

    private suspend fun importCollection(mode: ImportMode) {
        val json = state.value.pendingImportJson ?: run {
            setState { copy(showImportDialog = false) }
            return
        }
        setState { copy(showImportDialog = false, pendingImportJson = null, isImporting = true) }
        importCollectionUseCase(json,
            mode)
            .onSuccess { count ->
                setState { copy(isImporting = false) }
                emitEffect(Effect.ShowMessage("$count cards imported successfully"))
            }
            .onFailure { message, _ ->
                setState { copy(isImporting = false) }
                emitEffect(Effect.ShowMessage(message))
            }
    }

    private fun sync() {
        if (state.value.isSyncing) return
        setState { copy(isSyncing = true) }
        viewModelScope.launch {
            runCatching {
                when (val result = updateCardDataUseCase.run()) {
                    is UpdateCardDataUseCase.Result.UpToDate ->
                        setState { copy(isSyncing = false, syncStatus = SyncStatus.UP_TO_DATE) }
                    is UpdateCardDataUseCase.Result.Seeded ->
                        setState {
                            copy(
                                isSyncing = false,
                                syncStatus = SyncStatus.SEEDED,
                                lastSyncVersion = result.cardsVersion,
                                lastSyncCount = result.insertedOrReplaced,
                            )
                        }
                    is UpdateCardDataUseCase.Result.Error ->
                        setState { copy(isSyncing = false, syncStatus = SyncStatus.ERROR) }
                }
            }.onFailure { e ->
                log.e(e) { "sync failed" }
                setState { copy(isSyncing = false, syncStatus = SyncStatus.ERROR) }
            }
        }
    }
}