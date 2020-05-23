package com.srgpanov.simpleweather.data.remote

import com.squareup.okhttp.ResponseBody
import com.srgpanov.simpleweather.data.models.ip_to_location.IpToLocation
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IpToLocationService {
    @GET("json/?fields=24784")
    suspend fun getLocation(
    ): ResponseResult<IpToLocation>
}