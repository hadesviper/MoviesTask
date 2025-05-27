package com.herald.moviestask.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import retrofit2.HttpException
import java.io.IOException
import javax.net.ssl.SSLPeerUnverifiedException

object Utils {

    fun getErrorMessage(e: Exception?): String {
        return when (e) {
            is HttpException -> "Error Occurred, code: ${e.code()}"
            is SSLPeerUnverifiedException -> "MITM attack detected"
            is IOException -> "No Internet Connection"
            else -> e?.message ?: "Unknown Error"
        }
    }

    suspend fun showSnackBar(snackBarHostState: SnackbarHostState, message: String = "error", actionPerformed: () -> Unit ) {
        val result = snackBarHostState.showSnackbar(
            message = message,
            actionLabel = "Retry",
        )
        when (result) {
            SnackbarResult.Dismissed -> Unit
            SnackbarResult.ActionPerformed -> actionPerformed()
        }
    }
}