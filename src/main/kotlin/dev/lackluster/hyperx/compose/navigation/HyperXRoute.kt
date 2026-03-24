package dev.lackluster.hyperx.compose.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface HyperXRoute : NavKey {
    @Serializable
    data object Main : HyperXRoute

    @Serializable
    data object Empty : HyperXRoute
}
