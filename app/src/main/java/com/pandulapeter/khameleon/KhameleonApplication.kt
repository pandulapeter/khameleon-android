package com.pandulapeter.khameleon

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.database.FirebaseDatabase
import com.pandulapeter.khameleon.injection.*
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin


class KhameleonApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(
            this, Crashlytics.Builder().core(
                CrashlyticsCore.Builder()
                    .disabled(BuildConfig.DEBUG)
                    .build()
            ).build()
        )
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        startKoin(this, listOf(integrationModule, userModule, chatModule, calendarModule, songsModule))
    }
}