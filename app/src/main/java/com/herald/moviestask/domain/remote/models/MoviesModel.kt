package com.herald.moviestask.domain.remote.models

import kotlinx.serialization.Serializable

data class MoviesModel(
    val page: Int,
    val results: List<MovieData>,
    val totalPages: Int,
    val totalResults: Int
) {
    @Serializable
    data class MovieData(
        val backdropPath: String?,
        val id: Int,
        val overview: String,
        val popularity: Double,
        val posterPath: String?,
        val releaseDate: String,
        val title: String,
        val video: Boolean,
        val voteAverage: Double
    )
}