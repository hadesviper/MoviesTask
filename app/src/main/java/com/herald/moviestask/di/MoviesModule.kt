package com.herald.moviestask.di

import android.content.Context
import com.herald.moviestask.common.Constants
import com.herald.moviestask.common.Constants.CACHE_SIZE
import com.herald.moviestask.data.remote.RetroRepoImpl
import com.herald.moviestask.data.remote.RetroService
import com.herald.moviestask.domain.remote.repository.RetroRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MoviesModule {

    @Provides
    @Singleton
    fun getHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val certificatePinner = CertificatePinner
            .Builder()
            .add(Constants.BASE_HOST, "sha256/k1Hdw5sdSn5kh/gemLVSQD/P4i4IBQEY1tW4WNxh9XM=")
            .build()

        return OkHttpClient.Builder()
            .cache(Cache(File(context.cacheDir, "http_cache"), CACHE_SIZE))
            .certificatePinner(certificatePinner)
            .build()
    }

    @Provides
    @Singleton
    fun getRetrofitInstance(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun getRetroService(retrofit: Retrofit): RetroService {
        return retrofit.create(RetroService::class.java)
    }

    @Provides
    @Singleton
    fun getRetroImpl(retroService: RetroService): RetroRepository {
        return RetroRepoImpl(retroService)
    }
}