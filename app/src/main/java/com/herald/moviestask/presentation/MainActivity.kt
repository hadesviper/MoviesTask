package com.herald.moviestask.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.herald.moviestask.domain.remote.models.MoviesModel
import com.herald.moviestask.presentation.components.Screens
import com.herald.moviestask.presentation.movies.MoviesViewModel
import com.herald.moviestask.presentation.movies.ui_components.DetailsScreen
import com.herald.moviestask.presentation.movies.ui_components.MainScreen
import com.herald.moviestask.presentation.ui.theme.MoviesTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MoviesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoviesTaskTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screens.MainScreen
                ){
                    composable<Screens.MainScreen> { MainScreen(navController, viewModel) }
                    composable<MoviesModel.MovieData> {
                        val movie = it.toRoute<MoviesModel.MovieData>()
                        DetailsScreen(navController,movie)
                    }
                }
            }
        }
    }
}