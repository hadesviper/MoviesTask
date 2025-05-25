package com.herald.moviestask.presentation.movies.ui_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.herald.moviestask.common.Constants
import com.herald.moviestask.common.Utils.showSnackbar
import com.herald.moviestask.domain.remote.models.MoviesModel
import com.herald.moviestask.presentation.components.LoadingBar
import com.herald.moviestask.presentation.components.Screens
import com.herald.moviestask.presentation.movies.MoviesEvents
import com.herald.moviestask.presentation.movies.MoviesIntents
import com.herald.moviestask.presentation.movies.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, moviesViewModel: MoviesViewModel) {
    val movies = moviesViewModel.movies.collectAsLazyPagingItems()
    val listState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val onMovieClick: (MoviesModel.MovieItem) -> Unit = remember { { moviesViewModel.handleIntents(MoviesIntents.OpenMovieDetails(it.id)) } }

    Scaffold(snackbarHost = {
        SnackbarHost(snackbarHostState)
    }, modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar({
            Text("Movies App")
        })
    }) { innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {

            LoadingBar(movies.loadState.refresh is LoadState.Loading)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2), state = listState
            ) {
                items(movies.itemCount, contentType = { MoviesModel.MovieItem::class })
                { index ->
                    movies[index]?.let {
                        MovieItem(it) { movie ->
                            onMovieClick(movie)
                        }
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    LoadingBar(movies.loadState.append is LoadState.Loading)
                }
            }
        }
        LaunchedEffect(Unit) {
            moviesViewModel.events.collectLatest { res ->
                when (res) {
                    is MoviesEvents.ErrorOccurred -> {
                        showSnackbar(snackbarHostState, res.error) {
                            moviesViewModel.handleIntents(MoviesIntents.RetryLoadingData)
                        }
                    }
                    is MoviesEvents.NavigateToMovieDetails -> navController.navigate(Screens.DetailsScreen(res.id))
                    is MoviesEvents.Retry -> movies.retry()
                }
            }
        }
    }
}


@Composable
private fun MovieItem(movie: MoviesModel.MovieItem, onMovieClick: (MoviesModel.MovieItem) -> Unit) {
    Card(
        modifier = Modifier.padding(5.dp), shape = RoundedCornerShape(5)
    ) {
        Column(modifier = Modifier
            .clickable { onMovieClick(movie) }
            .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)) {
            AsyncImage(
                "${Constants.BASE_IMAGE_URL}${movie.posterPath}",
                "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(200.dp)
            )
            Text(movie.title, minLines = 2, maxLines = 2, overflow = TextOverflow.Clip)
            Text("Date: ${movie.releaseDate}", maxLines = 1)
        }
    }
}
