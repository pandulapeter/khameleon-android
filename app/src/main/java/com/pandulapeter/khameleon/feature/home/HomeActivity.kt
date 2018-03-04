package com.pandulapeter.khameleon.feature.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.pandulapeter.khameleon.HomeActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.repository.MessageRepository
import com.pandulapeter.khameleon.data.repository.PreferenceRepository
import com.pandulapeter.khameleon.feature.home.calendar.CalendarFragment
import com.pandulapeter.khameleon.feature.home.chat.ChatFragment
import com.pandulapeter.khameleon.feature.home.settings.SettingsFragment
import com.pandulapeter.khameleon.feature.home.songs.SongsFragment
import com.pandulapeter.khameleon.feature.shared.KhameleonActivity
import com.pandulapeter.khameleon.util.consume
import org.koin.android.ext.android.inject

class HomeActivity : KhameleonActivity<HomeActivityBinding>(R.layout.activity_home) {
    private val messageRepository by inject<MessageRepository>()
    private val preferenceRepository by inject<PreferenceRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            messageRepository.setPushNotificationsEnabled(preferenceRepository.chatNotifications)
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
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}