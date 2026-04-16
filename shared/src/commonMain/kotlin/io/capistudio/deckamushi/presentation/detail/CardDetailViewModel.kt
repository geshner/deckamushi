package io.capistudio.deckamushi.presentation.detail

import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.usecase.GetCardByIdUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CardDetailState(
    val isLoading: Boolean = false,
    val card: Card? = null,
    val error: String? = null,
)


class CardDetailViewModel(
    private val cardId: String,
    private val getCardByIdUseCase: GetCardByIdUseCase,
    private val scope: CoroutineScope = MainScope(),
) {
    private val _state = MutableStateFlow(CardDetailState())
    val state: StateFlow<CardDetailState> = _state

    fun load() {
        scope.launch {
            _state.value = CardDetailState(isLoading = true)
            try {
                val card = getCardByIdUseCase(cardId)
                _state.value = CardDetailState(isLoading = false, card = card)
            } catch (t: Throwable) {
                _state.value = CardDetailState(isLoading = false, error = t.message ?: "Unknown error")
            }
        }
    }

    fun clear() {
        scope.cancel()
    }
}