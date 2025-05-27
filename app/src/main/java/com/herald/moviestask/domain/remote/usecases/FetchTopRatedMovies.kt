package com.herald.moviestask.domain.remote.usecases

import com.herald.moviestask.common.Resource
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.domain.remote.repository.RetroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchTopRatedMovies @Inject constructor(
    private val retroRepository: RetroRepository
) {
    operator fun invoke(page: Int): Flow<Resource<MoviesModel>>  = flow {
        try {
            emit(Resource.Loading())
            val movies = retroRepository.getTopRatedMovies(page)
            emit(Resource.Success(movies))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}