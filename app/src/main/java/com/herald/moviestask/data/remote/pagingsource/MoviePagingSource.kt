package com.herald.moviestask.data.remote.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.herald.moviestask.data.remote.RetroService
import com.herald.moviestask.domain.models.MoviesModel

class MoviePagingSource(
    private val retroService: RetroService,
    private val onError: (Exception) -> Unit
) : PagingSource<Int, MoviesModel.MovieItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MoviesModel.MovieItem> {
        val page = params.key ?: 1
        return try {
            val response = retroService.getPopularMovies(page)
            val movieData = response.toMovies().movieListItems
            Log.i("TAG", "load: items page: $page ")
            LoadResult.Page(
                data = movieData,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page < response.totalPages) page + 1 else null
            )
        } catch (e: Exception) {
            onError(e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MoviesModel.MovieItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }
}