package com.herald.moviestask.di

import android.content.Context
import androidx.room.Room
import com.herald.moviestask.data.local.AppDatabase
import com.herald.moviestask.data.local.AppDatabase.Companion.DATABASE_NAME
import com.herald.moviestask.data.local.CachingRepoImpl
import com.herald.moviestask.data.local.MoviesDao
import com.herald.moviestask.domain.local.repository.CachingRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CashingModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(context,  AppDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    @Singleton
    fun providesDao(appDatabase: AppDatabase): MoviesDao = appDatabase.getDao()

    @Provides
    @Singleton
    fun providesUsersRepo(moviesDao: MoviesDao): CachingRepo = CachingRepoImpl(moviesDao)
}