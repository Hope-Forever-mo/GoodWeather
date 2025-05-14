package com.example.goodweather.android.logic

import androidx.lifecycle.liveData
import com.example.goodweather.android.logic.model.Place
import com.example.goodweather.android.logic.network.GoodWeatherNetwork
import kotlinx.coroutines.Dispatchers
import retrofit2.http.Query

object Repository {
    fun searchPlaces(query: String) = liveData(Dispatchers.IO){
        val result = try {
            val placesResponse = GoodWeatherNetwork.searchPlaces(query)
            if(placesResponse.status == "ok"){
                val places = placesResponse.places
                Result.success(places)
            }else{
                Result.failure(RuntimeException("response status is ${placesResponse.status}"))
            }
        }catch (e: Exception){
            Result.failure<List<Place>>(e)
        }
        emit(result)

    }

}