package com.herald.moviestask.domain.remote.models

data class MoviesModel(
    val page: Int,
    val movieListItem: List<MovieItem>,
) {
    data class MovieItem(
        val id: Int,
        val posterPath: String?,
        val releaseDate: String,
        val title: String,
        val voteAverage: String
    )
}