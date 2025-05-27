package com.herald.moviestask.presentation.movies.ui_components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.herald.moviestask.R
import com.herald.moviestask.common.Constants
import com.herald.moviestask.common.Utils.showSnackBar
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.presentation.components.EmptyScreen
import com.herald.moviestask.presentation.components.LoadingBar
import com.herald.moviestask.presentation.components.Screens
import com.herald.moviestask.presentation.movies.MoviesEvents
import com.herald.moviestask.presentation.movies.MoviesIntents
import com.herald.moviestask.presentation.movies.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchScreen(navController: NavHostController, moviesViewModel: MoviesViewModel) {
    val movies = moviesViewModel.searchResult.collectAsLazyPagingItems()
    var searchText = remember { mutableStateOf("") }
    val listState = rememberLazyGridState()
    val snackBarHostState = remember { SnackbarHostState() }
    val onMovieClick: (MoviesModel.MovieItem) -> Unit = remember { { moviesViewModel.handleIntents(MoviesIntents.OpenMovieDetails(it.id)) } }
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(snackbarHost = {
        SnackbarHost(snackBarHostState)
    }, modifier = Modifier.fillMaxSize(), topBar = {
        SearchTopBar(searchText,moviesViewModel::handleIntents)
    }) { innerPadding ->

        Column(modifier = Modifier
            .padding(innerPadding)
            .clickable(indication = null, interactionSource = null) { keyboardController?.hide() }) {

            LoadingBar((movies.loadState.refresh is LoadState.Loading) && searchText.value.isNotEmpty())

            when(movies.itemCount){
                0 -> EmptyScreen()
                else ->
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), state = listState,
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

        }
        BackHandler {
            moviesViewModel.handleIntents(MoviesIntents.NavigateBack)
        }
        LaunchedEffect(Unit) {
            moviesViewModel.events.collectLatest { res ->
                when (res) {
                    is MoviesEvents.ErrorOccurred -> {
                        showSnackBar(snackBarHostState, res.error) {
                            moviesViewModel.handleIntents(MoviesIntents.RetryLoadingData)
                        }
                    }
                    is MoviesEvents.NavigateToMovieDetails -> navController.navigate(Screens.DetailsScreen(res.id))
                    is MoviesEvents.Retry -> movies.retry()
                    is MoviesEvents.NavigateBack -> {
                        navController.navigateUp()
                        keyboardController?.hide()
                        moviesViewModel.handleIntents(MoviesIntents.OnSearchQueryChanged(""))
                    }
                    else -> Unit
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(searchText: MutableState<String>, handleIntents: (MoviesIntents) -> Unit) {
    TopAppBar(
        {
            TextField(
                value = searchText.value,
                onValueChange =
                    {
                        searchText.value = it
                        handleIntents(MoviesIntents.OnSearchQueryChanged(searchText.value))
                    },
                placeholder = { Text("Search movies...") },
                singleLine = true,
            )
        }, navigationIcon = {
            IconButton(onClick = { handleIntents(MoviesIntents.NavigateBack) })
            {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go Back")
            }
        },
        actions = {
            AnimatedVisibility(visible = searchText.value.isNotEmpty())
            {
                IconButton(onClick = {
                    searchText.value = ""
                    handleIntents(MoviesIntents.OnSearchQueryChanged(searchText.value))
                })
                {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        }
    )
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
                contentScale = ContentScale.FillWidth,
                placeholder = painterResource(R.drawable.image_loading),
                error = painterResource(R.drawable.no_image),
                modifier = Modifier.height(200.dp)
            )
            Text(movie.title, minLines = 1, maxLines = 1, overflow = TextOverflow.Clip)
            Text("Date: ${movie.releaseDate}", maxLines = 1)
        }
    }
}