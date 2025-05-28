package com.herald.moviestask.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.herald.moviestask.presentation.components.Screens
import com.herald.moviestask.presentation.movies.MoviesViewModel
import com.herald.moviestask.presentation.movies.ui_components.DetailsScreen
import com.herald.moviestask.presentation.movies.ui_components.SearchScreen
import com.herald.moviestask.presentation.movies.ui_components.main_screen.MainScreen
import com.herald.moviestask.presentation.theme.MoviesTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@OptIn(ExperimentalSharedTransitionApi::class)
class MainActivity : ComponentActivity() {
    private val viewModel: MoviesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoviesTaskTheme {
                SharedTransitionLayout {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screens.MainScreen
                    ) {
                        composable<Screens.MainScreen> {
                            MainScreen(
                                viewModel,
                                this@SharedTransitionLayout,
                                this@composable,
                                { navController.navigate(Screens.SearchScreen) },
                                { navController.navigate(Screens.DetailsScreen(it)) }
                            )
                        }
                        composable<Screens.SearchScreen>{
                            SearchScreen(
                                viewModel,
                                this@SharedTransitionLayout,
                                this@composable,
                                { navController.navigateUp() },
                                { navController.navigate(Screens.DetailsScreen(it)) }
                            )
                        }
                        composable<Screens.DetailsScreen> {
                            val movieID = it.toRoute<Screens.DetailsScreen>()
                            DetailsScreen(
                                movieID.id,
                                viewModel,
                                this@SharedTransitionLayout,
                                this@composable
                            )
                            { navController.navigateUp() }
                        }
                    }
                }

            }
        }
    }
}