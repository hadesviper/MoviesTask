package com.herald.moviestask.domain.models

data class MoviesModel(
    val movieListItems: List<MovieItem>,
) {
    data class MovieItem(
        val id: Int,
        val posterPath: String?,
        val releaseDate: String,
        val title: String,
        val voteAverage: String
    )
}