package com.herald.moviestask.domain.remote.usecases

import com.herald.moviestask.common.Resource
import com.herald.moviestask.domain.remote.repository.RetroRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchMovieDetailsUseCase @Inject constructor(
    private val retroRepository: RetroRepository
) {
    operator fun invoke(id:Int) = flow {
        emit(Resource.Loading)
        try {
            val movie = retroRepository.getMovieDetails(id)
            emit(Resource.Success(movie))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}