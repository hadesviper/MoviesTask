package com.herald.moviestask.presentation.movies.ui_components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.herald.moviestask.common.Utils.showSnackBar
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.presentation.components.EmptyScreen
import com.herald.moviestask.presentation.components.LoadingBar
import com.herald.moviestask.presentation.components.Screens
import com.herald.moviestask.presentation.movies.MoviesEvents
import com.herald.moviestask.presentation.movies.MoviesIntents
import com.herald.moviestask.presentation.movies.MoviesViewModel
import com.herald.moviestask.presentation.movies.ui_components.main_screen.MovieItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

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

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .clickable(
                    indication = null,
                    interactionSource = null
                ) { keyboardController?.hide() }) {

            LoadingBar((movies.loadState.refresh is LoadState.Loading) && searchText.value.isNotEmpty())

            when (movies.itemCount) {
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
            snapshotFlow { movies.loadState }
                .distinctUntilChanged()
                .collectLatest { loadStates ->
                    val refreshError = (loadStates.refresh as? LoadState.Error)?.error
                    val appendError = (loadStates.append as? LoadState.Error)?.error
                    val error = refreshError ?: appendError
                    error?.let { moviesViewModel.handleIntents(MoviesIntents.OnErrorOccurred(exception = it)) }
                }
        }
        LaunchedEffect(Unit) {
            moviesViewModel.events.collectLatest { res ->
                when (res) {
                    is MoviesEvents.ErrorOccurred -> {
                        keyboardController?.hide()
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
    TopAppBar(modifier = Modifier.padding(top = 16.dp), title =
        {
            SearchTextField(
                query = searchText.value,
                onQueryChange = {
                    searchText.value = it
                    handleIntents(MoviesIntents.OnSearchQueryChanged(searchText.value))
                },
                onClear = {
                    searchText.value = ""
                    handleIntents(MoviesIntents.OnSearchQueryChanged(searchText.value))
                }
            )
        }, navigationIcon = {
            IconButton(onClick = { handleIntents(MoviesIntents.NavigateBack) })
            {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go Back")
            }
        }
    )
}

@Composable
private fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "Search..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(hint) },
        singleLine = true,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            AnimatedVisibility(
                query.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                IconButton(onClick = onClear) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
    )
}