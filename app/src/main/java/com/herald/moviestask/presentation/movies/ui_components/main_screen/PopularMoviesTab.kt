package com.herald.moviestask.presentation.movies.ui_components.main_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.herald.moviestask.domain.models.MoviesModel.MovieItem
import com.herald.moviestask.presentation.components.EmptyScreen
import com.herald.moviestask.presentation.components.LoadingBar
import com.herald.moviestask.presentation.movies.MoviesEvents
import com.herald.moviestask.presentation.movies.MoviesIntents
import com.herald.moviestask.presentation.movies.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PopularMoviesTab(
    moviesViewModel: MoviesViewModel,
    listState: LazyGridState,
    onMovieClick: (MovieItem) -> Unit
) {
    val movies = moviesViewModel.movies.collectAsLazyPagingItems()
    Column {
        LoadingBar(movies.loadState.refresh is LoadState.Loading)
        EmptyScreen(movies.itemCount == 0)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), state = listState
        ) {
            items(movies.itemCount, contentType = { MovieItem::class })
            { index ->
                movies[index]?.let {
                    MovieItem(it,onMovieClick)
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                LoadingBar(movies.loadState.append is LoadState.Loading)
            }
        }
    }
    LaunchedEffect(movies.loadState) {
        val refreshError = (movies.loadState.refresh as? LoadState.Error)?.error
        val appendError = (movies.loadState.append as? LoadState.Error)?.error
        val error = refreshError ?: appendError
        error?.let { moviesViewModel.handleIntents(MoviesIntents.OnErrorOccurred(exception = it)) }
    }
    LaunchedEffect(Unit) {
        moviesViewModel.events.collectLatest {
            if (it is MoviesEvents.Retry) {
                movies.retry()
            }
        }
    }
}
