package io.capistudio.deckamushi.domain.usecase

import androidx.compose.ui.geometry.Offset
import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.repository.CardRepository

class SearchCardByNameUseCase(
    private val repository: CardRepository
) {

    suspend operator fun invoke(
        query: String,
        limit: Int,
        offset: Int,
    ) : List<Card> =
        repository.searchCardsByName(query, limit, offset)
}