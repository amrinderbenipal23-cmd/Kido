package com.quickfix.kidszone

import android.app.Application
import com.quickfix.kidszone.utils.AdManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KiddoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AdManager.initialize(this)
    }
}
