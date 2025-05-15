package com.example.goodweather.android.logic.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object GoodWeatherNetwork {
    private val placeService = ServiceCreator.create<PlaceService>()

    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    suspend fun getDailyWeather(lng: String,lat: String) =
        weatherService.getDailyWeather(lng,lat).await()

    suspend fun getRealtimeWeather(lng: String,lat: String) =
        weatherService.getRealtimeWeather(lng,lat).await()

    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    private suspend fun <T> Call<T>.await(): T{
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T>{
                override fun onResponse(call: Call<T?>, response: Response<T?>) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("Network", "响应体：${body}")
                        continuation.resume(body)
                    } else {
                        Log.e("Network", "空响应体，状态码：${response.code()}")
                        continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }

                override fun onFailure(call: Call<T?>, t: Throwable) {
                    Log.e("Network", "API请求失败", t)
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}