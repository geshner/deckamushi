package io.capistudio.deckamushi.domain.repository

import androidx.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.mapper.CardMapper.toCard
import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.model.CardSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Local data boundary for card catalog reads and owned-collection mutations.
 *
 * This repository currently exposes both card browsing operations and collection quantity changes
 * because both are backed by the same local SQLDelight database.
 */
interface CardRepository {
    suspend fun getCardById(id: String): Card?
    suspend fun getCardsCount(): Long
    suspend fun getCardsPage(limit: Int, offset: Int): List<CardSummary>
    suspend fun searchCardsCount(query: String): Long
    suspend fun searchCardsByName(query: String, limit: Int, offset: Int): List<CardSummary>
    suspend fun getOwnedQuantity(cardId: String): Long
    suspend fun incrementOwned(cardId: String)
    suspend fun decrementOwned(cardId: String)
    suspend fun getOwnedCards(limit: Int, offset: Int): List<CardSummary>
    suspend fun getOwnedTotal(): Long
    fun getOwnedCardsPagingSource(): PagingSource<Int, CardSummary>
    suspend fun getCardsByBaseId(baseId: String): List<CardSummary>
    suspend fun getAllOwned(): List<Pair<String, Long>>
}

/** SQLDelight-backed implementation of [CardRepository]. */
class CardRepositoryImpl(
    dbProvider: AppDatabaseProvider,
) : CardRepository {
    private val db = dbProvider.db

    override suspend fun getCardById(id: String): Card? {
        return db.cardQueries.getCardById(id).executeAsOneOrNull()?.toCard()
    }

    override suspend fun getCardsCount(): Long {
        return db.cardQueries.getCardsCount().executeAsOne()
    }

    override suspend fun getCardsPage(
        limit: Int,
        offset: Int
    ): List<CardSummary> {
        return db.cardQueries
            .getCardsPage(limit = limit.toLong(), offset = offset.toLong())
            .executeAsList()
            .map { r ->
                CardSummary(
                    id = r.id,
                    name = r.name,
                    variant = r.variant,
                    imageUrl = r.image_url.orEmpty(),
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
    ): List<CardSummary> {
        return db.cardQueries
            .searchCardsByName(query, limit.toLong(), offset.toLong())
            .executeAsList()
            .map { r ->
                CardSummary(
                    id = r.id,
                    variant = r.variant,
                    name = r.name,
                    imageUrl = r.image_url.orEmpty(),
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
        // Quantity 1 should remove the row entirely; larger quantities are decremented in place.
        db.collectionQueries.deleteIfQuantityIsOne(cardId)
        db.collectionQueries.decrementQuantity(cardId)
    }

    override suspend fun getOwnedCards(limit: Int, offset: Int): List<CardSummary> {
        return db.cardQueries.getOwnedCards(limit.toLong(), offset.toLong())
            .executeAsList()
            .map { r ->
                CardSummary(
                    id = r.id,
                    variant = r.variant,
                    name = r.name,
                    imageUrl = r.image_url.orEmpty(),
                    ownedCount = r.owned_count
                )
            }
    }

    override suspend fun getOwnedTotal(): Long {
        return db.collectionQueries.getOwnedTotal().executeAsOneOrNull() ?: 0L
    }

    override fun getOwnedCardsPagingSource(): PagingSource<Int, CardSummary> {
        // Paging is backed directly by SQLDelight queries so collection screens can load lazily
        // without materializing the full owned-card list in memory.
        return QueryPagingSource(
            countQuery = db.collectionQueries.getOwnedTotal(),
            transacter = db.collectionQueries,
            context = Dispatchers.IO,
            queryProvider = { limit, offset ->
                db.cardQueries.getOwnedCards(
                    limit = limit,
                    offset = offset,
                    mapper = { id: String, name: String, imageUrl: String?, variant: String?, ownedCount: Long ->
                        CardSummary(
                            id = id,
                            variant = variant,
                            name = name,
                            imageUrl = imageUrl.orEmpty(),
                            ownedCount = ownedCount
                        )
                    }
                )
            }
        )
    }

    override suspend fun getCardsByBaseId(baseId: String): List<CardSummary> {
        // Scanner flow works from base id first, then lets the user choose a specific variant.
        return db.cardQueries.getCardsByBaseId(baseId)
            .executeAsList()
            .map { r ->
                CardSummary(
                    id = r.id,
                    name = r.name,
                    variant = r.variant,
                    imageUrl = r.image_url.orEmpty(),
                    ownedCount = r.owned_count
                )
            }
    }

    override suspend fun getAllOwned(): List<Pair<String, Long>> {
        return db.collectionQueries
            .getAllOwned()
            .executeAsList()
            .map { Pair(it.card_id, it.quantity) }
    }
}