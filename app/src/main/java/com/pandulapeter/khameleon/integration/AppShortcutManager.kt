package com.pandulapeter.khameleon.integration

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresApi
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.home.HomeActivity

class AppShortcutManager(context: Context) {
    private val implementation =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) RealImplementation(context) else object : Implementation {}

    fun onChatOpened() = implementation.trackAppShortcutUsage(CHAT_ID)

    fun onCalendarOpened() = implementation.trackAppShortcutUsage(CALENDAR_ID)

    fun onSongsOpened() = implementation.trackAppShortcutUsage(SONGS_ID)

    fun onSettingsOpened() = implementation.trackAppShortcutUsage(SETTINGS_ID)

    fun removeAppShortcuts() = implementation.removeAppShortcuts()

    fun updateAppShortcuts() = implementation.updateAppShortcuts()

    private interface Implementation {

        fun updateAppShortcuts() = Unit

        fun removeAppShortcuts() = Unit

        fun trackAppShortcutUsage(id: String) = Unit
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private class RealImplementation(private val context: Context) : Implementation {
        private val shortcutManager: ShortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager

        override fun updateAppShortcuts() {
            removeAppShortcuts()
            shortcutManager.dynamicShortcuts = listOf(
                createAppShortcut(CHAT_ID, context.getString(R.string.chat), R.drawable.ic_shortcut_chat_48dp),
                createAppShortcut(CALENDAR_ID, context.getString(R.string.calendar), R.drawable.ic_shortcut_calendar_48dp),
                createAppShortcut(SONGS_ID, context.getString(R.string.songs), R.drawable.ic_shortcut_songs_48dp),
                createAppShortcut(SETTINGS_ID, context.getString(R.string.settings), R.drawable.ic_shortcut_settings_48dp)
            )
        }

        override fun removeAppShortcuts() = shortcutManager.removeAllDynamicShortcuts()

        override fun trackAppShortcutUsage(id: String) = shortcutManager.reportShortcutUsed(id)

        private fun createAppShortcut(id: String, label: String, @DrawableRes icon: Int) = ShortcutInfo.Builder(context, id)
            .setShortLabel(label)
            .setIcon(Icon.createWithResource(context, icon))
            .setIntent(HomeActivity.getStartIntent(context, id).setAction(Intent.ACTION_VIEW))
            .build()
    }

    companion object {
        const val CHAT_ID = "chat"
        const val CALENDAR_ID = "calendar"
        const val SONGS_ID = "songs"
        const val SETTINGS_ID = "settings"
    }
}