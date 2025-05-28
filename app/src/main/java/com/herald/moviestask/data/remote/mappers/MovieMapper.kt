package com.herald.moviestask.data.remote.mappers

import com.herald.moviestask.data.remote.dto.MovieDTO
import com.herald.moviestask.domain.models.MovieModel
import java.util.Locale

fun MovieDTO.toMovieModel(): MovieModel {
    return MovieModel(
        backdropPath = backdropPath ?: "",
        genres = genres?.map { it?.name ?: "" } ?: emptyList(),
        id = id,
        overview = overview.ifEmpty { "No overview available" },
        releaseDate = releaseDate.split("-")[0].ifEmpty { "----" },
        runtime = runtime ?: 0,
        tagline = tagline ?: "",
        title = title ?: "",
        ytTrailer = videos?.results?.find { it?.type == "Trailer" }?.key ?: "",
        voteAverage =  String.format(Locale.ENGLISH,"%.1f",voteAverage)
    )
}