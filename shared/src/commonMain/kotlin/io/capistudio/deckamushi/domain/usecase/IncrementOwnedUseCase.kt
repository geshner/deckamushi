package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.repository.CardRepository
import io.capistudio.deckamushi.domain.util.DomainResult
import io.capistudio.deckamushi.domain.util.domainResult

class IncrementOwnedUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(cardId: String): DomainResult<Unit> {
        return domainResult {
            repository.incrementOwned(cardId)
        }
    }
}