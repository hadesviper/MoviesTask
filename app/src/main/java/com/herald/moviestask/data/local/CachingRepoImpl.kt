package com.herald.moviestask.data.local

import com.herald.moviestask.data.local.mappers.toMovieEntity
import com.herald.moviestask.data.local.mappers.toMoviesModel
import com.herald.moviestask.domain.local.repository.CachingRepo
import com.herald.moviestask.domain.models.MoviesModel
import javax.inject.Inject

class CachingRepoImpl @Inject constructor(
    private val moviesDao: MoviesDao
): CachingRepo {

    override suspend fun saveTopRatedMovie(movies: List<MoviesModel.MovieItem>) {
        val movieEntities = movies.mapIndexed { index, movie ->
            movie.toMovieEntity(index)
        }
        moviesDao.addMovies(movieEntities)
    }

    override suspend fun deleteAllMovies() {
        moviesDao.deleteAllMovies()
    }

    override suspend fun getTopRatedMovies(): MoviesModel {
        val movieEntities = moviesDao.getAllMovies()
        return MoviesModel(
            movieListItems = movieEntities.map { it.toMoviesModel() }
        )
    }
}