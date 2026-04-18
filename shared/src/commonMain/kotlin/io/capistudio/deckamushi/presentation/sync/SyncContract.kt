package io.capistudio.deckamushi.presentation.sync

object SyncContract {

    data class State(
        val isWorking: Boolean = false,
        val lastSeededVersion: String? = null,
        val lastSeededCount: Int? = null,
        val status: SyncStatus = SyncStatus.IDLE,
        val error: String? = null,
    )

    sealed interface Action {
        data object SyncClicked : Action
        data object GoToListClicked : Action
    }

    sealed interface Effect {
        data object NavigateToList : Effect
        data class ShowMessage(val message: String) : Effect
    }
}

enum class SyncStatus {
    IDLE, WORKING, UP_TO_DATE, SEEDED, ERROR
}