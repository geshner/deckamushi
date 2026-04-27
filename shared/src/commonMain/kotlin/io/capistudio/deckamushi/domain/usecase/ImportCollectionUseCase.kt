package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.model.OwnedCardExport
import io.capistudio.deckamushi.domain.repository.CardRepository
import io.capistudio.deckamushi.domain.util.DomainResult
import io.capistudio.deckamushi.domain.util.domainResult
import kotlinx.serialization.json.Json


enum class ImportMode { OVERWRITE, MERGE }

class ImportCollectionUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(
        json: String,
        mode: ImportMode,
    ): DomainResult<Int> = domainResult {
        val entries = Json.decodeFromString<List<OwnedCardExport>>(json)

        val unknownIds = entries.filter { !repository.cardExists(it.cardId) }
        if (unknownIds.isNotEmpty()) {
            DomainResult.Error("${unknownIds.size} card(s) not found in the database. Please sync first.")
        }

        repository.importOwned(entries, mode)
        entries.size
    }
}