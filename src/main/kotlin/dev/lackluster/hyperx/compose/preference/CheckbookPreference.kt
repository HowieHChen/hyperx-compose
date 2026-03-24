package dev.lackluster.hyperx.compose.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.DrawableResIcon
import dev.lackluster.hyperx.compose.base.ImageIcon
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import androidx.compose.ui.state.ToggleableState
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.CheckboxDefaults

@Composable
fun CheckboxPreference(
    icon: ImageIcon? = null,
    title: String,
    summary: String? = null,
    key: String? = null,
    defValue: Boolean = false,
    enabled: Boolean = true,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    var spValue by remember {
        mutableStateOf(
            key?.let { SafeSP.getBoolean(it, defValue) } ?: defValue
        )
    }
    val updatedOnCheckedChange by rememberUpdatedState(onCheckedChange)

    val handleToggle = {
        spValue = !spValue
        key?.let { SafeSP.putAny(it, spValue) }
        updatedOnCheckedChange?.invoke(spValue)
    }

    BasicComponent(
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = icon?.let { { DrawableResIcon(it) } },
        endActions = {
            Checkbox(
                state = ToggleableState(spValue),
                onClick = { handleToggle() },
                colors = CheckboxDefaults.checkboxColors(),
                enabled = enabled,
            )
        },
        onClick = {
            if (enabled) {
                handleToggle()
            }
        },
        enabled = enabled,
    )
}
