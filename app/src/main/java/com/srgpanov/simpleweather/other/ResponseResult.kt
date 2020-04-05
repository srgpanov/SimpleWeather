package com.srgpanov.simpleweather.other

sealed class ResponseResult<T> {
    data class Success<T : Any>(val data: T?) : ResponseResult<T>()
    data class Error(val exception: Exception, val errorCode: Int = 0) :
        ResponseResult<Nothing>()

}