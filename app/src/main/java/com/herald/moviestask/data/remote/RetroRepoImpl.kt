package com.herald.moviestask.data.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.herald.moviestask.data.remote.pagingsource.MoviePagingSource
import com.herald.moviestask.data.remote.pagingsource.SearchingPagingSource
import com.herald.moviestask.domain.models.MovieModel
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.domain.remote.repository.RetroRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RetroRepoImpl @Inject constructor(
    private val retroService: RetroService
): RetroRepository {

    override fun getPopularMovies(onError: (Exception) -> Unit): Flow<PagingData<MoviesModel.MovieItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
            ),
            pagingSourceFactory = {
                MoviePagingSource(retroService, onError)
            }
        ).flow
    }

    override fun searchMovies(query: String,onError: (Exception) -> Unit): Flow<PagingData<MoviesModel.MovieItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
            ),
            pagingSourceFactory = {
                SearchingPagingSource(retroService,query, onError)
            }
        ).flow
    }

    override suspend fun getMovieDetails(id: Int): MovieModel {
        return retroService.getMovieDetails(id).toMovieModel()
    }

    override suspend fun getTrendingMovies(page: Int): MoviesModel {
        return retroService.getTrendingMovies(page).toMovies()
    }
}