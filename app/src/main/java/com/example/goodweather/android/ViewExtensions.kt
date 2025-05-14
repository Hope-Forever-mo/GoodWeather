package com.example.goodweather.android

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.Fragment


// 扩展函数用于Activity
fun <T : View> FragmentActivity.bindView(id: Int): T {
    return this.findViewById(id)
}

// 扩展函数用于View
fun <T : View> View.bindView(id: Int): T {
    return this.findViewById(id)
}

inline fun <reified T : View> Fragment.bindView(id: Int): T {
    return requireView().findViewById(id)
}