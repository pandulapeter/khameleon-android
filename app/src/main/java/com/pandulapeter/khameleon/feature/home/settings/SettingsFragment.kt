package com.pandulapeter.khameleon.feature.home.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SettingsFragmentBinding
import com.pandulapeter.khameleon.data.repository.MessageRepository
import com.pandulapeter.khameleon.data.repository.PreferenceRepository
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.feature.authentication.AuthenticationActivity
import com.pandulapeter.khameleon.feature.home.shared.AlertDialogFragment
import com.pandulapeter.khameleon.util.onPropertyChanged
import org.koin.android.ext.android.inject


class SettingsFragment : KhameleonFragment<SettingsFragmentBinding, SettingsViewModel>(R.layout.fragment_settings), AlertDialogFragment.OnDialogItemsSelectedListener {

    private val preferenceRepository by inject<PreferenceRepository>()
    override val viewModel = SettingsViewModel(preferenceRepository.shouldAllowPushNotifications)
    override val title = R.string.settings
    private val userRepository by inject<UserRepository>()
    private val messageRepository by inject<MessageRepository>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signOut.setOnClickListener {
            AlertDialogFragment.show(
                childFragmentManager,
                R.string.sign_out_confirmation_title,
                R.string.sign_out_confirmation_message,
                R.string.sign_out,
                R.string.cancel
            )
        }
        viewModel.shouldEnableChatPushNotifications.onPropertyChanged {
            preferenceRepository.shouldAllowPushNotifications = it
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