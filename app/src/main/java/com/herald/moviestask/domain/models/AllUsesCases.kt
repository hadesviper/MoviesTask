package com.herald.moviestask.domain.models

import com.herald.moviestask.domain.local.usecases.CacheMoviesUseCase
import com.herald.moviestask.domain.local.usecases.DeleteCacheUseCase
import com.herald.moviestask.domain.local.usecases.FetchCachedMoviesUseCase
import com.herald.moviestask.domain.remote.usecases.FetchMovieDetailsUseCase
import com.herald.moviestask.domain.remote.usecases.FetchMovieSearchUseCase
import com.herald.moviestask.domain.remote.usecases.FetchPagedMoviesUseCase
import com.herald.moviestask.domain.remote.usecases.FetchTopRatedMovies

data class AllUsesCases(
    val fetchPagedMoviesUseCase: FetchPagedMoviesUseCase,
    val fetchMovieSearchUseCase: FetchMovieSearchUseCase,
    val fetchMovieDetailsUseCase: FetchMovieDetailsUseCase,
    val fetchTopRatedMovies: FetchTopRatedMovies,
    val deleteCacheUseCase: DeleteCacheUseCase,
    val fetchCachedMoviesUseCase: FetchCachedMoviesUseCase,
    val cacheMoviesUseCase: CacheMoviesUseCase
)
