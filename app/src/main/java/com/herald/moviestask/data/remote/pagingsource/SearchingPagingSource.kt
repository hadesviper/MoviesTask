package com.herald.moviestask.data.remote.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.herald.moviestask.data.remote.RetroService
import com.herald.moviestask.domain.models.MoviesModel

class SearchingPagingSource(
    private val retroService: RetroService,
    private val query: String,
) : PagingSource<Int, MoviesModel.MovieItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MoviesModel.MovieItem> {
        val page = params.key ?: 1
        return try {
            if (query.isNotEmpty()){
                val response = retroService.searchMovies(page,query)
                val movieData = response.toMovies().movieListItems
                LoadResult.Page(
                    data = movieData,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page < response.totalPages) page + 1 else null
                )
            }
            else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null,
                )
            }
        } catch (e: Exception) {
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