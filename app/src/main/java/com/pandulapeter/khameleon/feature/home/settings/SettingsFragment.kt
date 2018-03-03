package com.pandulapeter.khameleon.feature.home.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SettingsFragmentBinding
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.feature.authentication.AuthenticationActivity
import com.pandulapeter.khameleon.feature.home.shared.AlertDialogFragment


class SettingsFragment : KhameleonFragment<SettingsFragmentBinding, SettingsViewModel>(R.layout.fragment_settings), AlertDialogFragment.OnDialogItemsSelectedListener {

    override val viewModel = SettingsViewModel()
    override val title = R.string.settings
    private val googleSignInClient by lazy {
        context?.let { GoogleSignIn.getClient(it, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()) }
    }

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
        activity?.let { activity ->
            googleSignInClient?.signOut()?.addOnCompleteListener(activity, {
                startActivity(Intent(activity, AuthenticationActivity::class.java))
                activity.finish()
            })
        }
    }
}