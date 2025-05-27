package com.herald.moviestask.domain.local.usecases

import com.herald.moviestask.common.Resource
import com.herald.moviestask.domain.local.repository.CachingRepo
import com.herald.moviestask.domain.models.MoviesModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FetchCachedMoviesUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var cacheRepository: CachingRepo
    private lateinit var fetchCachedMoviesUseCase: FetchCachedMoviesUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        cacheRepository = mockk()
        fetchCachedMoviesUseCase = FetchCachedMoviesUseCase(cacheRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should emit Loading then Success`() = runTest(testDispatcher) {
        val movies = MoviesModel(
            listOf(MoviesModel.MovieItem(id = 1, title = "Movie A", posterPath = null, releaseDate = "2023-01-01", voteAverage = "8.5"))
        )
        coEvery { cacheRepository.getTopRatedMovies() } returns movies

        val result = fetchCachedMoviesUseCase().toList()

        assertEquals(2, result.size)
        assertEquals(Resource.Loading, result[0])

        val success = result[1]
        assertTrue(success is Resource.Success)
        assertEquals(movies, (success as Resource.Success).data)

        coVerify(exactly = 1) { cacheRepository.getTopRatedMovies() }
    }

    @Test
    fun `invoke should emit Loading then Error`() = runTest(testDispatcher) {
        val exception = RuntimeException("DB failure")
        coEvery { cacheRepository.getTopRatedMovies() } throws exception

        val result = fetchCachedMoviesUseCase().toList()

        assertEquals(2, result.size)
        assertEquals(Resource.Loading, result[0])

        val error = result[1]
        assertTrue(error is Resource.Error)
        assertEquals(exception, (error as Resource.Error).error)

        coVerify(exactly = 1) { cacheRepository.getTopRatedMovies() }
    }
}
