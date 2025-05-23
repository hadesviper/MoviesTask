package com.herald.moviestask.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herald.moviestask.common.Resource
import com.herald.moviestask.domain.remote.models.MoviesModel
import com.herald.moviestask.domain.remote.usecases.FetchMoviesUseCase
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
    private val fetchMoviesUseCase: FetchMoviesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MoviesState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<MoviesEvents>()
    val events = _events.asSharedFlow()

    fun handleIntents(intent: MoviesIntents) {
        when (intent) {
            is MoviesIntents.FetchFirstMovies -> fetchFirstMovies(intent.page)
            is MoviesIntents.OpenMovieDetails -> showMovieDetails(intent.movie)
        }
    }

    init {
        fetchFirstMovies(1)
    }

    private fun fetchFirstMovies(page: Int) = viewModelScope.launch {
        fetchMoviesUseCase(page).collect { resource ->
            when (resource) {
                is Resource.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                is Resource.Success -> _state.update { MoviesState(movies = resource.data?.results) }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = resource.error?.message) }
                    _events.emit(MoviesEvents.ErrorOccurred(resource.error?.message ?: "Error"))
                }
            }
        }
    }

    private fun showMovieDetails(movie: MoviesModel.MovieData) = viewModelScope.launch {
        _events.emit(MoviesEvents.NavigateToMovieDetails(movie))
    }
}