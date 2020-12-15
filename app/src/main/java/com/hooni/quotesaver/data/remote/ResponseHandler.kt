package com.hooni.quotesaver.data.remote

import com.hooni.quotesaver.util.SOCKET_TIMEOUT_EXCEPTION
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ResponseHandler {
    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e.code()), null)
            is SocketTimeoutException -> Resource.error(getErrorMessage(SOCKET_TIMEOUT_EXCEPTION), null)
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            SOCKET_TIMEOUT_EXCEPTION -> "Socket Timeout"
            401 -> "Unauthorised"
            404 -> "Not found"
            else -> "Something went wrong"
        }
    }
}