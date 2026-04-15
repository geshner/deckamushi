package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.repository.CardRepository

class GetCardsCountUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(): Long = repository.getCardsCount()
}