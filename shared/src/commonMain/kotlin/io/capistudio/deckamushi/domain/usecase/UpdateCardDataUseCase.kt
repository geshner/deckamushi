package io.capistudio.deckamushi.domain.usecase

import io.capistudio.deckamushi.data.local.VersionCache
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.mapper.CardDtoMapper.toDbModel
import io.capistudio.deckamushi.data.remote.CardDataApi
import io.capistudio.deckamushi.data.remote.RemoteResult

/**
 * Synchronizes remote card metadata into the local SQLDelight database.
 *
 * Current behavior is intentionally non-destructive:
 * - `version.json` is checked first using a cached ETag
 * - `cardsVersion` is used as an additional logical safety check
 * - `cards.json` is fetched only when needed
 * - rows are written with `INSERT OR REPLACE`
 * - no pre-sync delete/wipe step is performed
 */
class UpdateCardDataUseCase(
    private val api: CardDataApi,
    private val cache: VersionCache,
    private val dbProvider: AppDatabaseProvider,
) {

    sealed interface Result {
        data object UpToDate: Result
        data class Seeded(val insertedOrReplaced: Int, val cardsVersion: String) : Result
        data class Error(val message: String) : Result
    }

    /**
     * Executes the current sync policy.
     *
     * A result of [Result.Seeded] means rows were inserted/replaced, not that the whole table was
     * wiped and reseeded from scratch.
     */
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

                // Extra safety: even if the server returned a fresh version payload, we still avoid
                // fetching/writing cards when the logical cards version is unchanged.
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
                            // Insert-or-replace keeps existing ids updated while leaving rows absent
                            // from the new payload untouched. Current sync does not delete cards.
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