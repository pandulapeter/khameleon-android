package com.pandulapeter.khameleon.util

import android.os.Bundle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

sealed class BundleArgumentDelegate<T>(protected val key: kotlin.String) : ReadWriteProperty<Bundle?, T> {

    class Boolean(key: kotlin.String) : BundleArgumentDelegate<kotlin.Boolean>(key) {

        override fun getValue(thisRef: Bundle?, property: KProperty<*>) = thisRef?.getBoolean(key, false) ?: false

        override fun setValue(thisRef: Bundle?, property: KProperty<*>, value: kotlin.Boolean) = thisRef?.putBoolean(key, value) ?: Unit
    }

    class Int(key: kotlin.String) : BundleArgumentDelegate<kotlin.Int>(key) {

        override fun getValue(thisRef: Bundle?, property: KProperty<*>) = thisRef?.getInt(key) ?: 0

        override fun setValue(thisRef: Bundle?, property: KProperty<*>, value: kotlin.Int) = thisRef?.putInt(key, value) ?: Unit
    }

    class String(key: kotlin.String) : BundleArgumentDelegate<kotlin.String>(key) {

        override fun getValue(thisRef: Bundle?, property: KProperty<*>) = thisRef?.getString(key, "") ?: ""

        override fun setValue(thisRef: Bundle?, property: KProperty<*>, value: kotlin.String) = thisRef?.putString(key, value) ?: Unit
    }

    class Parcelable<out U : android.os.Parcelable>(key: kotlin.String) : BundleArgumentDelegate<android.os.Parcelable>(key) {
        override fun getValue(thisRef: Bundle?, property: KProperty<*>) = thisRef?.getParcelable(key) as U

        override fun setValue(thisRef: Bundle?, property: KProperty<*>, value: android.os.Parcelable) = thisRef?.putParcelable(key, value) ?: Unit
    }
}