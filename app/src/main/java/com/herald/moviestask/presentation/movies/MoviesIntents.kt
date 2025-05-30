package com.herald.moviestask.presentation.movies

sealed interface MoviesIntents {
    data class  OpenMovieDetails(val id: Int) : MoviesIntents
    data class  LoadMovieDetails(val id: Int) : MoviesIntents
    data class  LoadTopRatedMovies(val page: Int) : MoviesIntents
    data class  OnSearchQueryChanged (val query: String): MoviesIntents
    data class  OnErrorOccurred (val exception: Throwable): MoviesIntents
    data object RetryLoadingData : MoviesIntents
    data object NavigateBack : MoviesIntents
    data object OpenMovieSearch : MoviesIntents
}
