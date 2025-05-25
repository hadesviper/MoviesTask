package com.herald.moviestask.presentation.movies

import com.herald.moviestask.domain.remote.models.MovieModel

data class SingleMovieStates (
    val isLoading: Boolean = false,
    val movie: MovieModel? = null,
    val error: String? = ""
)