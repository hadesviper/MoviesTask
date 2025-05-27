package com.herald.moviestask.domain.remote.usecases

import com.herald.moviestask.common.Resource
import com.herald.moviestask.domain.models.MovieModel
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.domain.remote.repository.RetroRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.net.ssl.SSLPeerUnverifiedException

@OptIn(ExperimentalCoroutinesApi::class)
class FetchTopRatedMoviesUseCaseTest {

    private lateinit var retroRepo: RetroRepository
    private lateinit var fetchTopRatedMoviesUseCase: FetchTopRatedMoviesUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        retroRepo = mockk()
        fetchTopRatedMoviesUseCase = FetchTopRatedMoviesUseCase(retroRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Data returned Successfully`() = runTest {
        val moviesModel = MoviesModel(
            listOf(
                MoviesModel.MovieItem(
                    id = 7019,
                    releaseDate = "ligula",
                    title = "scripta",
                    voteAverage = "dis",
                    posterPath = "epicurei"
                )
            )
        )
        coEvery { retroRepo.getTopRatedMovies(1) } returns moviesModel

        val result = fetchTopRatedMoviesUseCase(1).toList()

        assert(result[0] is Resource.Loading)
        assert(result[1] is Resource.Success)
        assertEquals((result[1] as Resource.Success).data,moviesModel)
    }

    @Test
    fun `Data Returns HttpException`() = runTest{
        coEvery { retroRepo.getTopRatedMovies(1) } throws HttpException(Response.error<String>(404,"err".toResponseBody()))
        val result = fetchTopRatedMoviesUseCase(1).toList()
        assert(result[0] is Resource.Loading)
        assert(result[1] is Resource.Error)
        assertEquals(((result[1] as Resource.Error).error as HttpException).code(),404)
    }

    @Test
    fun `Data Returns SSLException`() = runTest{
        val errorMessage = "Certificate Error"
        coEvery { retroRepo.getTopRatedMovies(1) } throws SSLPeerUnverifiedException(errorMessage)
        val result = fetchTopRatedMoviesUseCase(1).toList()
        assert(result[0] is Resource.Loading)
        assert(result[1] is Resource.Error)
        assertEquals((result[1] as Resource.Error).error.message,errorMessage)
    }

    @Test
    fun `Data Returns IOException`() = runTest{
        val errorMessage = "No Internet Error"
        coEvery { retroRepo.getTopRatedMovies(1) } throws IOException(errorMessage)
        val result = fetchTopRatedMoviesUseCase(1).toList()
        assert(result[0] is Resource.Loading)
        assert(result[1] is Resource.Error)
        assertEquals((result[1] as Resource.Error).error.message,errorMessage)
    }
}