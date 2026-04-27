package io.capistudio.deckamushi.presentation.settings

import io.capistudio.deckamushi.domain.usecase.ExportCollectionUseCase
import io.capistudio.deckamushi.domain.util.onFailure
import io.capistudio.deckamushi.domain.util.onSuccess
import io.capistudio.deckamushi.presentation.mvi.Mvi

class SettingsViewModel(
    private val exportCollectionUseCase: ExportCollectionUseCase,
) : Mvi<SettingsContract.State, SettingsContract.Action, SettingsContract.Effect>(
    initialState = SettingsContract.State()
) {
    override suspend fun handleAction(action: SettingsContract.Action) {
        when (action) {
            SettingsContract.Action.ExportClick -> exportCollection()
        }
    }

    private suspend fun exportCollection() {
        setState { copy(isExporting = true) }
        exportCollectionUseCase()
            .onSuccess { json ->
                setState { copy(isExporting = false) }
                emitEffect(SettingsContract.Effect.ShareCollection(json))
            }
            .onFailure { message, e ->
                setState { copy(isExporting = false) }
                emitEffect(SettingsContract.Effect.ShowMessage(message))
            }
    }
}