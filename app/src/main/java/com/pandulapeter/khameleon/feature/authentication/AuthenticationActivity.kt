package com.pandulapeter.khameleon.feature.authentication

import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.pandulapeter.khameleon.AuthenticationActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.home.HomeActivity
import com.pandulapeter.khameleon.feature.shared.KhameleonActivity
import com.pandulapeter.khameleon.util.showSnackbar


class AuthenticationActivity : KhameleonActivity<AuthenticationActivityBinding>(R.layout.activity_authentication) {

    companion object {
        private const val AUTHENTICATION_REQUEST = 435
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            setTheme(R.style.AppTheme_Authentication)
            super.onCreate(savedInstanceState)
            setTitle(R.string.authentication)
            val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build())
            binding.signInButton.setOnClickListener { startActivityForResult(googleSignInClient.signInIntent, AUTHENTICATION_REQUEST) }
        } else {
            startHomeScreen(account)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == AUTHENTICATION_REQUEST) {
            try {
                startHomeScreen(GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java))
            } catch (ignored: ApiException) {
                binding.root.showSnackbar(R.string.something_went_wrong)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startHomeScreen(account: GoogleSignInAccount) {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}