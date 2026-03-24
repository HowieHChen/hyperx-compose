package dev.lackluster.hyperx.compose.preference

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.compose.R
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.DrawableResIcon
import dev.lackluster.hyperx.compose.base.ImageIcon
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.SpinnerEntry
import top.yukonga.miuix.kmp.extra.SuperSpinner

@Composable
fun DropDownPreference(
    icon: ImageIcon? = null,
    title: String,
    summary: String? = null,
    entries: List<DropDownEntry>,
    key: String? = null,
    defValue: Int = 0,
    mode: DropDownMode = DropDownMode.Popup,
    showValue: Boolean = true,
    enabled: Boolean = true,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    onSelectedIndexChange: ((Int) -> Unit)? = null,
) {
    var spValue by remember {
        mutableIntStateOf(
            (key?.let { SafeSP.getInt(it, defValue) } ?: defValue).coerceIn(
                minimumValue = 0,
                maximumValue = entries.size - 1
            )
        )
    }
    val updatedOnSelectedIndexChange by rememberUpdatedState(onSelectedIndexChange)

    val wrappedEntries = entries.map { entry ->
        SpinnerEntry(
            icon = if (entry.hasIcon()) { imageModifier ->
                entry.RenderIcon(imageModifier)
            } else null,
            title = entry.title,
            summary = entry.summary,
        )
    }

    val handleSelectedIndexChange: (Int) -> Unit = { newValue ->
        spValue = newValue
        key?.let { SafeSP.putAny(it, newValue) }
        updatedOnSelectedIndexChange?.invoke(newValue)
    }

    val startAction: @Composable (() -> Unit)? = icon?.let { { DrawableResIcon(it) } }

    @Suppress("DEPRECATION")
    when (mode) {
        DropDownMode.Dialog -> {
            SuperSpinner(
                items = wrappedEntries,
                selectedIndex = spValue,
                title = title,
                dialogButtonString = stringResource(R.string.button_cancel),
                titleColor = titleColor,
                summary = summary,
                summaryColor = summaryColor,
                startAction = startAction,
                enabled = enabled,
                showValue = showValue,
                onSelectedIndexChange = handleSelectedIndexChange,
            )
        }

        else -> {
            SuperSpinner(
                items = wrappedEntries,
                selectedIndex = spValue,
                title = title,
                titleColor = titleColor,
                summary = summary,
                summaryColor = summaryColor,
                startAction = startAction,
                enabled = enabled,
                showValue = showValue,
                onSelectedIndexChange = handleSelectedIndexChange,
            )
        }
    }
}

data class DropDownEntry(
    val title: String? = null,
    val summary: String? = null,
    val iconRes: Int? = null,
    val iconBitmap: ImageBitmap? = null,
    val iconVector: ImageVector? = null,
    val iconTint: Color? = null
) {
    fun hasIcon(): Boolean = iconVector != null || iconRes != null || iconBitmap != null

    @Composable
    fun RenderIcon(modifier: Modifier) {
        iconVector?.let {
            Image(
                modifier = modifier,
                imageVector = it,
                contentDescription = null,
                colorFilter = iconTint?.let { tint -> ColorFilter.tint(tint) }
            )
        } ?: iconRes?.let {
            Image(
                modifier = modifier,
                painter = painterResource(it),
                contentDescription = null,
                colorFilter = iconTint?.let { tint -> ColorFilter.tint(tint) }
            )
        } ?: iconBitmap?.let {
            Image(
                modifier = modifier,
                bitmap = it,
                contentDescription = null,
                colorFilter = iconTint?.let { tint -> ColorFilter.tint(tint) }
            )
        }
    }
}

enum class DropDownMode {
    Popup,
    Dialog,
}
