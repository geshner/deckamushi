package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.model.CardSummary
import io.capistudio.deckamushi.domain.repository.CardRepository
import io.capistudio.deckamushi.domain.util.DomainResult
import io.capistudio.deckamushi.domain.util.domainResult

class GetCardsByBaseIdUseCase(
    private val repository: CardRepository,
) {
    suspend operator fun invoke(baseId: String): DomainResult<List<CardSummary>> =
        domainResult {
            repository.getCardsByBaseId(baseId)
        }
}