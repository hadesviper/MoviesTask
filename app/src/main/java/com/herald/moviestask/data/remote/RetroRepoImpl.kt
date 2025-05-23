package com.herald.moviestask.data.remote

import com.herald.moviestask.domain.remote.models.MoviesModel
import com.herald.moviestask.domain.remote.repository.RetroRepository
import javax.inject.Inject

class RetroRepoImpl @Inject constructor(
    private val retroService: RetroService
): RetroRepository {
    override suspend fun getAllMovies(page: Int): MoviesModel {
        return retroService.getPopularMovies(page).toMovies()
    }
}