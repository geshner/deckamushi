package io.capistudio.deckamushi.domain.repository

import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.domain.model.Card

interface CardRepository {
    suspend fun getCardsCount(): Long
    suspend fun getCardsPage(limit: Int, offset: Int): List<Card>
    suspend fun searchCardsCount(query: String): Long
    suspend fun searchCardsByName(query: String, limit: Int, offset: Int): List<Card>
}

class CardRepositoryImpl(
    private val dbProvider: AppDatabaseProvider,
) : CardRepository {
    val db = dbProvider.db

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
}