package com.herald.moviestask.domain.local.usecases

import com.herald.moviestask.common.Resource
import com.herald.moviestask.domain.local.repository.CachingRepo
import com.herald.moviestask.domain.models.MoviesModel
import javax.inject.Inject

class CacheMoviesUseCase @Inject constructor(
    private val cachingRepo: CachingRepo
){
    suspend operator fun invoke(movie: List<MoviesModel.MovieItem>): Resource<Unit>  {
        try {
            cachingRepo.saveTopRatedMovie(movie)
            return Resource.Success(Unit)
        }
        catch (e:Exception){
            e.printStackTrace()
            return Resource.Error(e)
        }
    }
}