package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.model.OwnedCardExport
import io.capistudio.deckamushi.domain.repository.CardRepository
import io.capistudio.deckamushi.domain.util.DomainResult
import io.capistudio.deckamushi.domain.util.domainResult
import kotlinx.serialization.json.Json

class ExportCollectionUseCase(
    private val repository: CardRepository,
) {
    private val json = Json { prettyPrint = false }

    suspend operator fun invoke(): DomainResult<String> = domainResult {
        val owned = repository.getAllOwned()
            .map { (cardId, quantity) -> OwnedCardExport(cardId, quantity) }

        json.encodeToString(owned)
    }
}