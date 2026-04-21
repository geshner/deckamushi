package io.capistudio.deckamushi.presentation.cards

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.touchlab.kermit.Logger
import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.usecase.GetCardsCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsFoundByNameCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsPageUseCase
import io.capistudio.deckamushi.domain.util.onSuccess
import io.capistudio.deckamushi.presentation.cards.CardsListContract.Action
import io.capistudio.deckamushi.presentation.cards.CardsListContract.Effect
import io.capistudio.deckamushi.presentation.cards.CardsListContract.Effect.NavigateToDetail
import io.capistudio.deckamushi.presentation.cards.CardsListContract.State
import io.capistudio.deckamushi.presentation.mvi.Mvi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CardListViewModel(
    private val getCardsPageUseCase: GetCardsPageUseCase,
    private val getCardsCountUseCase: GetCardsCountUseCase,
    private val getCardsFoundByNameCountUseCase: GetCardsFoundByNameCountUseCase,
) : Mvi<State, Action, Effect>(
    initialState = State(),
) {
    private val log = Logger.withTag("CardsBrowserVM")
    private val _query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val cardsPagingData: Flow<PagingData<Card>> = _query
        .debounce(300)
        .flatMapLatest { query -> getCardsPageUseCase(query) }
        .cachedIn(viewModelScope)

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.OnStart -> updateTotalCount("")
            is Action.QueryChanged -> {
                setState { copy(queryDraft = action.value) }
            }

            Action.SearchClicked -> {
                val query = state.value.queryDraft.trim()
                _query.value = query
                updateTotalCount(query)
            }

            is Action.CardClicked -> emitEffect(NavigateToDetail(action.id))
            is Action.ScrollPositionChanged -> setState {
                copy(
                    scrollIndex = action.index,
                    scrollOffset = action.offset
                )
            }
        }
    }

    private fun updateTotalCount(query: String) {
        viewModelScope.launch {
            var count = 0L
            if (query.isBlank()) {
                getCardsCountUseCase()
                    .onSuccess { count = it }
            } else {
                getCardsFoundByNameCountUseCase(query)
                    .onSuccess { count = it }
            }

            setState { copy(totalCount = count) }
        }
    }
}