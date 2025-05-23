package com.herald.moviestask.presentation.movies

import com.herald.moviestask.domain.remote.models.MoviesModel

sealed class MoviesIntents{
    data class FetchFirstMovies(val page: Int) : MoviesIntents()
    data class OpenMovieDetails(val movie: MoviesModel.MovieData): MoviesIntents()
}
