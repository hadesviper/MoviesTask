package com.herald.moviestask.domain.local.usecases

import com.herald.moviestask.domain.local.repository.CachingRepo
import com.herald.moviestask.domain.models.MoviesModel
import javax.inject.Inject

class CacheMoviesUseCase @Inject constructor(
    private val cachingRepo: CachingRepo
){
    suspend operator fun invoke(movie: List<MoviesModel.MovieItem>) {
        try {
            cachingRepo.saveTopRatedMovie(movie)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
}