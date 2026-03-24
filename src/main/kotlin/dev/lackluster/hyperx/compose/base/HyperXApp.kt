package dev.lackluster.hyperx.compose.base

import android.content.res.Configuration
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import dev.lackluster.hyperx.compose.R
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.navigation.MiuixNavHost
import dev.lackluster.hyperx.compose.navigation.MiuixNavHostDefaults
import dev.lackluster.hyperx.compose.navigation.miuixComposable
import dev.lackluster.hyperx.compose.navigation.rememberMiuixNavController
import dev.lackluster.hyperx.compose.theme.AppTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils

@Composable
fun HyperXApp(
    autoSplitView: MutableState<Boolean> = mutableStateOf(true),
    mainPageContent: @Composable (navController: NavHostController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) -> Unit,
    emptyPageContent: @Composable () -> Unit = { DefaultEmptyPage() },
    otherPageBuilder: (NavGraphBuilder.(navController: NavHostController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) -> Unit)? = null
) {
    AppTheme {
        val configuration = LocalConfiguration.current
        val isLandscape by rememberUpdatedState(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        val density = LocalDensity.current
        val containerSize = LocalWindowInfo.current.containerSize
        val windowWidth by rememberUpdatedState(with(density) { containerSize.width.toDp() })
        val windowHeight by rememberUpdatedState(with(density) { containerSize.height.toDp() })
        val largeScreen by remember { derivedStateOf { (windowHeight >= 480.dp && windowWidth >= 840.dp) } }
        val appRootLayout: AppRootLayout
        val normalLayoutPadding: PaddingValues
        val splitRightWeight: Float
        if (autoSplitView.value && largeScreen && isLandscape) {
            appRootLayout = AppRootLayout.Split12
            normalLayoutPadding = PaddingValues(0.dp)
            splitRightWeight = 2.0f
        } else if (autoSplitView.value && (largeScreen || isLandscape)) {
            appRootLayout = AppRootLayout.Split11
            normalLayoutPadding = PaddingValues(0.dp)
            splitRightWeight = 1.0f
        } else if (largeScreen) {
            appRootLayout = AppRootLayout.LargeScreen
            normalLayoutPadding = PaddingValues(horizontal = windowWidth * 0.1f)
            splitRightWeight = 1.0f
        } else {
            appRootLayout = AppRootLayout.Normal
            normalLayoutPadding = PaddingValues(0.dp)
            splitRightWeight = 1.0f
        }
        if (appRootLayout == AppRootLayout.Split11 || appRootLayout == AppRootLayout.Split12) {
            SplitLayout(mainPageContent, emptyPageContent, otherPageBuilder, 1.0f, splitRightWeight)
        } else {
            NormalLayout(mainPageContent, otherPageBuilder, normalLayoutPadding)
        }
        MiuixPopupUtils.MiuixPopupHost()
    }
}

@Composable
fun NormalLayout(
    mainPageContent: @Composable (navController: NavHostController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) -> Unit,
    otherPageBuilder: (NavGraphBuilder.(navController: NavHostController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) -> Unit)? = null,
    extraPadding: PaddingValues = PaddingValues(0.dp)
) {
    val navController = rememberMiuixNavController()
    val layoutDirection = LocalLayoutDirection.current
    val systemBarInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal).asPaddingValues()
    val contentPadding = systemBarInsets.let {
        PaddingValues.Absolute(
            left = it.calculateLeftPadding(layoutDirection) + extraPadding.calculateLeftPadding(layoutDirection),
            top = extraPadding.calculateTopPadding(),
            right = it.calculateRightPadding(layoutDirection) + extraPadding.calculateRightPadding(layoutDirection),
            bottom = extraPadding.calculateBottomPadding()
        )
    }
    MiuixNavHost(
        modifier = Modifier.background(Color.Black),
        navController = navController,
        startDestination = HyperXAppDefaults.PAGE_MAIN,
        cornerRadius = HyperXActivity.screenCornerRadius.intValue.dp
    ) {
        miuixComposable(HyperXAppDefaults.PAGE_MAIN) { mainPageContent(navController, contentPadding, BasePageDefaults.Mode.FULL) }
        otherPageBuilder?.let { it(navController, contentPadding, BasePageDefaults.Mode.FULL) }
    }
}

@Composable
fun SplitLayout(
    mainPageContent: @Composable (navController: NavHostController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) -> Unit,
    emptyPageContent: @Composable () -> Unit,
    otherPageBuilder: (NavGraphBuilder.(navController: NavHostController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) -> Unit)? = null,
    leftWeight: Float = 1.0f,
    rightWeight: Float = 1.0f
) {
    val easing = MiuixNavHostDefaults.NavAnimationEasing
    val duration = 500
    val navController = rememberMiuixNavController()
    val layoutDirection = LocalLayoutDirection.current
    val systemBarInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal).asPaddingValues()
    val contentPaddingLeft = systemBarInsets.let {
        PaddingValues.Absolute(
            left = it.calculateLeftPadding(layoutDirection) + 12.dp,
            top = it.calculateTopPadding(),
            right = 12.dp,
            bottom = it.calculateBottomPadding()
        )
    }
    val contentPaddingRight = systemBarInsets.let {
        PaddingValues.Absolute(
            left = 12.dp,
            top = it.calculateTopPadding(),
            right = it.calculateRightPadding(layoutDirection) + 12.dp,
            bottom = it.calculateBottomPadding()
        )
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier.weight(leftWeight)
        ) {
            mainPageContent(navController, contentPaddingLeft, BasePageDefaults.Mode.SPLIT_LEFT)
        }
        VerticalDivider(thickness = 0.75.dp, color = MiuixTheme.colorScheme.dividerLine)
        MiuixNavHost(
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
            miuixComposable(
                HyperXAppDefaults.PAGE_EMPTY,
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() }
            ) { emptyPageContent() }
            otherPageBuilder?.let { it(navController, contentPaddingRight, BasePageDefaults.Mode.SPLIT_RIGHT) }
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
    Canvas(
        modifier = modifier
            .fillMaxHeight()
            .width(thickness)
    ) {
        drawLine(
            color = color,
            strokeWidth = thickness.toPx(),
            start = Offset(thickness.toPx() / 2, 0f),
            end = Offset(thickness.toPx() / 2, size.height),
        )
    }