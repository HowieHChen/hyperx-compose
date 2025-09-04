package dev.lackluster.hyperx.compose.base

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.compose.theme.DisabledAlpha
import dev.lackluster.hyperx.compose.theme.contentColorFor
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape

/**
 * Cards contain contain content and actions that relate information about a subject. Filled cards
 * provide subtle separation from the background. This has less emphasis than elevated or outlined
 * cards.
 *
 * This Card does not handle input events - see the other Card overloads if you want a clickable or
 * selectable Card.
 *
 * @param modifier the [Modifier] to be applied to this card
 * @param shape defines the shape of this card's container, border (when [border] is not null)
 * @param colors [CardColors] that will be used to resolve the colors used for this card in
 *   different states. See [CardDefaults.cardColors].
 * @param border the border to draw around the container of this card
 * @param content The content displayed on the card
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = CardDefaults.contentPaddingZero,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = colors.containerColor(enabled = true),
        contentColor = colors.contentColor(enabled = true),
        border = border,
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

/**
 * Cards contain contain content and actions that relate information about a subject. Filled cards
 * provide subtle separation from the background. This has less emphasis than elevated or outlined
 * cards.
 *
 * This Card handles click events, calling its [onClick] lambda.
 *
 * @param onClick called when this card is clicked
 * @param modifier the [Modifier] to be applied to this card
 * @param enabled controls the enabled state of this card. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param shape defines the shape of this card's container, border (when [border] is not null)
 * @param colors [CardColors] that will be used to resolve the color(s) used for this card in
 *   different states. See [CardDefaults.cardColors].
 * @param border the border to draw around the container of this card
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this card. You can use this to change the card's appearance or
 *   preview the card in different states. Note that if `null` is provided, interactions will still
 *   happen internally.
 * @param content The content displayed on the card
 */
@Composable
fun Card(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    contentPadding: PaddingValues = CardDefaults.contentPaddingZero,
    content: @Composable ColumnScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        color = colors.containerColor(enabled),
        contentColor = colors.contentColor(enabled),
        border = border,
        interactionSource = interactionSource,
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

/** Contains the default values used by all card types. */
object CardDefaults {
    // shape Defaults
    /** Default shape for a card. */
    val shape: Shape
        @Composable get() = G2RoundedCornerShape(16.dp)

    val contentPaddingZero: PaddingValues = PaddingValues.Zero

    val contentPadding: PaddingValues = PaddingValues(16.dp)

    /**
     * Creates a [CardColors] that represents the default container and content colors used in a
     * [Card].
     */
    @Composable fun cardColors() =
        defaultCardColorsCached
            ?: CardColors(
                containerColor = MiuixTheme.colorScheme.surface,
                contentColor = contentColorFor(MiuixTheme.colorScheme.surface),
                disabledContainerColor = MiuixTheme.colorScheme.surface
                        .copy(alpha = DisabledAlpha)
                        .compositeOver(MiuixTheme.colorScheme.surface),
                disabledContentColor =
                    contentColorFor(MiuixTheme.colorScheme.surface)
                        .copy(DisabledAlpha),
            )
                .also { defaultCardColorsCached = it }

    /**
     * Creates a [CardColors] that represents the default container and content colors used in a
     * [Card].
     *
     * @param containerColor the container color of this [Card] when enabled.
     * @param contentColor the content color of this [Card] when enabled.
     * @param disabledContainerColor the container color of this [Card] when not enabled.
     * @param disabledContentColor the content color of this [Card] when not enabled.
     */
    @Composable
    fun cardColors(
        containerColor: Color = Color.Unspecified,
        contentColor: Color = contentColorFor(containerColor),
        disabledContainerColor: Color = Color.Unspecified,
        disabledContentColor: Color = contentColor.copy(DisabledAlpha),
    ): CardColors =
        cardColors().copy(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    internal var defaultCardColorsCached: CardColors? = null
}

/**
 * Represents the container and content colors used in a card in different states.
 *
 * @param containerColor the container color of this [Card] when enabled.
 * @param contentColor the content color of this [Card] when enabled.
 * @param disabledContainerColor the container color of this [Card] when not enabled.
 * @param disabledContentColor the content color of this [Card] when not enabled.
 * @constructor create an instance with arbitrary colors.
 * - See [CardDefaults.cardColors] for the default colors used in a [Card].
 */
@Immutable
class CardColors
constructor(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
) {
    /**
     * Returns a copy of this CardColors, optionally overriding some of the values. This uses the
     * Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        containerColor: Color = this.containerColor,
        contentColor: Color = this.contentColor,
        disabledContainerColor: Color = this.disabledContainerColor,
        disabledContentColor: Color = this.disabledContentColor,
    ) =
        CardColors(
            containerColor.takeOrElse { this.containerColor },
            contentColor.takeOrElse { this.contentColor },
            disabledContainerColor.takeOrElse { this.disabledContainerColor },
            disabledContentColor.takeOrElse { this.disabledContentColor },
        )

    /**
     * Represents the container color for this card, depending on [enabled].
     *
     * @param enabled whether the card is enabled
     */
    @Stable
    internal fun containerColor(enabled: Boolean): Color =
        if (enabled) containerColor else disabledContainerColor

    /**
     * Represents the content color for this card, depending on [enabled].
     *
     * @param enabled whether the card is enabled
     */
    @Stable
    internal fun contentColor(enabled: Boolean) =
        if (enabled) contentColor else disabledContentColor

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CardColors) return false

        if (containerColor != other.containerColor) return false
        if (contentColor != other.contentColor) return false
        if (disabledContainerColor != other.disabledContainerColor) return false
        if (disabledContentColor != other.disabledContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + contentColor.hashCode()
        result = 31 * result + disabledContainerColor.hashCode()
        result = 31 * result + disabledContentColor.hashCode()
        return result
    }
}