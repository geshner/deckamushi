package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.repository.CardRepository
import io.capistudio.deckamushi.domain.util.DomainResult
import io.capistudio.deckamushi.domain.util.domainResult

class GetCardByIdUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(id: String): DomainResult<Card?> =
        domainResult {
            repository.getCardById(id)
        }
}