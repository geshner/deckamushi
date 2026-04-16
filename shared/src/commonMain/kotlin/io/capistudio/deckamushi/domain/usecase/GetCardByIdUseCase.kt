package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.repository.CardRepository

class GetCardByIdUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(id: String): Card? =
        repository.getCardById(id)
}