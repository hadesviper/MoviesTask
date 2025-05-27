package com.herald.moviestask.domain.local.usecases

import com.herald.moviestask.domain.local.repository.CachingRepo
import javax.inject.Inject

class DeleteCacheUseCase @Inject constructor(
    private val cacheRepository: CachingRepo
) {
    suspend operator fun invoke() {
        try {
            cacheRepository.deleteAllMovies()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}