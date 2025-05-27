package com.herald.moviestask.domain.local.repository

import com.herald.moviestask.domain.models.MoviesModel

interface CachingRepo {
    suspend fun saveTopRatedMovie(movies: List<MoviesModel.MovieItem>)
    suspend fun deleteAllMovies()
    suspend fun getTopRatedMovies(): MoviesModel
}