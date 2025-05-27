package com.herald.moviestask.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.herald.moviestask.common.Resource
import com.herald.moviestask.common.Utils.getErrorMessage
import com.herald.moviestask.domain.models.AllUsesCases
import com.herald.moviestask.domain.models.MoviesModel
import com.herald.moviestask.presentation.movies.states.MovieDetailsState
import com.herald.moviestask.presentation.movies.states.MovieTopRatedState
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
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class MoviesViewModel @Inject constructor(
    private val useCases: AllUsesCases
) : ViewModel() {

    private val _movieDetailsState = MutableStateFlow(MovieDetailsState())
    val movieDetailsState = _movieDetailsState.asStateFlow()

    private val _topRatedMoviesState = MutableStateFlow(MovieTopRatedState())
    val topRatedMoviesState = _topRatedMoviesState.asStateFlow()

    private val _events = MutableSharedFlow<MoviesEvents>()
    val events = _events.asSharedFlow()

    val movies = useCases.fetchPagedMoviesUseCase().cachedIn(viewModelScope)

    private val _searchQueryState = MutableStateFlow("")

    val searchResult: Flow<PagingData<MoviesModel.MovieItem>> = _searchQueryState
        .debounce(1000)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            useCases.fetchMovieSearchUseCase(query)
        }
        .cachedIn(viewModelScope)


    fun handleIntents(intent: MoviesIntents) {
        when (intent) {
            is MoviesIntents.OpenMovieDetails       -> triggerEvent(MoviesEvents.NavigateToMovieDetails(intent.id))
            is MoviesIntents.RetryLoadingData       -> triggerEvent(MoviesEvents.Retry)
            is MoviesIntents.OpenMovieSearch        -> triggerEvent(MoviesEvents.NavigateToMovieSearch)
            is MoviesIntents.NavigateBack           -> triggerEvent(MoviesEvents.NavigateBack)
            is MoviesIntents.OnErrorOccurred        -> triggerEvent(MoviesEvents.ErrorOccurred((intent.exception as Exception).getErrorMessage()))
            is MoviesIntents.OnSearchQueryChanged   -> onSearchQueryChanged(intent.query)
            is MoviesIntents.LoadMovieDetails       -> loadMovieDetails(intent.id)
            is MoviesIntents.LoadTopRatedMovies     -> loadCachedTopRatedMovies()
        }
    }


    private fun loadMovieDetails(id: Int) = viewModelScope.launch {
        useCases.fetchMovieDetailsUseCase(id).collect { resource ->
            when (resource) {
                is Resource.Loading -> _movieDetailsState.update { MovieDetailsState(isLoading = true) }
                is Resource.Success -> _movieDetailsState.update { MovieDetailsState(movie = resource.data) }
                is Resource.Error -> {
                    resource.error.getErrorMessage().apply {
                        _movieDetailsState.update { MovieDetailsState(error = this) }
                        triggerEvent(MoviesEvents.ErrorOccurred(this))
                    }
                }
            }
        }
    }

    private fun loadCachedTopRatedMovies(page: Int = 1) = viewModelScope.launch {
        useCases.fetchCachedMoviesUseCase().collect { resource ->
            when (resource) {
                is Resource.Loading -> _topRatedMoviesState.update { it.copy(isLoading = true, error = null)
                }
                is Resource.Success -> {
                    if (resource.data.movieListItems.isNotEmpty()) {
                        _topRatedMoviesState.update { MovieTopRatedState(movies = resource.data) }
                    }
                    loadRemoteTopRatedMovies(page)
                }
                is Resource.Error -> {
                    resource.error.getErrorMessage().apply {
                        _topRatedMoviesState.update { it.copy(isLoading = false, error = this) }
                        triggerEvent(MoviesEvents.ErrorOccurred(this))
                    }
                }
            }
        }
    }
    private fun loadRemoteTopRatedMovies(page: Int = 1) = viewModelScope.launch {
        useCases.fetchTopRatedMoviesUseCase(page).collect { resource ->
            when (resource) {
                is Resource.Loading -> {
                    val isCachedMoviesEmpty = _topRatedMoviesState.value.movies?.movieListItems.isNullOrEmpty()
                    _topRatedMoviesState.update { it.copy(isLoading = isCachedMoviesEmpty ,error = null) }
                }
                is Resource.Success -> {
                    _topRatedMoviesState.update { MovieTopRatedState(movies = resource.data) }
                    useCases.deleteCacheUseCase()
                    useCases.cacheMoviesUseCase(resource.data.movieListItems)
                }
                is Resource.Error -> {
                    resource.error.getErrorMessage().apply {
                        _topRatedMoviesState.update { it.copy(isLoading = false, error = this) }
                        triggerEvent(MoviesEvents.ErrorOccurred(this))
                    }
                }
            }
        }
    }
    private fun triggerEvent(event: MoviesEvents) = viewModelScope.launch {
        _events.emit(event)
    }

    private fun onSearchQueryChanged(query: String) = viewModelScope.launch {
        _searchQueryState.update { query }
    }
}