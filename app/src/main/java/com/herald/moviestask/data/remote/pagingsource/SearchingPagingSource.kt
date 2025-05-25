package com.herald.moviestask.data.remote.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.herald.moviestask.data.remote.RetroService
import com.herald.moviestask.domain.remote.models.MoviesModel

class SearchingPagingSource(
    private val retroService: RetroService,
    private val query: String,
    private val onError: (Exception) -> Unit
) : PagingSource<Int, MoviesModel.MovieItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MoviesModel.MovieItem> {
        val page = params.key ?: 1
        return try {
            val movieData = retroService.searchMovies(page,query).toMovies().movieListItem
            Log.i("TAG", "load: items page: $page ")
            LoadResult.Page(
                data = movieData,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (movieData.isEmpty()) null else page + 1
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