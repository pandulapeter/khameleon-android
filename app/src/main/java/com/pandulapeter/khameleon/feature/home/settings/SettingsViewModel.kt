package com.pandulapeter.khameleon.feature.home.settings

import com.pandulapeter.khameleon.BuildConfig
import com.pandulapeter.khameleon.feature.KhameleonViewModel

class SettingsViewModel : KhameleonViewModel() {

    val version = BuildConfig.VERSION_NAME
}