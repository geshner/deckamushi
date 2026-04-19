package io.capistudio.deckamushi.domain.repository

import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.model.OwnedCard

interface CardRepository {
    suspend fun getCardById(id: String): Card?
    suspend fun getCardsCount(): Long
    suspend fun getCardsPage(limit: Int, offset: Int): List<Card>
    suspend fun searchCardsCount(query: String): Long
    suspend fun searchCardsByName(query: String, limit: Int, offset: Int): List<Card>
    suspend fun getOwnedQuantity(cardId: String): Long
    suspend fun incrementOwned(cardId: String)
    suspend fun decrementOwned(cardId: String)
    suspend fun getOwnedCards(limit: Int, offset: Int): List<OwnedCard>
    suspend fun getOwnedTotal(): Long
}

class CardRepositoryImpl(
    private val dbProvider: AppDatabaseProvider,
) : CardRepository {
    val db = dbProvider.db

    override suspend fun getCardById(id: String): Card? {
        return db.cardQueries.getCardById(id).executeAsOneOrNull()
            ?.let { r ->
                Card(
                    id = r.id,
                    baseId = r.base_id,
                    variant = r.variant,
                    name = r.name,
                    imageUrl = r.image_url,
                )
            }
    }

    override suspend fun getCardsCount(): Long {
        return db.cardQueries.getCardsCount().executeAsOne()
    }

    override suspend fun getCardsPage(
        limit: Int,
        offset: Int
    ): List<Card> {
        return db.cardQueries
            .getCardsPage(limit = limit.toLong(), offset = offset.toLong())
            .executeAsList()
            .map { r ->
                Card(
                    id = r.id,
                    baseId = r.base_id,
                    variant = r.variant,
                    name = r.name,
                    imageUrl = r.image_url,
                )
            }
    }

    override suspend fun searchCardsCount(query: String): Long {
        return db.cardQueries.searchCardsCount(query).executeAsOne()
    }

    override suspend fun searchCardsByName(
        query: String,
        limit: Int,
        offset: Int
    ): List<Card> {
        return db.cardQueries
            .searchCardsByName(query, limit.toLong(), offset.toLong())
            .executeAsList()
            .map { r ->
                Card(
                    id = r.id,
                    baseId = r.base_id,
                    variant = r.variant,
                    name = r.name,
                    imageUrl = r.image_url,
                )
            }
    }

    override suspend fun getOwnedQuantity(cardId: String): Long {
        return db.collectionQueries
            .getQuantityByCardId(cardId)
            .executeAsOneOrNull()
            ?: 0L
    }

    override suspend fun incrementOwned(cardId: String) {
        db.collectionQueries.incrementQuantity(cardId)
    }

    override suspend fun decrementOwned(cardId: String) {
        db.collectionQueries.deleteIfQuantityIsOne(cardId)
        db.collectionQueries.decrementQuantity(cardId)
    }

    override suspend fun getOwnedCards(limit: Int, offset: Int): List<OwnedCard> {
        return db.cardQueries.getOwnedCards(limit.toLong(), offset.toLong())
            .executeAsList()
            .map { r ->
                OwnedCard(
                    id = r.id,
                    baseId = r.base_id,
                    variant = r.variant,
                    name = r.name,
                    imageUrl = r.image_url,
                    ownedQuantity = r.owned_quantity
                )
            }
    }

    override suspend fun getOwnedTotal(): Long {
        return db.collectionQueries.getOwnedTotal().executeAsOneOrNull() ?: 0L
    }
}