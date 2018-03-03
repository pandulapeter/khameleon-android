package com.pandulapeter.khameleon.feature.home.settings

import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SettingsFragmentBinding
import com.pandulapeter.khameleon.feature.KhameleonFragment

class SettingsFragment : KhameleonFragment<SettingsFragmentBinding, SettingsViewModel>(R.layout.fragment_settings) {
    override val viewModel = SettingsViewModel()
    override val title = R.string.settings
}