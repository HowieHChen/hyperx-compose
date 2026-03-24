package dev.lackluster.hyperx.compose.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.compose.R
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.DrawableResIcon
import dev.lackluster.hyperx.compose.base.ImageIcon
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SeekBarPreference(
    icon: ImageIcon? = null,
    title: String,
    key: String? = null,
    defValue: Int = 0,
    min: Int = 0,
    max: Int = 1,
    showValue: Boolean = true,
    format: String = "%d",
    enabled: Boolean = true,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    onValueChange: ((Int) -> Unit)? = null,
) {
    var spValue by remember {
        mutableIntStateOf(
            key?.let { SafeSP.getInt(it, defValue) } ?: defValue
        )
    }
    val updatedOnValueChange by rememberUpdatedState(onValueChange)
    val dialogVisibility = remember { mutableStateOf(false) }
    val dialogHoldDown = remember { mutableStateOf(false) }

    val doOnInputConfirm: (String) -> Unit = { newString: String ->
        val oldValue = spValue
        val newValue = newString.toIntOrNull()
        if (newValue != null && newValue in min..max && oldValue != newValue) {
            spValue = newValue
            key?.let { SafeSP.putAny(it, newValue) }
            updatedOnValueChange?.let { it(newValue) }
        }
    }

    SuperArrow(
        title = title,
        titleColor = titleColor,
        startAction = icon?.let { { DrawableResIcon(it) } },
        endActions = {
            if (showValue) {
                Text(
                    text = String.format(Locale.current.platformLocale, format, spValue),
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                )
            }
        },
        onClick = {
            if (enabled) {
                dialogVisibility.value = true
                dialogHoldDown.value = true
            }
        },
        holdDownState = dialogHoldDown.value,
        bottomAction = {
            Slider(
                value = spValue.toFloat(),
                onValueChange = { newValue ->
                    val newInt = newValue.toInt()
                    spValue = newInt
                    key?.let { SafeSP.putAny(it, newInt) }
                    updatedOnValueChange?.let { it1 -> it1(newInt) }
                },
                valueRange = min.toFloat()..max.toFloat(),
                enabled = enabled,
            )
        },
        enabled = enabled,
    )
    EditTextDialog(
        visibility = dialogVisibility,
        holdDownState = dialogHoldDown,
        title = title,
        message = stringResource(R.string.slider_dialog_message_decimal, defValue, min, max),
        placeholder = defValue.toString(),
        value = spValue.toString(),
        onInputConfirm = { newString ->
            doOnInputConfirm(newString)
        }
    )
}

@Composable
fun SeekBarPreference(
    icon: ImageIcon? = null,
    title: String,
    key: String? = null,
    defValue: Float = 0.0f,
    min: Float = 0.0f,
    max: Float = 1.0f,
    showValue: Boolean = true,
    format: String = "%.2f",
    enabled: Boolean = true,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    onValueChange: ((Float) -> Unit)? = null,
) {
    var spValue by remember {
        mutableFloatStateOf(
            key?.let { SafeSP.getFloat(it, defValue) } ?: defValue
        )
    }
    val updatedOnValueChange by rememberUpdatedState(onValueChange)
    val dialogVisibility = remember { mutableStateOf(false) }
    val dialogHoldDown = remember { mutableStateOf(false) }

    val doOnInputConfirm: (String) -> Unit = { newString: String ->
        val oldValue = spValue
        val newValue = newString.toFloatOrNull()
        if (newValue != null && newValue in min..max && oldValue != newValue) {
            spValue = newValue
            key?.let { SafeSP.putAny(it, newValue) }
            updatedOnValueChange?.let { it(newValue) }
        }
    }

    SuperArrow(
        title = title,
        titleColor = titleColor,
        startAction = icon?.let { { DrawableResIcon(it) } },
        endActions = {
            if (showValue) {
                Text(
                    text = String.format(Locale.current.platformLocale, format, spValue),
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                )
            }
        },
        onClick = {
            if (enabled) {
                dialogVisibility.value = true
                dialogHoldDown.value = true
            }
        },
        holdDownState = dialogHoldDown.value,
        bottomAction = {
            Slider(
                value = spValue,
                onValueChange = { newValue ->
                    spValue = newValue
                    key?.let { SafeSP.putAny(it, newValue) }
                    updatedOnValueChange?.let { it1 -> it1(newValue) }
                },
                valueRange = min..max,
                enabled = enabled,
            )
        },
        enabled = enabled,
    )
    EditTextDialog(
        visibility = dialogVisibility,
        holdDownState = dialogHoldDown,
        title = title,
        message = stringResource(R.string.slider_dialog_message_float, defValue, min, max),
        placeholder = defValue.toString(),
        value = spValue.toString(),
        onInputConfirm = { newString ->
            doOnInputConfirm(newString)
        }
    )
}
