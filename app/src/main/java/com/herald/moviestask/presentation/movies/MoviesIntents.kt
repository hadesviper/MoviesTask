package com.herald.moviestask.presentation.movies

import com.herald.moviestask.domain.remote.models.MoviesModel

sealed interface MoviesIntents {
    data class OpenMovieDetails(val movie: MoviesModel.MovieData) : MoviesIntents
    data object PagerRetry : MoviesIntents
}
