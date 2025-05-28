package com.herald.moviestask.data.local.mappers

import com.herald.moviestask.data.local.entities.MoviesEntity
import com.herald.moviestask.domain.models.MoviesModel


fun MoviesModel.MovieItem.toMovieEntity(index: Int): MoviesEntity {
    return MoviesEntity(
        id = id,
        index = index,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        voteAverage = voteAverage
    )
}