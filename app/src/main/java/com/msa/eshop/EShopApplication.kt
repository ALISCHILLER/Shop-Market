package com.msa.eshop

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class  EShopApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        // if (BuildConfig.DEBUG) {
        Timber.plant(Timber.DebugTree())
        // }
    }
}