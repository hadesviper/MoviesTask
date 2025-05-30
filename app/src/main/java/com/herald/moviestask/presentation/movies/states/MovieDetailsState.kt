package com.herald.moviestask.presentation.movies.states

import com.herald.moviestask.domain.models.MovieModel

data class MovieDetailsState(
    val isLoading: Boolean = false,
    val movie: MovieModel? = null,
    val error: String? = null
)