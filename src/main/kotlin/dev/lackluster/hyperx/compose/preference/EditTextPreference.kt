package dev.lackluster.hyperx.compose.preference

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.compose.R
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.base.DrawableResIcon
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowRight
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog

@Composable
fun EditTextPreference(
    icon: ImageIcon? = null,
    title: String,
    summary: String? = null,
    key: String? = null,
    defValue: Any = "",
    dataType: EditTextDataType,
    dialogMessage: String? = null,
    dialogPlaceholder: String? = null,
    isValueValid: ((value: Any) -> Boolean)? = null,
    valuePosition: ValuePosition = ValuePosition.VALUE_VIEW,
    enabled: Boolean = true,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    rightActionColor: RightActionColor = RightActionDefaults.rightActionColors(),
    onValueChange: ((String, Any) -> Unit)? = null,
) {
    var spValue by remember { mutableStateOf(
        key?.let {
            when (dataType) {
                EditTextDataType.BOOLEAN -> SafeSP.getBoolean(key, defValue as? Boolean ?: false)
                EditTextDataType.INT -> SafeSP.getInt(key, defValue as? Int ?: 0)
                EditTextDataType.FLOAT -> SafeSP.getFloat(key, defValue as? Float ?: 0.0f)
                EditTextDataType.LONG -> SafeSP.getLong(key, defValue as? Long ?: 0L)
                EditTextDataType.STRING -> SafeSP.getString(key, defValue as? String ?: "")
            }
        } ?: defValue
    ) }
    val updatedOnValueChange by rememberUpdatedState(onValueChange)
    val dialogVisibility = remember { mutableStateOf(false) }

    val doOnInputConfirm: (String) -> Unit = { newString: String ->
        val oldValue = spValue
        val newValue = when (dataType) {
            EditTextDataType.BOOLEAN -> newString.toBooleanStrictOrNull()
            EditTextDataType.INT -> newString.toIntOrNull()
            EditTextDataType.FLOAT -> newString.toFloatOrNull()
            EditTextDataType.LONG -> newString.toLongOrNull()
            EditTextDataType.STRING -> newString
        }
        if (newValue != null && isValueValid?.invoke(newValue) != false && oldValue != newValue) {
            spValue = newValue
            key?.let { SafeSP.putAny(it, newValue) }
            updatedOnValueChange?.let { it(newString, newValue) }
        }
    }

    BasicComponent(
        insideMargin = PaddingValues((icon?.getHorizontalPadding() ?: 16.dp), 16.dp, 16.dp, 16.dp),
        title = title,
        titleColor = titleColor,
        summary = spValue.toString().takeIf { valuePosition == ValuePosition.SUMMARY_VIEW && it.isNotBlank() } ?: summary,
        summaryColor = summaryColor,
        leftAction = {
            icon?.let {
                DrawableResIcon(it)
            }
        },
        rightActions = {
            if (valuePosition == ValuePosition.VALUE_VIEW) {
                Text(
                    modifier = Modifier.widthIn(max = 130.dp),
                    text = spValue.toString(),
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = rightActionColor.color(enabled),
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
            Image(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(10.dp, 16.dp),
                imageVector = MiuixIcons.ArrowRight,
                contentDescription = null,
                colorFilter = ColorFilter.tint(rightActionColor.color(enabled)),
            )
        },
        onClick = {
            if (enabled) {
                dialogVisibility.value = true
            }
        },
        enabled = enabled
    )

    EditTextDialog(
        visibility = dialogVisibility,
        title = title,
        message = dialogMessage,
        placeholder = dialogPlaceholder ?: defValue.toString(),
        value = spValue.toString(),
        onInputConfirm = { newString ->
            doOnInputConfirm(newString)
        }
    )
}

@Composable
fun EditTextDialog(
    visibility: MutableState<Boolean>,
    title: String?,
    message: String? = null,
    placeholder: String? = null,
    value: String = "",
    onInputConfirm: ((value: String) -> Unit)? = null
) {
    val textState = remember { mutableStateOf(
        TextFieldValue(text = value, selection = TextRange(value.length))
    ) }
    val hapticFeedback = LocalHapticFeedback.current
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    SuperDialog(
        title = title,
        summary = message,
        show = visibility,
        onDismissRequest = {
            if (visibility.value) {
                keyboard?.hide()
                dismissDialog(visibility)
            }
        }
    ) {
        LaunchedEffect(visibility.value) {
            if (visibility.value) {
                textState.value = TextFieldValue(text = value, selection = TextRange(value.length))
                focusRequester.requestFocus()
            }
        }
        TextField(
            modifier = Modifier.padding(bottom = 12.dp).focusRequester(focusRequester),
            value = textState.value,
            singleLine = true,
            label = placeholder ?: "",
            useLabelAsPlaceholder = true,
            onValueChange = { textState.value = it }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.button_cancel),
                minHeight = 50.dp,
                onClick = {
                    keyboard?.hide()
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    dismissDialog(visibility)
                }
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.button_ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                minHeight = 50.dp,
                onClick = {
                    keyboard?.hide()
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    dismissDialog(visibility)
                    onInputConfirm?.let {
                        it(textState.value.text)
                    }
                }
            )
        }
    }
}

enum class EditTextDataType {
    BOOLEAN,
    INT,
    FLOAT,
    LONG,
    STRING,
}

enum class ValuePosition {
    HIDDEN,
    VALUE_VIEW,
    SUMMARY_VIEW,
}