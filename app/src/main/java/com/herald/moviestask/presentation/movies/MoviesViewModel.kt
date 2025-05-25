package com.herald.moviestask.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.herald.moviestask.common.Resource
import com.herald.moviestask.common.Utils.getErrorMessage
import com.herald.moviestask.domain.remote.usecases.FetchMovieDetailsUseCase
import com.herald.moviestask.domain.remote.usecases.FetchPagedMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    fetchPagedMoviesUseCase: FetchPagedMoviesUseCase,
    private val fetchMovieDetailsUseCase: FetchMovieDetailsUseCase
) : ViewModel() {
    private val _singleMovieStates = MutableStateFlow(SingleMovieStates())
    val singleMovieStates = _singleMovieStates.asStateFlow()

    private val _events = MutableSharedFlow<MoviesEvents>()
    val events = _events.asSharedFlow()

    val movies = fetchPagedMoviesUseCase(onError = { errorHandling(it) }).cachedIn(viewModelScope)

    fun handleIntents(intent: MoviesIntents) {
        when (intent) {
            is MoviesIntents.OpenMovieDetails -> showMovieDetails(intent.id)
            is MoviesIntents.RetryLoadingData -> retryLoadingData()
            is MoviesIntents.LoadMovieDetails -> loadMovieDetails(intent.id)
        }
    }

    private fun loadMovieDetails(id: Int) = viewModelScope.launch {
        fetchMovieDetailsUseCase(id).collect { resource ->
            when (resource) {
                is Resource.Loading -> _singleMovieStates.update { it.copy(isLoading = true) }
                is Resource.Success -> _singleMovieStates.update { SingleMovieStates(movie = resource.data) }
                is Resource.Error -> {
                    _singleMovieStates.update { it.copy(isLoading = false, error = getErrorMessage(resource.error)) }
                    _events.emit(MoviesEvents.ErrorOccurred(getErrorMessage(resource.error)))
                }
            }
        }
    }

    private fun errorHandling(e: Exception) = viewModelScope.launch {
        _events.emit(MoviesEvents.ErrorOccurred(getErrorMessage(e)))
    }

    private fun retryLoadingData() = viewModelScope.launch {
        _events.emit(MoviesEvents.Retry)
    }

    private fun showMovieDetails(id: Int) = viewModelScope.launch {
        _events.emit(MoviesEvents.NavigateToMovieDetails(id))
    }
}