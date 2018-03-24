package com.pandulapeter.khameleon.feature.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.pandulapeter.khameleon.HomeActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.repository.ChatRepository
import com.pandulapeter.khameleon.data.repository.PreferenceRepository
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.feature.authentication.AuthenticationActivity
import com.pandulapeter.khameleon.feature.home.calendar.CalendarFragment
import com.pandulapeter.khameleon.feature.home.chat.ChatFragment
import com.pandulapeter.khameleon.feature.home.settings.SettingsFragment
import com.pandulapeter.khameleon.feature.home.songs.SongsFragment
import com.pandulapeter.khameleon.feature.shared.KhameleonActivity
import com.pandulapeter.khameleon.integration.AppShortcutManager
import com.pandulapeter.khameleon.util.IntentExtraDelegate
import com.pandulapeter.khameleon.util.consume
import org.koin.android.ext.android.inject

class HomeActivity : KhameleonActivity<HomeActivityBinding>(R.layout.activity_home) {

    companion object {
        private var Intent.item by IntentExtraDelegate.String("item")
        private var Intent.text by IntentExtraDelegate.String(Intent.EXTRA_TEXT)

        fun getStartIntent(context: Context, item: String) = Intent(context, HomeActivity::class.java).apply {
            this.item = item
        }
    }

    private val userRepository by inject<UserRepository>()
    private val messageRepository by inject<ChatRepository>()
    private val preferenceRepository by inject<PreferenceRepository>()
    var calendarTimestamp: Long? = null
    var defaultMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.HomeTheme)
        super.onCreate(savedInstanceState)
        if (userRepository.getSignedInUser() == null) {
            startActivity(Intent(this, AuthenticationActivity::class.java))
            finish()
        }
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            consume { updateDisplayedFragment(it.itemId) }
        }
        if (!handleShareIntent()) {
            if (savedInstanceState == null) {
                messageRepository.setPushNotificationsEnabled(preferenceRepository.chatNotifications)
                when (intent.item) {
                    AppShortcutManager.CHAT_ID -> openChatScreen()
                    AppShortcutManager.CALENDAR_ID -> openCalendarScreen()
                    AppShortcutManager.SONGS_ID -> openSongsScreen()
                    AppShortcutManager.SETTINGS_ID -> openSettingsScreen()
                }
                updateDisplayedFragment(binding.bottomNavigation.selectedItemId)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        when (intent?.item) {
            AppShortcutManager.CHAT_ID -> openChatScreen()
            AppShortcutManager.CALENDAR_ID -> openCalendarScreen()
            AppShortcutManager.SONGS_ID -> openSongsScreen()
            AppShortcutManager.SETTINGS_ID -> openSettingsScreen()
            else -> handleShareIntent()
        }
    }

    override fun onBackPressed() {
        if ((supportFragmentManager.findFragmentById(R.id.fragment_container) as? KhameleonFragment<*, *>)?.onBackPressed() != true) {
            supportFinishAfterTransition()
        }
    }

    private fun updateDisplayedFragment(@IdRes id: Int) {
        when (id) {
            R.id.chat -> supportFragmentManager.handleReplace { ChatFragment() }
            R.id.calendar -> supportFragmentManager.handleReplace { CalendarFragment() }
            R.id.songs -> supportFragmentManager.handleReplace { SongsFragment() }
            R.id.settings -> supportFragmentManager.handleReplace { SettingsFragment() }
        }
    }

    private fun openChatScreen(defaultMessage: String? = null) {
        this.defaultMessage = defaultMessage
        binding.bottomNavigation.selectedItemId = R.id.chat
    }

    fun openCalendarScreen(timestamp: Long? = null) {
        this.calendarTimestamp = timestamp
        binding.bottomNavigation.selectedItemId = R.id.calendar
    }

    fun openSongsScreen() {
        binding.bottomNavigation.selectedItemId = R.id.songs
    }

    private fun openSettingsScreen() {
        binding.bottomNavigation.selectedItemId = R.id.settings
    }

    private fun handleShareIntent(): Boolean {
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.text.let {
                if (it.isNotEmpty()) {
                    openChatScreen(it)
                    intent.action = null
                    intent.type = null
                    intent.text = ""
                    return true
                }
            }
        }
        return false
    }

    private inline fun <reified T : Fragment> FragmentManager.handleReplace(crossinline newInstance: () -> T) {
        beginTransaction()
            .replace(
                R.id.fragment_container,
                findFragmentByTag(T::class.java.name) ?: newInstance.invoke(),
                T::class.java.name
            )
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}