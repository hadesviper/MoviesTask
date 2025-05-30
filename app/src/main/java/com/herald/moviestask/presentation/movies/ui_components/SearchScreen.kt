package com.herald.moviestask.presentation.movies.ui_components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.herald.moviestask.common.Utils.showRetrySnackbar
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.presentation.components.EmptyScreen
import com.herald.moviestask.presentation.components.LoadingBar
import com.herald.moviestask.presentation.components.MovieItem
import com.herald.moviestask.presentation.movies.MoviesEvents
import com.herald.moviestask.presentation.movies.MoviesIntents
import com.herald.moviestask.presentation.movies.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    moviesViewModel: MoviesViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navigateUp: () -> Unit,
    navigateToDetails: (Int) -> Unit
) {
    val movies = moviesViewModel.searchResult.collectAsLazyPagingItems()
    val searchText = remember { mutableStateOf("") }
    val listState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val onMovieClick: (MoviesModel.MovieItem) -> Unit = remember { { moviesViewModel.handleIntents(MoviesIntents.OpenMovieDetails(it.id)) } }
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(snackbarHost = {
        SnackbarHost(snackbarHostState)
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
                                MovieItem(it, sharedTransitionScope, animatedContentScope, onMovieClick)
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
        LaunchedEffect(movies.loadState) {
            val refreshError = (movies.loadState.refresh as? LoadState.Error)?.error
            val appendError = (movies.loadState.append as? LoadState.Error)?.error
            val error = refreshError ?: appendError
            error?.let { moviesViewModel.handleIntents(MoviesIntents.OnErrorOccurred(exception = it)) }
        }
        LaunchedEffect(Unit) {
            moviesViewModel.events.collectLatest { event ->
                when (event) {
                    is MoviesEvents.ErrorOccurred -> {
                        keyboardController?.hide()
                        showRetrySnackbar(snackbarHostState, event.error) {
                            moviesViewModel.handleIntents(MoviesIntents.RetryLoadingData)
                        }
                    }
                    is MoviesEvents.NavigateToMovieDetails -> navigateToDetails(event.id)
                    is MoviesEvents.Retry -> movies.retry()
                    is MoviesEvents.NavigateBack -> {
                        navigateUp()
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
        modifier = Modifier.padding(top = 16.dp),
        title =
            {
                SearchTextField(
                    query = searchText.value,
                    onQueryChange = {
                        searchText.value = it
                        handleIntents(MoviesIntents.OnSearchQueryChanged(searchText.value)) },
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
            .padding(horizontal = 16.dp)
    )
}