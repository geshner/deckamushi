package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.repository.CardRepository
import io.capistudio.deckamushi.domain.util.DomainResult
import io.capistudio.deckamushi.domain.util.domainResult

class GetCardsCountUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(): DomainResult<Long> {
        return domainResult { repository.getCardsCount() }
    }
}