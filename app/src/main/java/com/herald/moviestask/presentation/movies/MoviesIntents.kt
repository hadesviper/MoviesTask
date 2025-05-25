package com.herald.moviestask.presentation.movies

sealed interface MoviesIntents {
    data class OpenMovieDetails(val id: Int) : MoviesIntents
    data class LoadMovieDetails(val id: Int) : MoviesIntents
    data object RetryLoadingData : MoviesIntents
}
