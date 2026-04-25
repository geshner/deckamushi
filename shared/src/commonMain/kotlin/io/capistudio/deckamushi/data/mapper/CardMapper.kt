package io.capistudio.deckamushi.data.mapper

import io.capistudio.deckamushi.db.Cards
import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.model.toCardColors
import io.capistudio.deckamushi.domain.model.toCardType
import io.capistudio.deckamushi.domain.model.toRarity


object CardMapper {

    fun Cards.toCard() = Card(
        id = id,
        baseId = base_id,
        variant = variant,
        name = name,
        colors = color_flags.toInt().toCardColors(),
        rarity = rarity_id.toInt().toRarity(),
        cardType = card_category.toCardType(),
        attackPower = attack_power,
        counterPower = counter_power,
        life = life,
        combatAttribute = combat_attribute,
        feature = feature,
        cardText = card_text,
        blockIconCode = block_icon_code,
        packName = pack_name,
        imageUrl = image_url.orEmpty(),
    )
}