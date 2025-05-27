package com.herald.moviestask.domain.remote.usecases

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.domain.remote.repository.RetroRepository
import com.herald.moviestask.utils.MovieItemDiffCallback
import com.herald.moviestask.utils.NoopListCallback
import io.mockk.coEvery
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
class FetchMovieSearchUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var retroRepository: RetroRepository
    private lateinit var fetchMovieSearchUseCase: FetchMovieSearchUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        retroRepository = mockk()
        fetchMovieSearchUseCase = FetchMovieSearchUseCase(retroRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should emit expected PagingData`() = runTest(testDispatcher) {
        val query = "Batman"
        val movieItems = listOf(
            MoviesModel.MovieItem(id = 1, title = "Batman Begins",
                posterPath = null,
                releaseDate = "te",
                voteAverage = "nihil"
            ),
            MoviesModel.MovieItem(id = 2, title = "The Dark Knight",
                posterPath = null,
                releaseDate = "has",
                voteAverage = "dolorum"
            )
        )
        val expectedPagingData = PagingData.from(movieItems)

        coEvery { retroRepository.searchMovies(query) } returns flowOf(expectedPagingData)

        val result = fetchMovieSearchUseCase(query).first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MovieItemDiffCallback(),
            updateCallback = NoopListCallback(),
            mainDispatcher = testDispatcher,
            workerDispatcher = testDispatcher
        )

        differ.submitData(result)
        testDispatcher.scheduler.advanceUntilIdle()

        val resultList = differ.snapshot().items
        assertEquals(movieItems, resultList)
    }
}