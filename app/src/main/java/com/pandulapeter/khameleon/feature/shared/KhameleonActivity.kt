package com.pandulapeter.khameleon.feature.shared

import android.app.ActivityManager
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import com.pandulapeter.khameleon.BuildConfig
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.util.color

abstract class KhameleonActivity<B : ViewDataBinding>(@LayoutRes private val layoutResourceId: Int) : AppCompatActivity() {

    protected lateinit var binding: B

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
        binding = DataBindingUtil.setContentView(this, layoutResourceId)
    }
}