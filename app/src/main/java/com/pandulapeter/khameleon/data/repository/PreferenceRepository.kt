package com.pandulapeter.khameleon.data.repository

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("preference_storage", Context.MODE_PRIVATE)
    var chatNotifications by PreferenceFieldDelegate.Boolean("chat_notifications", true)
    var eventNotifications by PreferenceFieldDelegate.Boolean("event_notifications", true)

    private sealed class PreferenceFieldDelegate<T>(protected val key: String, protected val defaultValue: T) : ReadWriteProperty<PreferenceRepository, T> {

        class Boolean(key: String, defaultValue: kotlin.Boolean = false) : PreferenceFieldDelegate<kotlin.Boolean>(key, defaultValue) {

            override fun getValue(thisRef: PreferenceRepository, property: KProperty<*>) = thisRef.preferences.getBoolean(key, defaultValue)

            override fun setValue(thisRef: PreferenceRepository, property: KProperty<*>, value: kotlin.Boolean) =
                thisRef.preferences.edit().putBoolean(key, value).apply()
        }
    }
}