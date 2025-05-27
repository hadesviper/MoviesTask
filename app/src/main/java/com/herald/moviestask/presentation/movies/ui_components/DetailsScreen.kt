package com.herald.moviestask.presentation.movies.ui_components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.herald.moviestask.R
import com.herald.moviestask.common.Constants
import com.herald.moviestask.common.Utils.showSnackBar
import com.herald.moviestask.domain.models.MovieModel
import com.herald.moviestask.presentation.components.LoadingBar
import com.herald.moviestask.presentation.components.TextWithIcon
import com.herald.moviestask.presentation.movies.MoviesEvents
import com.herald.moviestask.presentation.movies.MoviesIntents
import com.herald.moviestask.presentation.movies.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DetailsScreen(
    navController: NavController,
    movieID: Int,
    moviesViewModel: MoviesViewModel
) {
    val states by moviesViewModel.movieDetailsStates.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = { DetailsTopAppBar { moviesViewModel.handleIntents(MoviesIntents.NavigateBack) } }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            LoadingBar(states.isLoading)

            states.movie?.let { movie ->
                val imageURL = Constants.BASE_BACK_IMAGE_URL+movie.backdropPath
                AsyncImage(
                    imageURL,
                    "Background Image",
                    contentScale = ContentScale.FillWidth,
                    placeholder = painterResource(R.drawable.image_loading),
                    error = painterResource(R.drawable.no_image),
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
                MovieDetails(movie)
            }
        }
    }
    LaunchedEffect(Unit) {
        moviesViewModel.handleIntents(MoviesIntents.LoadMovieDetails(movieID))
        moviesViewModel.events.collectLatest { res ->
            when (res) {
                is MoviesEvents.ErrorOccurred -> {
                    showSnackBar(snackBarHostState, res.error) {
                        moviesViewModel.handleIntents(MoviesIntents.LoadMovieDetails(movieID))
                    }
                }
                is MoviesEvents.NavigateBack -> { navController.navigateUp() }
                else -> Unit
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsTopAppBar(goBack: ()->Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        title = { Text("Movie Details") },
        navigationIcon = {
            IconButton(onClick = { goBack()})
            {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go Back")
            }
        }
    )
}

@Composable
private fun MovieDetails(movie: MovieModel) {
    Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = movie.title,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                shadow = Shadow(
                    color = MaterialTheme.colorScheme.primary,
                    blurRadius = 20f
                )
            ),
            fontSize = 20.sp,
        )

        Text(
            text = movie.tagline,
            fontSize = 18.sp,
        )

        Text(
            text = "Genre(s): ${movie.genres.joinToString(", ")}",
            fontSize = 18.sp
        )

        BasicDataRow(movie = movie)

        Text(
            text = movie.overview,
            style = TextStyle(fontWeight = FontWeight.Normal),
            fontSize = 16.sp,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun BasicDataRow(movie: MovieModel) {
    val context = LocalContext.current
    val fontSize = MaterialTheme.typography.titleSmall.fontSize
    val iconSize = MaterialTheme.typography.titleSmall.fontSize.value
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        TextWithIcon(
            modifier = Modifier.align(Alignment.CenterVertically),
            Icons.Filled.Star,
            movie.voteAverage,
            fontSize,
            iconSize
        )

        TextWithIcon(
            modifier = Modifier.align(Alignment.CenterVertically),
            Icons.Filled.DateRange,
            movie.releaseDate,
            fontSize,
            iconSize
        )

        TextWithIcon(
            modifier = Modifier.align(Alignment.CenterVertically),
            Icons.Filled.CheckCircle,
            "${movie.runtime.toString().ifEmpty { "-" }} mins",
            fontSize,
            iconSize
        )

        if (movie.ytTrailer.isNotEmpty()){
            Button(
                onClick = {
                    openYoutubeLink(
                        context = context,
                        youtubeID = movie.ytTrailer
                    )
                },
            ) {
                TextWithIcon(
                    icon = Icons.Filled.PlayArrow,
                    text = "Trailer",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    iconSize = MaterialTheme.typography.titleMedium.fontSize.value
                )
            }
        }
    }
}


private fun openYoutubeLink(context: Context, youtubeID: String) {
    context.startActivity(
        Intent(
            Intent.ACTION_VIEW,
            "http://m.youtube.com/watch?v=$youtubeID".toUri()
        )
    )
}
