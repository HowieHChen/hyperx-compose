package dev.lackluster.hyperx.compose.base

import android.content.res.Configuration
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.lackluster.hyperx.compose.R
import dev.lackluster.hyperx.compose.animation.SpringEasing
import dev.lackluster.hyperx.compose.theme.AppTheme
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun HyperXApp(
    autoSplitView: MutableState<Boolean> = mutableStateOf(true),
    mainPageContent: @Composable (navController: NavHostController, adjustPadding: PaddingValues) -> Unit,
    emptyPageContent: @Composable () -> Unit = { DefaultEmptyPage() },
    otherPageBuilder: (NavGraphBuilder.(navController: NavHostController, adjustPadding: PaddingValues) -> Unit)? = null
) {
    AppTheme {
        val configuration = LocalConfiguration.current
        val isLandscape by rememberUpdatedState(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        val density = LocalDensity.current
        val getWindowSize by rememberUpdatedState(getWindowSize())
        val windowWidth by rememberUpdatedState(getWindowSize.width.dp / density.density)
        val windowHeight by rememberUpdatedState(getWindowSize.height.dp / density.density)
        val largeScreen by remember { derivedStateOf { (windowHeight >= 480.dp && windowWidth >= 840.dp) } }
        val appRootLayout by remember { derivedStateOf {
            if (autoSplitView.value) {
                if (largeScreen) {
                    if (isLandscape) {
                        AppRootLayout.Split12
                    } else {
                        AppRootLayout.Split11
                    }
                } else {
                    if (isLandscape) {
                        AppRootLayout.Split11
                    } else {
                        AppRootLayout.Normal
                    }
                }
            } else {
                if (largeScreen) {
                    AppRootLayout.LargeScreen
                } else {
                    AppRootLayout.Normal
                }
            }
        } }
        val normalLayoutPadding = if (appRootLayout == AppRootLayout.LargeScreen)
            PaddingValues(horizontal = windowWidth * 0.1f)
        else
            PaddingValues(0.dp)
        val splitRightWeight = if (appRootLayout == AppRootLayout.Split12)
            2.0f
        else
            1.0f
        if (appRootLayout == AppRootLayout.Split11 || appRootLayout == AppRootLayout.Split12) {
            SplitLayout(mainPageContent, emptyPageContent, otherPageBuilder, 1.0f, splitRightWeight)
        } else {
            NormalLayout(mainPageContent, otherPageBuilder, normalLayoutPadding)
        }
        MiuixPopupUtil.MiuixPopupHost()
    }
}

@Composable
fun NormalLayout(
    mainPageContent: @Composable (navController: NavHostController, adjustPadding: PaddingValues) -> Unit,
    otherPageBuilder: (NavGraphBuilder.(navController: NavHostController, adjustPadding: PaddingValues) -> Unit)? = null,
    adjustPadding: PaddingValues = PaddingValues(0.dp)
) {
    val easing = HyperXAppDefaults.NavAnimationEasing
    val duration = easing.duration.toInt()
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HyperXAppDefaults.PAGE_MAIN,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(duration, 0, easing)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 4 },
                animationSpec = tween(duration, 0, easing)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 4 },
                animationSpec = tween(duration, 0, easing)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(duration, 0, easing)
            )
        }
    ) {
        composable(HyperXAppDefaults.PAGE_MAIN) { mainPageContent(navController, adjustPadding) }
        otherPageBuilder?.let { it(navController, adjustPadding) }
    }
}

@Composable
fun SplitLayout(
    mainPageContent: @Composable (navController: NavHostController, adjustPadding: PaddingValues) -> Unit,
    emptyPageContent: @Composable () -> Unit,
    otherPageBuilder: (NavGraphBuilder.(navController: NavHostController, adjustPadding: PaddingValues) -> Unit)? = null,
    leftWeight: Float = 1.0f,
    rightWeight: Float = 1.0f
) {
    val easing = HyperXAppDefaults.NavAnimationEasing
    val duration = easing.duration.toInt()
    val navController = rememberNavController()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier.weight(leftWeight)
        ) {
            mainPageContent(navController, PaddingValues(horizontal = 12.dp))
        }
        VerticalDivider(thickness = 0.75.dp, color = MiuixTheme.colorScheme.dividerLine)
        NavHost(
            navController = navController,
            startDestination = HyperXAppDefaults.PAGE_EMPTY,
            modifier = Modifier.weight(rightWeight),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(duration, 0, easing)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(duration, 0, easing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(duration, 0, easing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(duration, 0, easing)
                )
            }
        ) {
            composable(
                HyperXAppDefaults.PAGE_EMPTY,
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() }
            ) { emptyPageContent() }
            otherPageBuilder?.let { it(navController, PaddingValues(horizontal = 12.dp)) }
        }
    }
}

@Composable
fun DefaultEmptyPage(
    imageIcon: ImageIcon = ImageIcon(
        iconRes = R.drawable.ic_miuix,
        iconSize = 255.dp
    )
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DrawableResIcon(imageIcon)
    }
}

object HyperXAppDefaults {
    const val PAGE_MAIN = "MainPage"
    const val PAGE_EMPTY = "EmptyPage"

    val NavAnimationEasing = SpringEasing(0.95f, 0.4f)
}

enum class AppRootLayout {
    Normal,
    LargeScreen,
    Split11,
    Split12
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp,
    color: Color,
) =
    Canvas(modifier.fillMaxHeight().width(thickness)) {
        drawLine(
            color = color,
            strokeWidth = thickness.toPx(),
            start = Offset(thickness.toPx() / 2, 0f),
            end = Offset(thickness.toPx() / 2, size.height),
        )
    }