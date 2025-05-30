package com.herald.moviestask.domain.remote.repository

import androidx.paging.PagingData
import com.herald.moviestask.domain.models.MovieModel
import com.herald.moviestask.domain.models.MoviesModel
import kotlinx.coroutines.flow.Flow

interface RetroRepository {
    fun getPopularMovies(): Flow<PagingData<MoviesModel.MovieItem>>
    fun searchMovies(query: String): Flow<PagingData<MoviesModel.MovieItem>>
    suspend fun getMovieDetails(id: Int): MovieModel
    suspend fun getTopRatedMovies(page: Int): MoviesModel
}