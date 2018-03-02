package com.pandulapeter.khameleon.feature

import android.app.ActivityManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.pandulapeter.khameleon.BuildConfig
import com.pandulapeter.khameleon.MainActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.util.color

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        @Suppress("ConstantConditionIf")
        setTaskDescription(
            ActivityManager.TaskDescription(
                getString(R.string.khameleon) + if (BuildConfig.BUILD_TYPE == "release") "" else " (" + BuildConfig.BUILD_TYPE + ")",
                null, color(R.color.primary)
            )
        )
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<MainActivityBinding>(this, R.layout.activity_main)
    }
}