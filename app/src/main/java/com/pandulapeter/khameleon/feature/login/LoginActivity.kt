package com.pandulapeter.khameleon.feature.login

import android.os.Bundle
import android.os.PersistableBundle
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.pandulapeter.khameleon.LoginActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.shared.KhameleonActivity


class LoginActivity : KhameleonActivity<LoginActivityBinding>(R.layout.activity_login) {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }
}