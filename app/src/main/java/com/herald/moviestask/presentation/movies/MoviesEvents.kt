package com.herald.moviestask.presentation.movies

sealed interface MoviesEvents {
    data object Retry : MoviesEvents
    data class ErrorOccurred(val error: String) : MoviesEvents
    data class NavigateToMovieDetails(val id: Int) : MoviesEvents
}