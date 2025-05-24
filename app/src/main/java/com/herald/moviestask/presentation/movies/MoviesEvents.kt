package com.herald.moviestask.presentation.movies

import com.herald.moviestask.domain.remote.models.MoviesModel

sealed interface MoviesEvents {
    data object Retry : MoviesEvents
    data class ErrorOccurred(val error: String) : MoviesEvents
    data class NavigateToMovieDetails(val movie: MoviesModel.MovieData) : MoviesEvents
}