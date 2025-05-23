package com.herald.moviestask.domain.remote.repository

import com.herald.moviestask.domain.remote.models.MoviesModel

interface RetroRepository {
    suspend fun getAllMovies(page: Int): MoviesModel
}