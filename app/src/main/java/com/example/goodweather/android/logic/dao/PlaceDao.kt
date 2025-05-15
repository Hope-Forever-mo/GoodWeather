package com.example.goodweather.android.logic.dao

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Global.putString
import com.example.goodweather.android.GoodWeatherApplication
import com.example.goodweather.android.logic.model.Place
import com.google.gson.Gson

object PlaceDao {

    fun savePlace(place: Place) {
        val editor = sharedPreferences().edit()
        editor.putString("place", Gson().toJson(place))
        editor.apply() // 使用 apply 而不是 commit 以避免阻塞主线程
    }

    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = GoodWeatherApplication.context.
    getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

}