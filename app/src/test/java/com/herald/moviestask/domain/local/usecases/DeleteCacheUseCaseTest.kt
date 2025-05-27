package com.herald.moviestask.domain.local.usecases

import com.herald.moviestask.common.Resource
import com.herald.moviestask.domain.local.repository.CachingRepo
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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteCacheUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var cacheRepository: CachingRepo
    private lateinit var deleteCacheUseCase: DeleteCacheUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        cacheRepository = mockk()
        deleteCacheUseCase = DeleteCacheUseCase(cacheRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should return Success when deletion is successful`() = runTest(testDispatcher) {
        coEvery { cacheRepository.deleteAllMovies() } just Runs

        val result = deleteCacheUseCase()

        assertTrue(result is Resource.Success)
        coVerify(exactly = 1) { cacheRepository.deleteAllMovies() }
    }

    @Test
    fun `invoke should return Error when deletion throws exception`() = runTest(testDispatcher) {
        val exception = RuntimeException("Delete failed")
        coEvery { cacheRepository.deleteAllMovies() } throws exception

        val result = deleteCacheUseCase()

        assertTrue(result is Resource.Error)
        assertEquals(exception, (result as Resource.Error).error)
        coVerify(exactly = 1) { cacheRepository.deleteAllMovies() }
    }
}