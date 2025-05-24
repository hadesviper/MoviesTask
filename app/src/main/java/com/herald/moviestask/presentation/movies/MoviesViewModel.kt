package com.herald.moviestask.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.herald.moviestask.domain.remote.models.MoviesModel
import com.herald.moviestask.domain.remote.usecases.FetchPagedMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.net.ssl.SSLPeerUnverifiedException

@HiltViewModel
class MoviesViewModel @Inject constructor(
    fetchPagedMoviesUseCase: FetchPagedMoviesUseCase
) : ViewModel() {
    private val _events = MutableSharedFlow<MoviesEvents>()
    val events = _events.asSharedFlow()

    val movies = fetchPagedMoviesUseCase(onError = { errorHandling(it) }).cachedIn(viewModelScope)

    fun handleIntents(intent: MoviesIntents) {
        when (intent) {
            is MoviesIntents.OpenMovieDetails -> showMovieDetails(intent.movie)
            is MoviesIntents.PagerRetry -> retryLoadingData()
        }
    }

    private fun errorHandling(e: Exception) = viewModelScope.launch{
        val message = when(e){
            is HttpException -> "Error Occurred, code: ${e.code()}"
            is SSLPeerUnverifiedException -> "MITM attack detected"
            is IOException -> "No Internet Connection"
            else -> e.message ?: "Unknown Error"
        }
        _events.emit(MoviesEvents.ErrorOccurred(message))
    }
    
    private fun retryLoadingData() = viewModelScope.launch {
        _events.emit(MoviesEvents.Retry)
    }

    private fun showMovieDetails(movie: MoviesModel.MovieData) = viewModelScope.launch {
        _events.emit(MoviesEvents.NavigateToMovieDetails(movie))
    }
}