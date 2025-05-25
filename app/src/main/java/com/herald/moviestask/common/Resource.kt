package com.herald.moviestask.common

sealed class Resource<T>(val data: T? = null, val error: Exception? = null) {
    class Loading<T> : Resource<T>()
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(error: Exception?) : Resource<T>(error = error)
}