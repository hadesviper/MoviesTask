package com.herald.moviestask.presentation.movies.states

import com.herald.moviestask.domain.models.MoviesModel

data class MovieTopRatedState(
    val isLoading: Boolean = false,
    val movies: MoviesModel? = null,
    val error: String? = null
)