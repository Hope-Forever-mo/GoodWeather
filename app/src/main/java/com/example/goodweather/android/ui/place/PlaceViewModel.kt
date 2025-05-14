package com.example.goodweather.android.ui.place

import android.view.animation.Transformation
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.goodweather.android.logic.Repository
import com.example.goodweather.android.logic.model.Place
import retrofit2.http.Query

class PlaceViewModel : ViewModel() {
    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    //实际上调用的还是 Transformations.switchMap(...)，
    // 只是被封装成了 LiveData.switchMap { ... } 的形式
    val placeLiveData = searchLiveData.switchMap { query ->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query: String){
        searchLiveData.value = query
    }
}


