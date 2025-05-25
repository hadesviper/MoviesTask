package com.herald.moviestask.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.herald.moviestask.common.Resource
import com.herald.moviestask.common.Utils.getErrorMessage
import com.herald.moviestask.domain.remote.models.MoviesModel
import com.herald.moviestask.domain.remote.usecases.FetchMovieDetailsUseCase
import com.herald.moviestask.domain.remote.usecases.FetchMovieSearchUseCase
import com.herald.moviestask.domain.remote.usecases.FetchPagedMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    fetchPagedMoviesUseCase: FetchPagedMoviesUseCase,
    fetchMovieSearchUseCase: FetchMovieSearchUseCase,
    private val fetchMovieDetailsUseCase: FetchMovieDetailsUseCase
) : ViewModel() {

    private val _singleMovieStates = MutableStateFlow(SingleMovieStates())
    val singleMovieStates = _singleMovieStates.asStateFlow()

    private val _events = MutableSharedFlow<MoviesEvents>()
    val events = _events.asSharedFlow()

    val movies = fetchPagedMoviesUseCase(onError = { errorHandling(it) }).cachedIn(viewModelScope)

    private val _searchQueryState = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResult: Flow<PagingData<MoviesModel.MovieItem>> = _searchQueryState
        .debounce(1000)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            fetchMovieSearchUseCase(query, onError = { errorHandling(it) })
        }
        .cachedIn(viewModelScope)


    fun handleIntents(intent: MoviesIntents) {
        when (intent) {
            is MoviesIntents.OpenMovieDetails -> navigateToMovieDetails(intent.id)
            is MoviesIntents.RetryLoadingData -> retryLoadingData()
            is MoviesIntents.LoadMovieDetails -> loadMovieDetails(intent.id)
            is MoviesIntents.OpenMovieSearch -> navigateToMovieSearch()
            is MoviesIntents.NavigateBack -> navigateBack()
            is MoviesIntents.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
        }
    }


    private fun loadMovieDetails(id: Int) = viewModelScope.launch {
        fetchMovieDetailsUseCase(id).collect { resource ->
            when (resource) {
                is Resource.Loading -> _singleMovieStates.update { SingleMovieStates(isLoading = true) }
                is Resource.Success -> _singleMovieStates.update { SingleMovieStates(movie = resource.data) }
                is Resource.Error -> {
                    _singleMovieStates.update { SingleMovieStates(error = getErrorMessage(resource.error)) }
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

    private fun navigateToMovieDetails(id: Int) = viewModelScope.launch {
        _events.emit(MoviesEvents.NavigateToMovieDetails(id))
    }

    private fun navigateToMovieSearch() = viewModelScope.launch {
        _events.emit(MoviesEvents.NavigateToMovieSearch)
    }
    private fun navigateBack() = viewModelScope.launch {
        _events.emit(MoviesEvents.NavigateBack)
    }
    private fun onSearchQueryChanged(query: String) = viewModelScope.launch {
        _searchQueryState.update { query }
    }
}