package dev.lackluster.hyperx.compose.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.navigation.Navigator
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.icon.ImmersionClose
import dev.lackluster.hyperx.compose.icon.ImmersionConfirm
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun FullScreenDialog(
    navigator: Navigator,
    adjustPadding: PaddingValues,
    title: String,
    blurEnabled: MutableState<Boolean> = mutableStateOf(true),
    blurTintAlphaLight: MutableFloatState = mutableFloatStateOf(0.8f),
    blurTintAlphaDark: MutableFloatState = mutableFloatStateOf(0.7f),
    mode: BasePageDefaults.Mode = BasePageDefaults.Mode.FULL,
    onNegativeButton: (() -> Unit)? = {
        navigator.pop()
    },
    onPositiveButton: (() -> Unit)? = {
        navigator.pop()
    },
    content: LazyListScope.() -> Unit
) {
    val currentOnNegativeButton by rememberUpdatedState(onNegativeButton)
    val currentOnPositiveButton by rememberUpdatedState(onPositiveButton)

    BackHandler(enabled = true) {
        currentOnNegativeButton?.invoke()
    }

    BasePage(
        navigator = navigator,
        adjustPadding = adjustPadding,
        title = title,
        blurEnabled = blurEnabled,
        mode = mode,
        blurTintAlphaLight = blurTintAlphaLight,
        blurTintAlphaDark = blurTintAlphaDark,
        navigationIcon = { padding ->
            IconButton(
                modifier = Modifier
                    .padding(padding)
                    .padding(start = 21.dp)
                    .size(40.dp),
                onClick = {
                    currentOnNegativeButton?.invoke()
                }
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = MiuixIcons.ImmersionClose,
                    contentDescription = "Close",
                    tint = MiuixTheme.colorScheme.onSurfaceSecondary
                )
            }
        },
        actions = { padding ->
            IconButton(
                modifier = Modifier
                    .padding(padding)
                    .padding(end = 21.dp)
                    .size(40.dp),
                onClick = {
                    currentOnPositiveButton?.invoke()
                }
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = MiuixIcons.ImmersionConfirm,
                    contentDescription = "Confirm",
                    tint = MiuixTheme.colorScheme.onSurfaceSecondary
                )
            }
        },
        content = content
    )
}