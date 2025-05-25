package com.herald.moviestask.domain.remote.usecases

import com.herald.moviestask.domain.remote.repository.RetroRepository
import javax.inject.Inject

class FetchMovieSearchUseCase @Inject constructor(
    private val retroRepository: RetroRepository
) {
    operator fun invoke(query: String, onError: (Exception) -> Unit) =
        retroRepository.searchMovies(query, onError)

}