package com.herald.moviestask.data.remote

import com.herald.moviestask.common.Constants
import com.herald.moviestask.common.Constants.CACHE_DURATION
import com.herald.moviestask.data.remote.dto.MovieDTO
import com.herald.moviestask.data.remote.dto.MoviesDTO
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RetroService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page:Int,
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") authorization: String = Constants.MY_KEY,
        @Header("Cache-Control") cacheControl: String = "public, max-age=$CACHE_DURATION"
    ) : MoviesDTO

    @GET("movie/{movieID}?append_to_response=videos")
    suspend fun getMovieDetails(
        @Path("movieID") movieID:Int,
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") authorization: String = Constants.MY_KEY,
        @Header("Cache-Control") cacheControl: String = "public, max-age=$CACHE_DURATION"
    ): MovieDTO
}