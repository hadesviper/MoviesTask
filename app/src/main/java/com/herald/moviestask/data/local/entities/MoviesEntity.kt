package com.herald.moviestask.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.herald.moviestask.domain.models.MoviesModel

@Entity(tableName = "Movies")
data class MoviesEntity(
    @PrimaryKey
    val id: Int,
    val index: Int,
    val posterPath: String?,
    val releaseDate: String,
    val title: String,
    val voteAverage: String
) {
    fun toMoviesModel(): MoviesModel.MovieItem {
        return MoviesModel.MovieItem(
            id = id,
            posterPath = posterPath,
            releaseDate = releaseDate,
            title = title,
            voteAverage = voteAverage
        )
    }
}