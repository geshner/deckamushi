package io.capistudio.deckamushi.presentation.sync

import co.touchlab.kermit.Logger
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.mvi.Mvi
import io.capistudio.deckamushi.presentation.sync.SyncContract.Effect
import io.capistudio.deckamushi.presentation.sync.SyncContract.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SyncViewModel(
    private val updateCardDataUseCase: UpdateCardDataUseCase,
    scope: CoroutineScope = MainScope()
) : Mvi<State, SyncContract.Action, Effect>(
    initialState = State(),
    scope = scope
) {
    private val log = Logger.withTag("SyncVM")

    override suspend fun handleAction(action: SyncContract.Action) {
        when (action) {
            SyncContract.Action.GoToListClicked -> emitEffect(Effect.NavigateToList)
            SyncContract.Action.SyncClicked -> sync()
        }
    }

    private suspend fun sync() {
        if (state.value.isWorking) return

        setState { copy(isWorking = true) }

        scope.launch {
            runCatching {
                when (val result = updateCardDataUseCase.run()) {
                    is UpdateCardDataUseCase.Result.UpToDate -> {
                        setState { copy(isWorking = false, status = SyncStatus.UP_TO_DATE) }
                    }

                    is UpdateCardDataUseCase.Result.Seeded -> {
                        setState {
                            copy(
                                isWorking = false,
                                status = SyncStatus.SEEDED,
                                lastSeededVersion = result.cardsVersion,
                                lastSeededCount = result.insertedOrReplaced,
                            )
                        }
                    }

                    is UpdateCardDataUseCase.Result.Error -> {
                        setState {
                            copy(
                                isWorking = false,
                                status = SyncStatus.ERROR,
                                error = "Error: ${result.message}"
                            )
                        }
                    }
                }
            }.onFailure { e ->
                log.e(e) { "sync failed" }
                setState {
                    copy(
                        isWorking = false,
                        status = SyncStatus.ERROR,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }
}
