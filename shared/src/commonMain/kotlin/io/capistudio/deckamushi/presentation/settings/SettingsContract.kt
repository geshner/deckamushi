package io.capistudio.deckamushi.presentation.settings

import io.capistudio.deckamushi.domain.usecase.ImportMode

object SettingsContract {
    data class State(
        val isExporting: Boolean = false,
        val isImporting: Boolean = false,
        val showImportDialog: Boolean = false,
        val pendingImportJson: String? = null,
    )

    sealed interface Action {
        data  object ExportClick : Action
        data object ImportCancelled : Action
        data class FilePickResult(val json: String) : Action
        data class ImportConfirmed(val mode: ImportMode) : Action
    }

    sealed interface Effect {
        data class ShareCollection(val json: String) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}