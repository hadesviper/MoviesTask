package com.herald.moviestask.domain.remote.repository

import androidx.paging.PagingData
import com.herald.moviestask.domain.remote.models.MoviesModel
import kotlinx.coroutines.flow.Flow

interface RetroRepository {
    fun getPopularMovies(onError: (Exception) -> Unit): Flow<PagingData<MoviesModel.MovieData>>
}