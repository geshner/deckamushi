package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.data.local.VersionCache
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.mapper.CardDtoMapper.toDbModel
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.data.remote.RemoteResult

class UpdateCardDataUseCase(
    private val api: DeckamushiDataApi,
    private val cache: VersionCache,
    private val dbProvider: AppDatabaseProvider,
) {

    sealed interface Result {
        data object UpToDate: Result
        data class Seeded(val insertedOrReplaced: Int, val cardsVersion: String) : Result
        data class Error(val message: String) : Result
    }

    suspend fun run(): Result {
        val cachedETag = cache.getVersionETag()
        val cachedCardsVersion = cache.getCardsVersion()

        return when (val versionResult = api.fetchVersion(cachedETag)) {
            is RemoteResult.NotModified -> Result.UpToDate
            is RemoteResult.HttpError -> Result.Error("Version HTTP ${versionResult.code}: ${versionResult.message}")
            is RemoteResult.NetworkError -> Result.Error(versionResult.message)
            is RemoteResult.Success -> {
                val newCardsVersion = versionResult.data.cardsVersion
                cache.setVersionETag(versionResult.eTag)

                //Extra safety
                if (newCardsVersion.isNotBlank() && newCardsVersion == cachedCardsVersion) {
                    return Result.UpToDate
                }

                when (val cardsResult = api.fetchCards()) {
                    is RemoteResult.HttpError -> Result.Error("Cards HTTP ${cardsResult.code}: ${cardsResult.message}")
                    is RemoteResult.NetworkError -> Result.Error(cardsResult.message)
                    is RemoteResult.NotModified -> Result.UpToDate //should not happen
                    is RemoteResult.Success -> {
                        val rows = cardsResult.data.map { it.toDbModel() }

                        dbProvider.db.transaction {
                            for (r in rows) {
                                dbProvider.db.cardQueries.insertCard(
                                    id = r.id,
                                    base_id = r.baseId,
                                    variant = r.variant,
                                    name = r.name,
                                    color_flags = r.colorFlags.toLong(),
                                    rarity_id = r.rarityId.toLong(),
                                    card_category = r.cardCategory,
                                    attack_power = r.attackPower?.toLong(),
                                    counter_power = r.counterPower?.toLong(),
                                    life = r.life?.toLong(),
                                    combat_attribute = r.combatAttribute,
                                    feature = r.feature,
                                    card_text = r.cardText,
                                    block_icon_code = r.blockIconCode,
                                    pack_name = r.packName,
                                    image_url = r.imageUrl,
                                )
                            }
                        }
                        cache.setCardsVersion(newCardsVersion)
                        Result.Seeded(rows.size, cardsVersion = newCardsVersion)
                    }
                }
            }
        }
    }
}