package com.pandulapeter.khameleon.feature.home.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SettingsFragmentBinding
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.feature.authentication.AuthenticationActivity
import com.pandulapeter.khameleon.feature.home.shared.AlertDialogFragment
import org.koin.android.ext.android.inject


class SettingsFragment : KhameleonFragment<SettingsFragmentBinding, SettingsViewModel>(R.layout.fragment_settings), AlertDialogFragment.OnDialogItemsSelectedListener {

    override val viewModel = SettingsViewModel()
    override val title = R.string.settings
    private val userRepository by inject<UserRepository>()

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