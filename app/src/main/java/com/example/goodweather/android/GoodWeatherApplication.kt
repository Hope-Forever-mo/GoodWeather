package com.example.goodweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class GoodWeatherApplication : Application() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        const val TOKEN = "ET7kwefSiGiegSdt"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}