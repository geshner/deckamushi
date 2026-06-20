package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.repository.CardRepository

class SetCartQuantityUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(cardId: String, amount: Long) {
        repository.setCartQuantity(cardId, amount)
    }
}