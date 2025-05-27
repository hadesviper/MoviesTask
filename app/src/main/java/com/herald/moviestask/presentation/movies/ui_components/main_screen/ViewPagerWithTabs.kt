package com.herald.moviestask.presentation.movies.ui_components.main_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.presentation.movies.MoviesIntents
import com.herald.moviestask.presentation.movies.MoviesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPagerWithTabs(
    moviesViewModel: MoviesViewModel,
    listStateTrending: LazyGridState,
    listStatePopular: LazyGridState,
    tabs: Array<TabItem>,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
) {
    val onMovieClick: (MoviesModel.MovieItem) -> Unit = remember { { moviesViewModel.handleIntents(MoviesIntents.OpenMovieDetails(it.id)) } }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(tab.title) },
                )
            }
        }
        HorizontalDivider()
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (tabs[page]) {
                TabItem.Popular -> PopularMoviesTab(
                    moviesViewModel,
                    listStatePopular,
                    onMovieClick
                )
                TabItem.Trending -> TrendingMoviesTab(
                    moviesViewModel,
                    listStateTrending,
                    onMovieClick
                )
            }
        }
    }
}

enum class TabItem(val title: String) {
    Popular("Popular"),
    Trending("Top Rated"),
}