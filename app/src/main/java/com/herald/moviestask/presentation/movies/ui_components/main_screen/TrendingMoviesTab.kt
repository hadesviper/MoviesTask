package com.herald.moviestask.presentation.movies.ui_components.main_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.herald.moviestask.domain.models.MoviesModel.MovieItem
import com.herald.moviestask.presentation.components.EmptyScreen
import com.herald.moviestask.presentation.components.LoadingBar
import com.herald.moviestask.presentation.movies.MoviesEvents
import com.herald.moviestask.presentation.movies.MoviesIntents
import com.herald.moviestask.presentation.movies.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TrendingMoviesTab(
    moviesViewModel: MoviesViewModel,
    listState: LazyGridState,
    onMovieClick: (MovieItem) -> Unit,
) {
    val state = moviesViewModel.trendingMoviesStates.collectAsState()
    Column {
        when{
            state.value.error != null -> EmptyScreen()
            state.value.isLoading -> LoadingBar(true)
            state.value.movies != null -> {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), state = listState
                ) {
                    items(state.value.movies!!.movieListItems, contentType = { MovieItem::class })
                    {
                        MovieItem(it) { movie ->
                            onMovieClick(movie)
                        }
                    }
                }
            }
        }

    }
    LaunchedEffect(Unit) {
        if (state.value.movies == null) {
            moviesViewModel.handleIntents(MoviesIntents.LoadTrendingMovies(1))
        }
        moviesViewModel.events.collectLatest {
            if (it is MoviesEvents.Retry) {
                moviesViewModel.handleIntents(MoviesIntents.LoadTrendingMovies(1))
            }
        }
    }
}