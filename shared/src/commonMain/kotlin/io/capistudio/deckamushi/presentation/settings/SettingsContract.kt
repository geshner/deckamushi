package io.capistudio.deckamushi.presentation.settings

import io.capistudio.deckamushi.domain.usecase.ImportMode

enum class SyncStatus { IDLE, WORKING, UP_TO_DATE, SEEDED, ERROR }

object SettingsContract {
    data class State(
        val isExporting: Boolean = false,
        val isImporting: Boolean = false,
        val showImportDialog: Boolean = false,
        val pendingImportJson: String? = null,
        val isSyncing: Boolean = false,
        val syncStatus: SyncStatus = SyncStatus.IDLE,
        val lastSyncVersion: String? = null,
        val lastSyncCount: Int? = null,
    )

    sealed interface Action {
        data object ExportClick : Action
        data object ImportCancelled : Action
        data class FilePickResult(val json: String) : Action
        data class ImportConfirmed(val mode: ImportMode) : Action
        data object SyncClick : Action
    }

    sealed interface Effect {
        data class ShareCollection(val json: String) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}
