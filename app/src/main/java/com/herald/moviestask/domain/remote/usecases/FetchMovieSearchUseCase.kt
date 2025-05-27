package com.herald.moviestask.domain.remote.usecases

import androidx.paging.PagingData
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.domain.remote.repository.RetroRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchMovieSearchUseCase @Inject constructor(
    private val retroRepository: RetroRepository
) {
    operator fun invoke(query: String): Flow<PagingData<MoviesModel.MovieItem>> = retroRepository.searchMovies(query)
}