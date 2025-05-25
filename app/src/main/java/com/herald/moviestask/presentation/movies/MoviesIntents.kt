package com.herald.moviestask.presentation.movies

sealed interface MoviesIntents {
    data class OpenMovieDetails(val id: Int) : MoviesIntents
    data object OpenMovieSearch : MoviesIntents
    data class LoadMovieDetails(val id: Int) : MoviesIntents
    data object RetryLoadingData : MoviesIntents
    data object NavigateBack : MoviesIntents
    data class OnSearchQueryChanged (val query: String): MoviesIntents
}
