package io.capistudio.deckamushi.domain.repository

import androidx.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.model.OwnedCard
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
    suspend fun getCardsPage(limit: Int, offset: Int): List<Card>
    suspend fun searchCardsCount(query: String): Long
    suspend fun searchCardsByName(query: String, limit: Int, offset: Int): List<Card>
    suspend fun getOwnedQuantity(cardId: String): Long
    suspend fun incrementOwned(cardId: String)
    suspend fun decrementOwned(cardId: String)
    suspend fun getOwnedCards(limit: Int, offset: Int): List<OwnedCard>
    suspend fun getOwnedTotal(): Long
    fun getOwnedCardsPagingSource(): PagingSource<Int, OwnedCard>
    suspend fun getCardsByBaseId(baseId: String): List<Card>
}

/** SQLDelight-backed implementation of [CardRepository]. */
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
        // Quantity 1 should remove the row entirely; larger quantities are decremented in place.
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

    override fun getOwnedCardsPagingSource(): PagingSource<Int, OwnedCard> {
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
                    mapper = { id: String, baseId: String, variant: String?, name: String, color_flags: Long, rarity_id: Long, card_category: String, attack_power: Long?, counter_power: Long?, life: Long?, combat_attribute: String?, feature: String?, card_text: String?, block_icon_code: String?, pack_name: String?, imageUrl: String?, ownedQuantity: Long ->
                        OwnedCard(
                            id = id,
                            baseId = baseId,
                            variant = variant,
                            name = name,
                            imageUrl = imageUrl,
                            ownedQuantity = ownedQuantity
                        )
                    }
                )
            }
        )
    }

    override suspend fun getCardsByBaseId(baseId: String): List<Card> {
        // Scanner flow works from base id first, then lets the user choose a specific variant.
        val results = db.cardQueries.getCardsByBaseId(baseId)
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

        return results
    }
}