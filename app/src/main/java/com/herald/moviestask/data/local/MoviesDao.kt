package com.herald.moviestask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.herald.moviestask.data.local.entities.MoviesEntity


@Dao
interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovie(user: MoviesEntity)

    @Query("DELETE FROM Movies")
    suspend fun deleteAllMovies()

    @Query("Select * from Movies order by `index` asc")
    suspend fun getAllMovies(): List<MoviesEntity>
}