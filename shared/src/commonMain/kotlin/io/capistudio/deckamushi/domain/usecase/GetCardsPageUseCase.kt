package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.repository.CardRepository

class GetCardsPageUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(limit: Int, offset: Int): List<Card> =
        repository.getCardsPage(limit, offset)
}