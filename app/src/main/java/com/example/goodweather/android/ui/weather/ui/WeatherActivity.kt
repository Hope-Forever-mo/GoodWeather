package com.example.goodweather.android.ui.weather.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.goodweather.android.R
import com.example.goodweather.android.bindView
import com.example.goodweather.android.logic.model.Weather
import com.example.goodweather.android.logic.model.getSky
import com.example.goodweather.android.ui.weather.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    lateinit var swipeRefresh : SwipeRefreshLayout
    lateinit var navBtn : Button
    lateinit var drawerLayout : DrawerLayout

    val viewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN
        window.statusBarColor = Color.Transparent.toArgb()
        setContentView(R.layout.activity_weather)
        val locationLng = intent.getStringExtra("location_lng")
        val locationLat = intent.getStringExtra("location_lat")
        val placeName = intent.getStringExtra("location_name")

        swipeRefresh = bindView<SwipeRefreshLayout>(R.id.swipeRefresh)

        Log.d("WeatherActivity", "Received Lng: $locationLng, Lat: $locationLat, Place Name: $placeName")
//        if(viewModel.locationLng.isEmpty()){
//            viewModel.locationLng = intent.getStringExtra("location_lng")?:""
//        }
//        if (viewModel.locationLat.isEmpty()){
//            viewModel.locationLat = intent.getStringExtra("location_lat")?:""
//        }
//        if (viewModel.placeName.isEmpty()){
//            viewModel.placeName = intent.getStringExtra("location_name")?:""
//        }
        if (viewModel.locationLng.isEmpty()){
            viewModel.locationLng = locationLng ?: ""
        }
        if (viewModel.locationLat.isEmpty()){
            viewModel.locationLat = locationLat ?: ""
        }
        if (viewModel.placeName.isEmpty()){
            viewModel.placeName = placeName ?: ""
        }

        viewModel.weatherLiveData.observe(this, Observer{ result ->
            val weather = result.getOrNull()
            if (weather != null){
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this,"无法获取天气信息",Toast.LENGTH_SHORT).show()
                Log.e("WeatherActivity", "获取天气信息失败", result.exceptionOrNull())
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        navBtn = findViewById<Button>(R.id.navBtn)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
    }

    private fun showWeatherInfo(weather: Weather){
        val placeName = bindView<TextView>(R.id.placeName)
        val currentTemp = bindView<TextView>(R.id.currentTemp)
        val currentSky = bindView<TextView>(R.id.currentSky)
        val currentAQI = bindView<TextView>(R.id.currentAQI)
        val nowLayout = bindView<RelativeLayout>(R.id.nowLayout)

        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now.xml中布局的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //填充forecast.xml布局中的数据
        val forecastLayout = bindView<LinearLayout>(R.id.forecastLayout)
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days){
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)
            val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }

        //填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        val coldRiskText = bindView<TextView>(R.id.coldRiskText)
        val dressingText = bindView<TextView>(R.id.dressingText)
        val ultravioletText = bindView<TextView>(R.id.ultravioletText)
        val carWashingText = bindView<TextView>(R.id.carWashingText)
        val weatherLayout = bindView<ScrollView>(R.id.weatherLayout)

        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE



    }
    fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

}
