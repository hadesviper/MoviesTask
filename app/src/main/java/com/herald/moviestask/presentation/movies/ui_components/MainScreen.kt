package com.herald.moviestask.presentation.movies.ui_components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.herald.moviestask.common.Constants
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
    val state by moviesViewModel.state.collectAsState()
    val listState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val onMovieClick: (MoviesModel.MovieData) -> Unit = remember { { moviesViewModel.handleIntents(MoviesIntents.OpenMovieDetails(it)) } }

    Scaffold(snackbarHost = {
        SnackbarHost(snackbarHostState)
    }, modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar({
            Text("Movies App")
        })
    }) { innerPadding ->
        when {
            state.isLoading -> {
                LoadingBar()
            }
        }

        Column(modifier = Modifier.padding(innerPadding)) {
            state.movies?.let { results ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), state = listState
                ) {
                    items(results,
                        key = { it.id },
                        contentType = { MoviesModel.MovieData::class }) {
                        MovieItem(it) { movie ->
                            onMovieClick(movie)
                        }
                    }
                }
            }

        }
        LaunchedEffect(Unit) {
            moviesViewModel.events.collectLatest { res ->
                when (res) {
                    is MoviesEvents.ErrorOccurred -> {
                        val result = snackbarHostState.showSnackbar(
                            "error",
                            actionLabel = "retry",
                        )
                        when (result) {
                            SnackbarResult.Dismissed -> TODO()
                            SnackbarResult.ActionPerformed -> moviesViewModel.handleIntents(MoviesIntents.FetchFirstMovies(1))
                        }
                    }
                    is MoviesEvents.NavigateToMovieDetails -> {
                        Toast.makeText(context, res.movie.title, Toast.LENGTH_SHORT).show()
                        navController.navigate(Screens.DetailsScreen)
                    }
                }
            }
        }
    }
}


@Composable
private fun MovieItem(movie: MoviesModel.MovieData, onMovieClick: (MoviesModel.MovieData) -> Unit) {
    Card(
        modifier = Modifier.padding(5.dp),
        shape = RoundedCornerShape(5)
    ) {
        Column(
            modifier = Modifier
                .clickable { onMovieClick(movie) }
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
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