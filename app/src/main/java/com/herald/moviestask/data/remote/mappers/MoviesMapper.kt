package com.herald.moviestask.data.remote.mappers

import com.herald.moviestask.data.remote.dto.MoviesDTO
import com.herald.moviestask.domain.models.MoviesModel
import java.util.Locale

fun MoviesDTO.toMovies():MoviesModel{
    return MoviesModel(
        movieListItems = results.map {
            MoviesModel.MovieItem(
                id = it.id,
                posterPath = it.posterPath,
                releaseDate = it.releaseDate.split("-")[0].ifEmpty { "----" },
                title = it.title,
                voteAverage = String.format(Locale.ENGLISH,"%.1f",it.voteAverage)
            )
        },
    )
}