package com.srgpanov.simpleweather.data.remote

import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

class ResponseResultCall<T : Any>(
    private val delegate: Call<T>,
    private val errorConverter: Converter<ResponseBody, Nothing>

) : Call<ResponseResult<T>> {
    override fun enqueue(callback: Callback<ResponseResult<T>>) {
        return delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                val code = response.code()
                val errorBody = response.errorBody()
                if (response.isSuccessful) {
                    if (body != null) {
                        callback.onResponse(
                            this@ResponseResultCall,
                            Response.success(ResponseResult.Success(body))
                        )
                    } else {
                        callback.onResponse(
                            this@ResponseResultCall,
                            Response.success(ResponseResult.Failure.ServerError(code, errorBody))
                        )
                    }
                } else {
                    callback.onResponse(
                        this@ResponseResultCall,
                        Response.success(ResponseResult.Failure.ServerError(code, errorBody))
                    )

                }
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                callback.onResponse(
                    this@ResponseResultCall,
                    Response.success(ResponseResult.Failure.NetworkError(IOException(throwable.message)))
                )
            }

        })
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun clone(): Call<ResponseResult<T>> {
        return ResponseResultCall(delegate.clone(),errorConverter)
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun execute(): Response<ResponseResult<T>> {
        throw UnsupportedOperationException("NetworkResponseCall doesn't support execute")
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}