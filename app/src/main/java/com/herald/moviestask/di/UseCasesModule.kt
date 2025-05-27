package com.herald.moviestask.di

import com.herald.moviestask.domain.local.repository.CachingRepo
import com.herald.moviestask.domain.local.usecases.CacheMoviesUseCase
import com.herald.moviestask.domain.local.usecases.DeleteCacheUseCase
import com.herald.moviestask.domain.local.usecases.FetchCachedMoviesUseCase
import com.herald.moviestask.domain.models.AllUsesCases
import com.herald.moviestask.domain.remote.repository.RetroRepository
import com.herald.moviestask.domain.remote.usecases.FetchMovieDetailsUseCase
import com.herald.moviestask.domain.remote.usecases.FetchMovieSearchUseCase
import com.herald.moviestask.domain.remote.usecases.FetchPagedMoviesUseCase
import com.herald.moviestask.domain.remote.usecases.FetchTopRatedMoviesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {

    @Provides
    @Singleton
    fun providesAllUseCases(retroRepository: RetroRepository, cachingRepo: CachingRepo): AllUsesCases{
        return AllUsesCases(
            fetchPagedMoviesUseCase = FetchPagedMoviesUseCase(retroRepository),
            fetchMovieSearchUseCase = FetchMovieSearchUseCase(retroRepository),
            fetchMovieDetailsUseCase = FetchMovieDetailsUseCase(retroRepository),
            fetchTopRatedMoviesUseCase = FetchTopRatedMoviesUseCase(retroRepository),
            deleteCacheUseCase = DeleteCacheUseCase(cachingRepo),
            fetchCachedMoviesUseCase = FetchCachedMoviesUseCase(cachingRepo),
            cacheMoviesUseCase = CacheMoviesUseCase(cachingRepo)
        )
    }
}