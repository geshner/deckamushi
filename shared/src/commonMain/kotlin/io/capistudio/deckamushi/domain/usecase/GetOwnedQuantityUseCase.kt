package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.repository.CardRepository

class GetOwnedQuantityUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(cardId: String): Long = repository.getOwnedQuantity(cardId)
}