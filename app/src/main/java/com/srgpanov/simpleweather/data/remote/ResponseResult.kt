package com.srgpanov.simpleweather.data.remote

import okhttp3.ResponseBody

sealed class ResponseResult<out T : Any> {
    data class Success<T : Any>(val data: T) : ResponseResult<T>()
    sealed class Failure : ResponseResult<Nothing>() {
        data class ServerError(
            val errorCode: Int = 0,
            val errorBody: ResponseBody? = null
        ) : Failure()

        data class NetworkError(val ex: Exception) : Failure()
    }


}