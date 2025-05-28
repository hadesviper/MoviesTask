package com.herald.moviestask.presentation.movies.ui_components.main_screen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.herald.moviestask.common.Utils.showRetrySnackbar
import com.herald.moviestask.presentation.movies.MoviesEvents
import com.herald.moviestask.presentation.movies.MoviesIntents
import com.herald.moviestask.presentation.movies.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(
    moviesViewModel: MoviesViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navigateToSearch: () -> Unit,
    navigateToDetails: (Int) -> Unit
) {
    val listStatePopular = rememberLazyGridState()
    val listStateTopRated = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val tabs = remember { TabItem.entries.toTypedArray() }
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(snackbarHost = {
        SnackbarHost(snackbarHostState)
    }, modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            {
                Text("Movies App")
            },
            actions = {
                IconButton(onClick = { moviesViewModel.handleIntents(MoviesIntents.OpenMovieSearch) }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        )
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ViewPagerWithTabs(
                moviesViewModel,
                listStateTopRated,
                listStatePopular,
                sharedTransitionScope,
                animatedContentScope,
                tabs,
                pagerState,
                coroutineScope
            )
        }

        LaunchedEffect(Unit) {
            moviesViewModel.events.collectLatest { event ->
                when (event) {
                    is MoviesEvents.ErrorOccurred -> {
                        showRetrySnackbar(snackbarHostState, event.error) {
                            moviesViewModel.handleIntents(MoviesIntents.RetryLoadingData)
                        }
                    }

                    is MoviesEvents.NavigateToMovieDetails -> navigateToDetails(event.id)
                    is MoviesEvents.NavigateToMovieSearch -> navigateToSearch()
                    else -> Unit
                }
            }
        }
    }
}
