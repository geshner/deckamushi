package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.model.OwnedCard
import io.capistudio.deckamushi.domain.repository.CardRepository

class GetOwnedCardsUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(limit: Int, offset: Int): List<OwnedCard> {
        return repository.getOwnedCards(limit, offset)
    }
}
