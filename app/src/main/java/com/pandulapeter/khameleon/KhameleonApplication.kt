package com.pandulapeter.khameleon

import android.app.Application
import org.koin.android.ext.android.startKoin

class KhameleonApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf())
    }
}