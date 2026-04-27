package io.capistudio.deckamushi.presentation.settings

object SettingsContract {
    data class State(
        val isExporting: Boolean = false,
    )

    sealed interface Action {
        data  object ExportClick : Action
    }

    sealed interface Effect {
        data class ShareCollection(val json: String) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}