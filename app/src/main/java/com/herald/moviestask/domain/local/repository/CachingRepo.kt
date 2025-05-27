package com.herald.moviestask.domain.local.repository

import com.herald.moviestask.domain.models.MoviesModel

interface CachingRepo {
    suspend fun saveTrendingMovie(movies: List<MoviesModel.MovieItem>)
    suspend fun deleteAllMovies()
    suspend fun getTrendingMovies(): MoviesModel
}