package com.herald.moviestask.domain.remote.usecases

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.domain.remote.repository.RetroRepository
import com.herald.moviestask.utils.MovieItemDiffCallback
import com.herald.moviestask.utils.NoopListCallback
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FetchPagedMoviesUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var retroRepository: RetroRepository
    private lateinit var fetchPagedMoviesUseCase: FetchPagedMoviesUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        retroRepository = mockk()
        fetchPagedMoviesUseCase = FetchPagedMoviesUseCase(retroRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should return expected PagingData`() = runTest(testDispatcher) {
        val movieItems = listOf(
            MoviesModel.MovieItem(id = 1, title = "Movie One",
                posterPath = null,
                releaseDate = "expetenda",
                voteAverage = "porro"
            ),
            MoviesModel.MovieItem(id = 2, title = "Movie Two",
                posterPath = null,
                releaseDate = "has",
                voteAverage = "civibus"
            )
        )
        val expectedPagingData = PagingData.from(movieItems)

        coEvery { retroRepository.getPopularMovies() } returns flowOf(expectedPagingData)

        val result = fetchPagedMoviesUseCase().first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MovieItemDiffCallback(),
            updateCallback = NoopListCallback(),
            mainDispatcher = testDispatcher,
            workerDispatcher = testDispatcher
        )

        differ.submitData(result)
        testDispatcher.scheduler.advanceUntilIdle()

        val actualItems = differ.snapshot().items
        assertEquals(movieItems, actualItems)

        coVerify(exactly = 1) { retroRepository.getPopularMovies() }
    }
}