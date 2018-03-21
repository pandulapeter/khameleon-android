package com.pandulapeter.khameleon.feature.home.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SettingsFragmentBinding
import com.pandulapeter.khameleon.data.repository.ChatRepository
import com.pandulapeter.khameleon.data.repository.PreferenceRepository
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.feature.authentication.AuthenticationActivity
import com.pandulapeter.khameleon.feature.home.shared.AlertDialogFragment
import com.pandulapeter.khameleon.integration.AppShortcutManager
import com.pandulapeter.khameleon.util.onPropertyChanged
import org.koin.android.ext.android.inject


class SettingsFragment : KhameleonFragment<SettingsFragmentBinding, SettingsViewModel>(R.layout.fragment_settings), AlertDialogFragment.OnDialogItemsSelectedListener {

    private val preferenceRepository by inject<PreferenceRepository>()
    private val userRepository by inject<UserRepository>()
    private val messageRepository by inject<ChatRepository>()
    private val appShortcutManager by inject<AppShortcutManager>()
    override val viewModel = SettingsViewModel(
        preferenceRepository.chatNotifications,
        preferenceRepository.eventNotifications,
        userRepository.getSignedInUser()?.avatar ?: ""
    )
    override val title = R.string.settings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appShortcutManager.onSettingsOpened()
        binding.signOut.setOnClickListener {
            AlertDialogFragment.show(
                childFragmentManager,
                R.string.sign_out_confirmation_title,
                R.string.sign_out_confirmation_message,
                R.string.sign_out,
                R.string.cancel
            )
        }
        binding.checkForUpdates.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=com.pandulapeter.khameleon")
            })
        }
        viewModel.shouldEnableChatPushNotifications.onPropertyChanged {
            preferenceRepository.chatNotifications = it
            messageRepository.setPushNotificationsEnabled(it)
        }
    }

    override fun onPositiveButtonSelected() {
        activity?.let {
            userRepository.signOut(it) {
                startActivity(Intent(it, AuthenticationActivity::class.java))
                it.finish()
            }
        }
    }
}