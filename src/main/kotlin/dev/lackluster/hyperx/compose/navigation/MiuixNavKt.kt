package dev.lackluster.hyperx.compose.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import dev.lackluster.hyperx.compose.navigation.MiuixNavHostDefaults.NavAnimationEasing
import dev.lackluster.hyperx.compose.navigation.MiuixNavHostDefaults.TRANSITION_DURATION
import kotlin.collections.forEach


@Composable
fun rememberMiuixNavController(
    vararg navigators: Navigator<out NavDestination>
): NavHostController =
    rememberNavController(
        remember { MiuixNavigator() },
        *navigators
    )


fun NavGraphBuilder.miuixComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        null,
    exitTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        null,
    popEnterTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        enterTransition,
    popExitTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        exitTransition,
    sizeTransform:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? =
        null,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    destination(
        MiuixNavigatorDestinationBuilder(provider[MiuixNavigator::class], route, content)
            .apply {
                arguments.forEach { (argumentName, argument) -> argument(argumentName, argument) }
                deepLinks.forEach { deepLink -> deepLink(deepLink) }
                this.enterTransition = enterTransition
                this.exitTransition = exitTransition
                this.popEnterTransition = popEnterTransition
                this.popExitTransition = popExitTransition
                this.sizeTransform = sizeTransform
            }
    )
}

@Stable
fun miuixEnterTransition(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(TRANSITION_DURATION, 0, NavAnimationEasing)
    )

@Stable
fun miuixExitTransition(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { -it / 4 },
        animationSpec = tween(TRANSITION_DURATION, 0, NavAnimationEasing)
    ) + fadeOut(
        targetAlpha = 0.5f,
        animationSpec = tween(TRANSITION_DURATION, 0, NavAnimationEasing)
    )

@Stable
fun miuixPopEnterTransition(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { -it / 4 },
        animationSpec = tween(TRANSITION_DURATION, 0, NavAnimationEasing)
    ) + fadeIn(
        initialAlpha = 0.5f,
        animationSpec = tween(TRANSITION_DURATION, 0, NavAnimationEasing)
    )

@Stable
fun miuixPopExitTransition(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(TRANSITION_DURATION, 0, NavAnimationEasing)
    )

