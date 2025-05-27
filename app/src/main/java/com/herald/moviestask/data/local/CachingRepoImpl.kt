package com.herald.moviestask.data.local

import com.herald.moviestask.data.local.entities.MoviesEntity
import com.herald.moviestask.domain.local.repository.CachingRepo
import com.herald.moviestask.domain.models.MoviesModel

import javax.inject.Inject

class CachingRepoImpl @Inject constructor(
    private val moviesDao: MoviesDao
): CachingRepo {

    override suspend fun saveTrendingMovie(movies: List<MoviesModel.MovieItem>) {
        movies.forEachIndexed {index, movie ->
            moviesDao.addMovie(
                MoviesEntity(
                    id = movie.id,
                    index = index,
                    posterPath = movie.posterPath,
                    releaseDate = movie.releaseDate,
                    title = movie.title,
                    voteAverage = movie.voteAverage
                )
            )
        }
    }

    override suspend fun deleteAllMovies() {
        moviesDao.deleteAllMovies()
    }

    override suspend fun getTrendingMovies(): MoviesModel {
        val movies = moviesDao.getAllMovies()
        return MoviesModel(
            movieListItems = movies.map {
                it.toMoviesModel()
            }
        )
    }
}