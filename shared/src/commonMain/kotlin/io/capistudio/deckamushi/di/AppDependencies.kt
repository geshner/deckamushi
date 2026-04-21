package io.capistudio.deckamushi.di

import io.capistudio.deckamushi.domain.usecase.GetCardByIdUseCase
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.cards.CardListViewModel

data class AppDependencies(
    val updateCardDataUseCase: UpdateCardDataUseCase,
    val cardListViewModel: CardListViewModel,
    val getCardByIdUseCase: GetCardByIdUseCase
)