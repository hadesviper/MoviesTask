package com.herald.moviestask.domain.local.usecases

import com.herald.moviestask.common.Resource
import com.herald.moviestask.domain.local.repository.CachingRepo
import com.herald.moviestask.domain.models.MoviesModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CacheMoviesUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var cachingRepo: CachingRepo
    private lateinit var cacheMoviesUseCase: CacheMoviesUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        cachingRepo = mockk()
        cacheMoviesUseCase = CacheMoviesUseCase(cachingRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should return Success when caching is successful`() = runTest(testDispatcher) {
        val movies = listOf(
            MoviesModel.MovieItem(
                id = 1, title = "Test Movie",
                posterPath = null,
                releaseDate = "comprehensam",
                voteAverage = "voluptatum",
            )
        )
        coEvery { cachingRepo.saveTopRatedMovie(movies) } just Runs

        val result = cacheMoviesUseCase(movies)

        assertTrue(result is Resource.Success)
        coVerify(exactly = 1) { cachingRepo.saveTopRatedMovie(movies) }
    }

    @Test
    fun `invoke should return Error when exception is thrown`() = runTest(testDispatcher) {
        val movies = listOf(
            MoviesModel.MovieItem(
                id = 1, title = "Test Movie",
                posterPath = null,
                releaseDate = "comprehensam",
                voteAverage = "voluptatum",
            )
        )
        val exception = RuntimeException("Cache failed")
        coEvery { cachingRepo.saveTopRatedMovie(movies) } throws exception

        val result = cacheMoviesUseCase(movies)

        assertTrue(result is Resource.Error)
        assertEquals(exception, (result as Resource.Error).error)
        coVerify(exactly = 1) { cachingRepo.saveTopRatedMovie(movies) }
    }
}
