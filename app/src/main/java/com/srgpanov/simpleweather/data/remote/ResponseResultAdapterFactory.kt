package com.srgpanov.simpleweather.data.remote

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResponseResultAdapterFactory: CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Call::class.java != getRawType(returnType)) {
            return null
        }
        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<ResponseResult<<Foo>> or Call<ResponseResult<out Foo>>"
        }
        val responseType = getParameterUpperBound(0, returnType)
        // if the response type is not ApiResponse then we can't handle this type, so we return null
        if (getRawType(responseType) != ResponseResult::class.java) {
            return null
        }
        // the response type is ApiResponse and should be parameterized
        check(responseType is ParameterizedType) { "Response must be parameterized as ResponseResult<Foo> or ResponseResult<out Foo>" }

        val successBodyType = getParameterUpperBound(0, responseType)
        val nothing:Type=Nothing::class.java
        val errorBodyConverter =
            retrofit.nextResponseBodyConverter<Nothing>(null, nothing, annotations)

        return ResponseResultAdapter<Any>(successBodyType, errorBodyConverter)
    }
}