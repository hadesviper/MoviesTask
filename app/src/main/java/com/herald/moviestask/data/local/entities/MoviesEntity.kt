package com.herald.moviestask.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Movies")
data class MoviesEntity(
    @PrimaryKey
    val id: Int,
    val index: Int,
    val posterPath: String?,
    val releaseDate: String,
    val title: String,
    val voteAverage: String
)