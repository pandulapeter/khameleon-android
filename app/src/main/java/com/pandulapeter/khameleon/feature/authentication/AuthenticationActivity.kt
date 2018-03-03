package com.pandulapeter.khameleon.feature.authentication

import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.pandulapeter.khameleon.AuthenticationActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.repository.MessageRepository
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.home.HomeActivity
import com.pandulapeter.khameleon.feature.shared.KhameleonActivity
import com.pandulapeter.khameleon.util.GlideApp
import com.pandulapeter.khameleon.util.showSnackbar
import org.koin.android.ext.android.inject


class AuthenticationActivity : KhameleonActivity<AuthenticationActivityBinding>(R.layout.activity_authentication) {

    companion object {
        private const val AUTHENTICATION_REQUEST = 435
    }

    private val userRepository by inject<UserRepository>()
    private val messageRepository by inject<MessageRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Authentication)
        super.onCreate(savedInstanceState)
        if (userRepository.getSignedInUser() == null) {
            messageRepository.setPushNotificationsEnabled(false)
            binding.signInButton.setSize(SignInButton.SIZE_WIDE)
            binding.signInButton.setOnClickListener { startActivityForResult(userRepository.getSignInIntent(), AUTHENTICATION_REQUEST) }
            GlideApp.with(this)
                .load("https://scontent.fomr1-1.fna.fbcdn.net/v/t31.0-8/27164944_1597705330318762_2684267793485833634_o.jpg?oh=391d11915c86415c14de1a810db30c8b&oe=5B00B774")
                .into(binding.logo)
        } else {
            startHomeScreen()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == AUTHENTICATION_REQUEST) {
            try {
                userRepository.signIn(data)
            } catch (exception: ApiException) {
                binding.root.showSnackbar(getString(R.string.something_went_wrong_reason, GoogleSignInStatusCodes.getStatusCodeString(exception.statusCode)))
            }
            if (userRepository.getSignedInUser() != null) {
                startHomeScreen()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startHomeScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}