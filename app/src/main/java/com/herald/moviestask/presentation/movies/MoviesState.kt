package com.herald.moviestask.presentation.movies

import com.herald.moviestask.domain.remote.models.MoviesModel

data class MoviesState(
    val isLoading: Boolean = false,
    val movies: List<MoviesModel.MovieData>? = null,
    val error: String? = null
)
