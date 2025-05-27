package com.herald.moviestask.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import retrofit2.HttpException
import java.io.IOException
import javax.net.ssl.SSLPeerUnverifiedException

object Utils {

    fun Exception?.getErrorMessage(): String {
        return when (this) {
            is HttpException -> "Error Occurred, code: ${this.code()}"
            is SSLPeerUnverifiedException -> "MITM attack detected"
            is IOException -> "No Internet Connection"
            else -> this?.message ?: "Unknown Error"
        }
    }

    suspend fun showRetrySnackbar(snackbarHostState: SnackbarHostState, message: String = "error", actionPerformed: () -> Unit ) {
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = "Retry",
        )
        when (result) {
            SnackbarResult.Dismissed -> Unit
            SnackbarResult.ActionPerformed -> actionPerformed()
        }
    }
}