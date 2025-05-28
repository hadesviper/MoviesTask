package com.herald.moviestask.presentation.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.herald.moviestask.R
import com.herald.moviestask.common.Constants
import com.herald.moviestask.domain.models.MoviesModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MovieItem(
    movie: MoviesModel.MovieItem,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onMovieClick: (MoviesModel.MovieItem) -> Unit
) {
    with(sharedTransitionScope) {
        Card(
            modifier =
                Modifier
                    .padding(5.dp)
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = movie.id),
                        animatedVisibilityScope = animatedContentScope
                    ), shape = RoundedCornerShape(5)
        ) {
            Column(
                modifier = Modifier
                    .clickable { onMovieClick(movie) }
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)) {
                AsyncImage(
                    Constants.BASE_IMAGE_URL + movie.posterPath,
                    "Movie Item ${movie.title}",
                    contentScale = ContentScale.FillWidth,
                    placeholder = painterResource(R.drawable.image_loading),
                    error = painterResource(R.drawable.no_image),
                    modifier = Modifier.height(200.dp)
                )
                Text(movie.title, minLines = 1, maxLines = 1, overflow = TextOverflow.Ellipsis)
                TextWithIcon(
                    icon = Icons.Default.DateRange,
                    text = movie.releaseDate,
                    fontSize = 16.sp,
                    iconSize = 16f
                )
            }
        }
    }
}