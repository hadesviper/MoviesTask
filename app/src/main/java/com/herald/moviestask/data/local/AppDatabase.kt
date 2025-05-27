package com.herald.moviestask.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.herald.moviestask.data.local.entities.MoviesEntity

@Database(entities = [MoviesEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getDao(): MoviesDao
    companion object{
        const val DATABASE_NAME = "Movies"
    }
}