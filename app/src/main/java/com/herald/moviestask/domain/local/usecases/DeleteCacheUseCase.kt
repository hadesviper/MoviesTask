package com.herald.moviestask.domain.local.usecases

import com.herald.moviestask.common.Resource
import com.herald.moviestask.domain.local.repository.CachingRepo
import javax.inject.Inject

class DeleteCacheUseCase @Inject constructor(
    private val cacheRepository: CachingRepo
) {
    suspend operator fun invoke(): Resource<Unit> {
        try {
            cacheRepository.deleteAllMovies()
            return Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(e)
        }
    }
}