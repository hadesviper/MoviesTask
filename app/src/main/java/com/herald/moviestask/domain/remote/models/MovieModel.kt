package com.herald.moviestask.domain.remote.models


data class MovieModel(
    val backdropPath: String,
    val genres: List<String>,
    val id: Int,
    val overview: String,
    val releaseDate: String,
    val runtime: Int,
    val tagline: String,
    val title: String,
    val ytTrailer: String,
    val voteAverage: String,
)
