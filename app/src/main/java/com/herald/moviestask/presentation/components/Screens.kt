package com.herald.moviestask.presentation.components

import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    object MainScreen
    @Serializable
    object DetailsScreen
}