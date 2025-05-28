package com.herald.moviestask.data.local.mappers

import com.herald.moviestask.data.local.entities.MoviesEntity
import com.herald.moviestask.domain.models.MoviesModel

fun MoviesEntity.toMoviesModel(): MoviesModel.MovieItem {
    return MoviesModel.MovieItem(
        id = id,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        voteAverage = voteAverage
    )
}