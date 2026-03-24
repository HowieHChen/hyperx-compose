package dev.lackluster.hyperx.compose.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import top.yukonga.miuix.kmp.basic.FabPosition
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalHazeApi::class)
@Composable
fun HazeScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable ((contentPadding: PaddingValues) -> Unit)? = null,
    bottomBar: @Composable ((contentPadding: PaddingValues) -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    snackbarHost: @Composable () -> Unit = {},
    containerColor: Color = MiuixTheme.colorScheme.surface,
    contentWindowInsets: WindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Vertical),
    blurTopBar: Boolean = false,
    blurBottomBar: Boolean = false,
    blurTintAlpha: Float = 0.8f,
    hazeState: HazeState = remember { HazeState() },
    hazeStyle: HazeStyle = HazeStyle(
        backgroundColor = containerColor,
        tint = HazeTint(containerColor.copy(blurTintAlpha))
    ),
    adjustPadding: PaddingValues = PaddingValues(0.dp),
    fixedBackgroundColor: Color = containerColor,
    fixedContent: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val density = LocalDensity.current

    var fixedContentHeightPx by remember { mutableIntStateOf(0) }
    val fixedPadding = with(density) {
        fixedContentHeightPx.toDp()
    }

    Scaffold(
        modifier = modifier,
        topBar = @Composable {
            topBar?.let {
                if (blurTopBar) {
                    Box(
                        modifier = Modifier.hazeEffect(state = hazeState, style = hazeStyle) {
                            blurRadius = 20.dp
                            inputScale = HazeInputScale.Fixed(0.35f)
                            noiseFactor = 0f
                            forceInvalidateOnPreDraw = false
                        },
                    ) {
                        it(adjustPadding)
                    }
                } else {
                    it(adjustPadding)
                }
            }
        },
        bottomBar = @Composable {
            bottomBar?.let {
                if (blurBottomBar) {
                    Box(
                        modifier = Modifier.hazeEffect(state = hazeState, style = hazeStyle) {
                            blurRadius = 20.dp
                            inputScale = HazeInputScale.Fixed(0.35f)
                            noiseFactor = 0f
                            forceInvalidateOnPreDraw = false
                        },
                    ) {
                        it(adjustPadding)
                    }
                } else {
                    it(adjustPadding)
                }
            }
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = snackbarHost,
        containerColor = containerColor,
        contentWindowInsets = contentWindowInsets,
    ) { contentPadding ->
        Box(
            Modifier
                .hazeSource(state = hazeState)
        ) {
            content(
                PaddingValues(
                    start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr) +
                            adjustPadding.calculateLeftPadding(LayoutDirection.Ltr),
                    top = contentPadding.calculateTopPadding() +
                            adjustPadding.calculateTopPadding() +
                            fixedPadding,
                    end = contentPadding.calculateRightPadding(LayoutDirection.Ltr) +
                            adjustPadding.calculateRightPadding(LayoutDirection.Ltr),
                    bottom = contentPadding.calculateBottomPadding() +
                            adjustPadding.calculateBottomPadding()
                )
            )
        }

        fixedContent?.let {
            Box(
                modifier = if (blurTopBar) {
                    Modifier.hazeEffect(state = hazeState, style = hazeStyle) {
                        blurRadius = 20.dp
                        inputScale = HazeInputScale.Fixed(0.35f)
                        noiseFactor = 0f
                        forceInvalidateOnPreDraw = false
                    }
                } else {
                    Modifier
                }
                    .zIndex(1f)
                    .background(fixedBackgroundColor)
                    .padding(top = contentPadding.calculateTopPadding())
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {} // Prevent penetration clicks
                    .onSizeChanged { size ->
                        if (fixedContentHeightPx != size.height) {
                            fixedContentHeightPx = size.height
                        }
                    }
            ) {
                it()
            }
        }
    }
}