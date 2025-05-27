package com.herald.moviestask.domain.local.usecases

import com.herald.moviestask.common.Resource
import com.herald.moviestask.domain.local.repository.CachingRepo
import com.herald.moviestask.domain.models.MoviesModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchCachedMoviesUseCase @Inject constructor(
    private val cacheRepository: CachingRepo
) {
    operator fun invoke(): Flow<Resource<MoviesModel>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(cacheRepository.getTopRatedMovies()))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}