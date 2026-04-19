package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.repository.CardRepository

class DecrementOwnedUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(cardId: String) {
        repository.decrementOwned(cardId)
    }
}