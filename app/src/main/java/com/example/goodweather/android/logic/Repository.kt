package com.example.goodweather.android.logic

import android.content.Context
import androidx.lifecycle.liveData
import com.example.goodweather.android.logic.dao.PlaceDao
import com.example.goodweather.android.logic.model.Place
import com.example.goodweather.android.logic.model.Weather
import com.example.goodweather.android.logic.network.GoodWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.http.Query
import kotlin.coroutines.CoroutineContext

object Repository {

    fun searchPlaces(query: String) = fire(Dispatchers.IO){
            val placesResponse = GoodWeatherNetwork.searchPlaces(query)
            if(placesResponse.status == "ok"){
                val places = placesResponse.places
                Result.success(places)
            }else{
                Result.failure(RuntimeException("response status is ${placesResponse.status}"))
            }
        }

    fun refreshWeather(lng: String,lat: String) = fire(Dispatchers.IO){
        coroutineScope {
            val deferredRealtime = async {
                GoodWeatherNetwork.getRealtimeWeather(lng,lat)
            }
            val deferredDaily = async {
                GoodWeatherNetwork.getDailyWeather(lng,lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok"){
                val weather = Weather(realtimeResponse.result.realtime,
                    dailyResponse.result.daily)
                Result.success(weather)
            }else{
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }


    private fun <T> fire(context: CoroutineContext,block: suspend() -> Result<T>) =
        liveData<Result<T>>(context){
            val result = try {
                block()
            }catch (e: Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

}


