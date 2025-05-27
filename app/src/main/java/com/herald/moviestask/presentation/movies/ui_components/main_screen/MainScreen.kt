package com.herald.moviestask.presentation.movies.ui_components.main_screen

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
import androidx.navigation.NavHostController
import com.herald.moviestask.common.Utils.showSnackBar
import com.herald.moviestask.presentation.components.Screens
import com.herald.moviestask.presentation.movies.MoviesEvents
import com.herald.moviestask.presentation.movies.MoviesIntents
import com.herald.moviestask.presentation.movies.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, moviesViewModel: MoviesViewModel) {
    val listStatePopular = rememberLazyGridState()
    val listStateTopRated = rememberLazyGridState()
    val snackBarHostState = remember { SnackbarHostState() }
    val tabs = remember { TabItem.entries.toTypedArray() }
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(snackbarHost = {
        SnackbarHost(snackBarHostState)
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
                tabs,
                pagerState,
                coroutineScope
            )
        }

        LaunchedEffect(Unit) {
            moviesViewModel.events.collectLatest { res ->
                when (res) {
                    is MoviesEvents.ErrorOccurred -> {
                        showSnackBar(snackBarHostState, res.error) {
                            moviesViewModel.handleIntents(MoviesIntents.RetryLoadingData)
                        }
                    }

                    is MoviesEvents.NavigateToMovieDetails -> navController.navigate(
                        Screens.DetailsScreen(
                            res.id
                        )
                    )
                    is MoviesEvents.NavigateToMovieSearch -> navController.navigate(Screens.SearchScreen)
                    else -> Unit
                }
            }
        }
    }
}
