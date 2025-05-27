package com.herald.moviestask.presentation.movies

sealed interface MoviesEvents {
    data class  ErrorOccurred(val error: String) : MoviesEvents
    data class  NavigateToMovieDetails(val id: Int) : MoviesEvents
    data object NavigateToMovieSearch : MoviesEvents
    data object NavigateBack : MoviesEvents
    data object Retry : MoviesEvents
}