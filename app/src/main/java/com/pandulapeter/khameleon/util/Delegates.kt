package com.pandulapeter.khameleon.util

import android.os.Bundle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


sealed class BundleArgumentDelegate<T>(protected val key: kotlin.String, protected val defaultValue: T) : ReadWriteProperty<Bundle?, T> {

    class Boolean(key: kotlin.String, defaultValue: kotlin.Boolean = false) : BundleArgumentDelegate<kotlin.Boolean>(key, defaultValue) {

        override fun getValue(thisRef: Bundle?, property: KProperty<*>) = thisRef?.getBoolean(key, defaultValue) ?: defaultValue

        override fun setValue(thisRef: Bundle?, property: KProperty<*>, value: kotlin.Boolean) = thisRef?.putBoolean(key, value) ?: Unit
    }

    class Int(key: kotlin.String, defaultValue: kotlin.Int = 0) : BundleArgumentDelegate<kotlin.Int>(key, defaultValue) {

        override fun getValue(thisRef: Bundle?, property: KProperty<*>) = thisRef?.getInt(key, defaultValue) ?: defaultValue

        override fun setValue(thisRef: Bundle?, property: KProperty<*>, value: kotlin.Int) = thisRef?.putInt(key, value) ?: Unit
    }

    class String(key: kotlin.String, defaultValue: kotlin.String = "") : BundleArgumentDelegate<kotlin.String>(key, defaultValue) {

        override fun getValue(thisRef: Bundle?, property: KProperty<*>) = thisRef?.getString(key, defaultValue) ?: defaultValue

        override fun setValue(thisRef: Bundle?, property: KProperty<*>, value: kotlin.String) = thisRef?.putString(key, value) ?: Unit
    }
}