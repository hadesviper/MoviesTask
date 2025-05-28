package com.herald.moviestask.presentation.movies.ui_components.main_screen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.herald.moviestask.domain.models.MoviesModel.MovieItem
import com.herald.moviestask.presentation.components.EmptyScreen
import com.herald.moviestask.presentation.components.LoadingBar
import com.herald.moviestask.presentation.components.MovieItem
import com.herald.moviestask.presentation.movies.MoviesEvents
import com.herald.moviestask.presentation.movies.MoviesIntents
import com.herald.moviestask.presentation.movies.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TopRatedMoviesTab(
    moviesViewModel: MoviesViewModel,
    listState: LazyGridState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onMovieClick: (MovieItem) -> Unit
) {
    val state = moviesViewModel.topRatedMoviesState.collectAsState()
    Column {
        when{
            state.value.movies?.movieListItems.isNullOrEmpty() -> EmptyScreen()
            state.value.isLoading -> LoadingBar(true)
            state.value.movies != null -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), state = listState
                ) {
                    items(state.value.movies!!.movieListItems, contentType = { MovieItem::class })
                    {
                        MovieItem(
                            it,
                            sharedTransitionScope,
                            animatedContentScope,
                            onMovieClick
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }){
                        Text(modifier = Modifier.padding(10.dp),text = "This tab loads the first page only unlike the other tab, this one is for caching demonstration with room and it applies the caching flow mentioned in the task description, the other one uses paging 3 in order to load more pages and it uses the regular http request caching")
                    }
                }
            }
        }

    }
    LaunchedEffect(Unit) {
        if (state.value.movies == null) {
            moviesViewModel.handleIntents(MoviesIntents.LoadTopRatedMovies(1))
        }
        moviesViewModel.events.collectLatest {
            if (it is MoviesEvents.Retry) {
                moviesViewModel.handleIntents(MoviesIntents.LoadTopRatedMovies(1))
            }
        }
    }
}