package io.capistudio.deckamushi.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import deckamushi.shared.generated.resources.Res
import deckamushi.shared.generated.resources.swords_24px
import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.model.CardColor
import io.capistudio.deckamushi.domain.model.CardType
import io.capistudio.deckamushi.presentation.components.CardGridItem
import io.capistudio.deckamushi.presentation.components.ReprintBanner
import io.capistudio.deckamushi.presentation.detail.CardDetailContract.Action
import io.capistudio.deckamushi.presentation.theme.Dimensions.CARD_ASPECT_RATIO
import io.capistudio.deckamushi.presentation.theme.Dimensions.cardImageHeight
import org.jetbrains.compose.resources.painterResource

@Composable
fun CardDetailScreen(
    state: CardDetailContract.State,
    onAction: (Action) -> Unit,
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        state.card?.let { card ->
            CardDetailHeaderSection(card = card)
            OwnedSection(
                state.ownedQuantity,
                onDecrement = { onAction(Action.DecrementOwnedClick) },
                onIncrement = { onAction(Action.IncrementOwnedClick) }
            )

            if (card.cardType in arrayOf(CardType.LEADER, CardType.CHARACTER)) {
                CombatStatRow(card = card)
            }

            if (card.cardText != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "ABILITY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CardAbilityText(text = card.cardText)
                    }
                }
            }

            if (card.packName != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = card.packName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxWidth().padding(64.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            state.card == null -> Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Card Not Found",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "This card doesn't exist in the database.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
private fun CardDetailHeaderSection(card: Card) {
    CardGridItem(
        imageUrl = card.imageUrl,
        contentDescription = card.name,
        onClick = {},
        modifier = Modifier
            .height(cardImageHeight)
            .width(cardImageHeight * CARD_ASPECT_RATIO),
        overlay = {
            if (card.isReprint) {
                ReprintBanner(
                    originalCardBaseId = card.baseId,
                    compact = false,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                )
            }
        }
    )

    // Name
    Text(
        text = card.name.uppercase(),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center
    )

    //  Base ID Tag & Color Pips & Type  |  Feature Chip
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(4.dp),
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.baseId.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Color Pips
                card.colors.sortedBy { it.bit }.forEach { cardColor ->
                    val color = when (cardColor) {
                        CardColor.RED    -> Color.Red
                        CardColor.GREEN  -> Color.Green
                        CardColor.BLUE   -> Color.Blue
                        CardColor.PURPLE -> Color(0xFF993388)
                        CardColor.BLACK  -> Color.Black
                        CardColor.YELLOW -> Color.Yellow
                    }
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = card.cardType.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        if (card.feature != null) {
            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        text = card.feature.uppercase(),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                shape = CircleShape
            )
        }
    }
}

@Composable
private fun CombatStatRow(card: Card) {
    // Stats Row: Power, Counter, Life
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = painterResource(Res.drawable.swords_24px),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                label = "POWER", value = card.attackPower?.toString() ?: "—")
            VerticalDivider(
                modifier = Modifier.height(40.dp).width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            StatItem(
                icon = rememberVectorPainter(Icons.Default.Shield),
                tint = MaterialTheme.colorScheme.tertiary,
                label = "COUNTER", value = card.counterPower?.let { "+$it" } ?: "—")
            VerticalDivider(
                modifier = Modifier.height(40.dp).width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            StatItem(
                icon = rememberVectorPainter(Icons.Default.Favorite),
                tint = MaterialTheme.colorScheme.errorContainer,
                label = "LIFE", value = card.life?.toString() ?: "—")
        }
    }
}

@Composable
private fun StatItem(icon: Painter, tint: Color, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = tint
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun CardAbilityText(text: String) {
    val keywordColor = MaterialTheme.colorScheme.tertiary
    val parenColor = MaterialTheme.colorScheme.onSurfaceVariant

    val processed = text
        .replace("。【", "。\n\n【")
        .replace(")【", ")\n\n【")

    val annotated = buildAnnotatedString {
        val matches = (Regex("""【[^】]*】""").findAll(processed) + Regex("""\([^)]*\)""").findAll(processed))
            .sortedBy { it.range.first }

        var cursor = 0
        for (match in matches) {
            if (match.range.first > cursor) append(processed.substring(cursor, match.range.first))
            val style = if (match.value.startsWith("【"))
                SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold)
            else
                SpanStyle(color = parenColor)
            withStyle(style) { append(match.value) }
            cursor = match.range.last + 1
        }
        if (cursor < processed.length) append(processed.substring(cursor))
    }

    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun OwnedSection(ownedCount: Long, onDecrement: () -> Unit, onIncrement: () -> Unit) {
    // OWNED COPIES SECTION
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(
                text = "OWNED COPIES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(
                    enabled = ownedCount >= 1,
                    onClick = onDecrement,
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.tertiary,
                        CircleShape
                    )
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }

                Text(
                    text = "$ownedCount",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )

                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier.background(MaterialTheme.colorScheme.tertiary, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}