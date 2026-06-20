package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.domain.model.CartItemSummary
import io.capistudio.deckamushi.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow

class GetCartItemsUseCase(
    private val repository: CardRepository,
) {

    suspend operator fun invoke(): Flow<List<CartItemSummary>> {
        return repository.getCartItems()
    }
}