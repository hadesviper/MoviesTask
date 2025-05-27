package com.herald.moviestask.domain.remote.usecases

import androidx.paging.PagingData
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.domain.remote.repository.RetroRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchPagedMoviesUseCase @Inject constructor(
    private val retroRepository: RetroRepository
) {
    operator fun invoke(): Flow<PagingData<MoviesModel.MovieItem>> = retroRepository.getPopularMovies()
}