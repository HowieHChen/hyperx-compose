package dev.lackluster.hyperx.compose.preference

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.compose.R
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.base.DrawableResIcon
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SpinnerEntry
import top.yukonga.miuix.kmp.extra.SpinnerItemImpl
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowUpDownIntegrated
import top.yukonga.miuix.kmp.interfaces.HoldDownInteraction
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissPopup

@Composable
fun DropDownPreference(
    icon: ImageIcon? = null,
    title: String,
    summary: String? = null,
    entries: List<DropDownEntry>,
    key: String? = null,
    defValue: Int = 0,
    mode: DropDownMode = DropDownMode.AlwaysOnRight,
    showValue: Boolean = true,
    enabled: Boolean = true,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    rightActionColor: RightActionColor = RightActionDefaults.rightActionColors(),
    onSelectedIndexChange: ((Int) -> Unit)? = null,
) {
    var spValue by remember { mutableIntStateOf(
        (key?.let { SafeSP.getInt(it, defValue) } ?: defValue).coerceIn(
            minimumValue = 0,
            maximumValue = entries.size - 1
        )
    ) }
    val updatedOnSelectedIndexChange by rememberUpdatedState(onSelectedIndexChange)
    val wrappedEntries = entries.map { entry ->
        SpinnerEntry(
            icon = { imageModifier ->
                entry.iconVector?.let {
                    Image(
                        modifier = imageModifier,
                        imageVector = it,
                        contentDescription = null
                    )
                } ?: entry.iconRes?.let {
                    Image(
                        modifier = imageModifier,
                        painter = painterResource(it),
                        contentDescription = null
                    )
                } ?: entry.iconBitmap?.let {
                    Image(
                        modifier = imageModifier,
                        bitmap = it,
                        contentDescription = null
                    )
                }
            },
            title = entry.title,
            summary = entry.summary,
        )
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val showPopup = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val held = remember { mutableStateOf<HoldDownInteraction.Hold?>(null) }
    val hapticFeedback = LocalHapticFeedback.current
    var alignLeft by rememberSaveable { mutableStateOf(true) }
    var dropdownOffsetXPx by remember { mutableIntStateOf(0) }
    var dropdownOffsetYPx by remember { mutableIntStateOf(0) }
    var componentHeightPx by remember { mutableIntStateOf(0) }
    var componentWidthPx by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            dismissPopup(isDropdownExpanded)
        }
    }

    if (!isDropdownExpanded.value) {
        held.value?.let { oldValue ->
            coroutineScope.launch {
                interactionSource.emit(HoldDownInteraction.Release(oldValue))
            }
            held.value = null
        }
    }

    BasicComponent(
        modifier = Modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (enabled) {
                        val event = awaitPointerEvent()
                        if (event.type != PointerEventType.Move) {
                            val eventChange = event.changes.first()
                            alignLeft = eventChange.position.x < (size.width / 2)
                        }
                    }
                }
            }
            .onGloballyPositioned { coordinates ->
                if (isDropdownExpanded.value) {
                    val positionInWindow = coordinates.positionInWindow()
                    dropdownOffsetXPx = positionInWindow.x.toInt()
                    dropdownOffsetYPx = positionInWindow.y.toInt()
                    componentHeightPx = coordinates.size.height
                    componentWidthPx = coordinates.size.width
                }
            },
        interactionSource = interactionSource,
        insideMargin = PaddingValues((icon?.getHorizontalPadding() ?: 16.dp), 16.dp, 16.dp, 16.dp),
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        leftAction = {
            if (mode != DropDownMode.Dialog) {
                if (isDropdownExpanded.value) {
                    ListPopup(
                        show = showPopup,
                        alignment = if ((mode == DropDownMode.AlwaysOnRight || !alignLeft))
                            PopupPositionProvider.Align.Right
                        else
                            PopupPositionProvider.Align.Left,
                        onDismissRequest = {
                            isDropdownExpanded.value = false
                        }
                    ) {
                        ListPopupColumn {
                            wrappedEntries.forEachIndexed { index, spinnerEntry ->
                                SpinnerItemImpl(
                                    entry = spinnerEntry,
                                    entryCount = wrappedEntries.size,
                                    isSelected = spValue == index,
                                    index = index,
                                    dialogMode = false
                                ) { newValue ->
                                    spValue = newValue
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    key?.let { SafeSP.putAny(it, newValue) }
                                    updatedOnSelectedIndexChange?.let { it1 -> it1(newValue) }
                                    dismissPopup(showPopup)
                                    isDropdownExpanded.value = false
                                }
                            }
                        }
                    }
                    showPopup.value = true
                }
            }
            icon?.let {
                DrawableResIcon(it)
            }
        },
        rightActions = {
            if (showValue) {
                Text(
                    modifier = Modifier.widthIn(max = 130.dp),
                    text = wrappedEntries[spValue].title ?: "",
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
                    .size(10.dp, 16.dp)
                    .align(Alignment.CenterVertically),
                imageVector = MiuixIcons.ArrowUpDownIntegrated,
                colorFilter = ColorFilter.tint(rightActionColor.color(enabled)),
                contentDescription = null
            )
        },
        onClick = {
            if (enabled) {
                isDropdownExpanded.value = true
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                coroutineScope.launch {
                    interactionSource.emit(HoldDownInteraction.Hold().also {
                        held.value = it
                    })
                }
            }
        },
        enabled = enabled
    )
    if (mode == DropDownMode.Dialog) {
        SuperDialog(
            title = title,
            show = isDropdownExpanded,
            onDismissRequest = {
                dismissDialog(isDropdownExpanded)
            },
            insideMargin = DpSize(0.dp, 24.dp)
        ) {
            Layout(
                content = {
                    LazyColumn {
                        items(wrappedEntries.size) { index ->
                            SpinnerItemImpl(
                                entry = wrappedEntries[index],
                                entryCount = wrappedEntries.size,
                                isSelected = spValue == index,
                                index = index,
                                dialogMode = true
                            ) { newValue ->
                                spValue = newValue
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                key?.let { SafeSP.putAny(it, newValue) }
                                updatedOnSelectedIndexChange?.let { it1 -> it1(newValue) }
                                dismissDialog(isDropdownExpanded)
                            }
                        }
                    }
                    TextButton(
                        modifier = Modifier.padding(start = 24.dp, top = 12.dp, end = 24.dp).fillMaxWidth(),
                        text = stringResource(R.string.button_cancel),
                        minHeight = 50.dp,
                        onClick = {
                            dismissDialog(isDropdownExpanded)
                        }
                    )
                }
            ) { measurables, constraints ->
                if (measurables.size != 2) {
                    layout(0, 0) { }
                }
                val button = measurables[1].measure(constraints)
                val lazyList = measurables[0].measure(constraints.copy(
                    maxHeight = constraints.maxHeight - button.height
                ))
                layout(constraints.maxWidth, lazyList.height + button.height) {
                    lazyList.place(0, 0)
                    button.place(0, lazyList.height)
                }
            }
        }
    }
}

data class DropDownEntry(
    val title: String? = null,
    val summary: String? = null,
    val iconRes: Int? = null,
    val iconBitmap: ImageBitmap? = null,
    val iconVector: ImageVector? = null,
)

enum class DropDownMode {
    Normal,
    AlwaysOnRight,
    Dialog
}