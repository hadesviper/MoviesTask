package com.herald.moviestask.domain.remote.usecases

import com.herald.moviestask.common.Resource
import com.herald.moviestask.domain.remote.repository.RetroRepository
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class FetchMoviesUseCase @Inject constructor(
    private val retroRepository: RetroRepository
){
    suspend operator fun invoke(page:Int) = flow {
        try {
            emit(Resource.Loading())
            val result = retroRepository.getAllMovies(page)
            emit(Resource.Success(result))
        }
        catch (e: HttpException){
            emit(Resource.Error(error = e))
        }
        catch (e: IOException){
            emit(Resource.Error(error = e))
        }
    }
}