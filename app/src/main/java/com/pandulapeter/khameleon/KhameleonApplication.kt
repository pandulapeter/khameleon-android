package com.pandulapeter.khameleon

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.pandulapeter.khameleon.injection.chatModule
import com.pandulapeter.khameleon.injection.userModule
import org.koin.android.ext.android.startKoin

class KhameleonApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseMessaging.getInstance().subscribeToTopic("chat")
        startKoin(this, listOf(userModule, chatModule))
    }
}