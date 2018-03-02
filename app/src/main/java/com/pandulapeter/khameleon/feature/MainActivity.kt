package com.pandulapeter.khameleon.feature

import android.app.ActivityManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.pandulapeter.khameleon.BuildConfig
import com.pandulapeter.khameleon.MainActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.calendar.CalendarFragment
import com.pandulapeter.khameleon.feature.chat.ChatFragment
import com.pandulapeter.khameleon.feature.settings.SettingsFragment
import com.pandulapeter.khameleon.feature.songs.SongsFragment
import com.pandulapeter.khameleon.util.color
import com.pandulapeter.khameleon.util.consume

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
        val binding = DataBindingUtil.setContentView<MainActivityBinding>(this, R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.handleReplace { ChatFragment() }
        }
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            consume {
                when (it.itemId) {
                    R.id.chat -> supportFragmentManager.handleReplace { ChatFragment() }
                    R.id.calendar -> supportFragmentManager.handleReplace { CalendarFragment() }
                    R.id.songs -> supportFragmentManager.handleReplace { SongsFragment() }
                    R.id.settings -> supportFragmentManager.handleReplace { SettingsFragment() }
                }
            }
        }
    }

    private inline fun <reified T : Fragment> FragmentManager.handleReplace(crossinline newInstance: () -> T) {
        beginTransaction()
            .replace(
                R.id.fragment_container,
                findFragmentByTag(T::class.java.name) ?: newInstance.invoke(),
                T::class.java.name
            )
            .commit()
    }
}