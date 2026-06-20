package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.repository.CardRepository

class ClearCartUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke() {
        repository.clearCart()
    }
}