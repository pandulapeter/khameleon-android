package com.pandulapeter.khameleon.feature.home.settings

import android.databinding.ObservableBoolean
import com.pandulapeter.khameleon.BuildConfig
import com.pandulapeter.khameleon.feature.KhameleonViewModel

class SettingsViewModel(shouldEnableChatPushNotifications: Boolean, val avatarImage: String) : KhameleonViewModel() {

    val version = BuildConfig.VERSION_NAME
    val shouldEnableChatPushNotifications = ObservableBoolean(shouldEnableChatPushNotifications)
}