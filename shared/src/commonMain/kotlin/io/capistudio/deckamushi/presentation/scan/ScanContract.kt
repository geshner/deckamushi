package io.capistudio.deckamushi.presentation.scan

object ScanContract {

    data class State(
        val isScanning: Boolean = false,
        val permissionGranted: Boolean = false,
        val isProcessing: Boolean = false,
        val lastRawText: String? = null
    )

    sealed interface Action {
        data object OnStart : Action
        data object BackClicked : Action
        data class OnPermissionResult(val granted: Boolean) : Action
        data class OnRawTextDetected(val text: String) : Action
    }

    sealed interface Effect {
        data class NavigateToDetail(val cardId: String) : Effect      // single variant
        data class NavigateToResults(val baseId: String) : Effect     // multiple variants
        data class ShowMessage(val text: String) : Effect             // not found
        data object RequestCameraPermission : Effect
    }
}