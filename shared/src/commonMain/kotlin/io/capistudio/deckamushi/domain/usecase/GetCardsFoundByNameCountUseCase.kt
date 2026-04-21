package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.repository.CardRepository
import io.capistudio.deckamushi.domain.util.DomainResult
import io.capistudio.deckamushi.domain.util.domainResult

class GetCardsFoundByNameCountUseCase(
    private val repository: CardRepository
) {

    suspend operator fun invoke(query: String): DomainResult<Long> =
        domainResult {
            repository.searchCardsCount(query)
        }
}