package io.capistudio.deckamushi.di

import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.cards.CardsBrowserViewModel

data class AppDependencies(
    val updateCardDataUseCase: UpdateCardDataUseCase,
    val cardsBrowserViewModel: CardsBrowserViewModel,
)