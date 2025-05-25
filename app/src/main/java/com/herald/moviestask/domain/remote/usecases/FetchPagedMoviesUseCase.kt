package com.herald.moviestask.domain.remote.usecases

import androidx.paging.PagingData
import com.herald.moviestask.domain.remote.models.MoviesModel
import com.herald.moviestask.domain.remote.repository.RetroRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchPagedMoviesUseCase @Inject constructor(
    private val retroRepository: RetroRepository
) {
    operator fun invoke(onError: (Exception) -> Unit): Flow<PagingData<MoviesModel.MovieItem>> {
        return retroRepository.getPopularMovies(onError)
    }
}