package dev.lackluster.hyperx.compose.base

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.compose.preference.RightActionDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun IntegratedTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    enabled: Boolean = true,
    insideMargin: PaddingValues = PaddingValues(0.dp),
    interactionSource: MutableInteractionSource? = null,
) {
    val interaction = interactionSource ?: remember { MutableInteractionSource() }
    val focused = interaction.collectIsFocusedAsState().value
    val focusRequester = remember { FocusRequester() }
    if (focused) {
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
            .focusRequester(focusRequester)
            .semantics {
                onClick {
                    focusRequester.requestFocus()
                    true
                }
            },
        enabled = enabled,
        singleLine = true,
        textStyle = MiuixTheme.textStyles.main.copy(
            textAlign = TextAlign.End
        ),
        cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
        interactionSource = interaction,
        decorationBox =
            @Composable { innerTextField ->
                Box(
                    modifier = Modifier.padding(insideMargin),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = if (text.isEmpty()) hint else "",
                        color = RightActionDefaults.rightActionColors().color(enabled),
                        textAlign = TextAlign.End,
                        softWrap = false,
                        maxLines = 1
                    )
                    innerTextField()
                }
            }
    )
}