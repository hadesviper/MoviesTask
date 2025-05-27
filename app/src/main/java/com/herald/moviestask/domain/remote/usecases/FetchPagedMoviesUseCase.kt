package com.herald.moviestask.domain.remote.usecases

import com.herald.moviestask.domain.remote.repository.RetroRepository
import javax.inject.Inject

class FetchPagedMoviesUseCase @Inject constructor(
    private val retroRepository: RetroRepository
) {
    operator fun invoke() = retroRepository.getPopularMovies()
}