package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.repository.CardRepository

class GetCartTotalCountUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(): Long {
        return repository.getCartTotalCount()
    }
}