package dev.lackluster.hyperx.compose.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.compose.R
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog

@Composable
fun AlertDialog(
    visibility: MutableState<Boolean>,
    title: String?,
    message: String? = null,
    cancelable: Boolean = true,
    mode: AlertDialogMode = AlertDialogMode.Positive,
    negativeText: String = stringResource(R.string.button_cancel),
    positiveText: String = stringResource(R.string.button_ok),
    onNegativeButton: (() -> Unit)? = null,
    onPositiveButton: (() -> Unit)? = null,
) {
    val hapticFeedback = LocalHapticFeedback.current
    SuperDialog(
        title = title,
        summary = message,
        show = visibility,
        onDismissRequest = {
            if (cancelable) {
                dismissDialog(visibility)
            }
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (mode != AlertDialogMode.Positive) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    text = negativeText,
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                        onNegativeButton?.let { it1 -> it1() } ?: dismissDialog(visibility)
                    }
                )
            }
            if (mode == AlertDialogMode.NegativeAndPositive) {
                Spacer(Modifier.width(20.dp))
            }
            if (mode != AlertDialogMode.Negative) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    text = positiveText,
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                        onPositiveButton?.let { it1 -> it1() } ?: dismissDialog(visibility)
                    }
                )
            }
        }
    }
}

enum class AlertDialogMode {
    Negative,
    Positive,
    NegativeAndPositive,
}