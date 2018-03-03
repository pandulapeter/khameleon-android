package com.pandulapeter.khameleon.data.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.pandulapeter.khameleon.data.model.User

class UserRepository(context: Context) {
    private val googleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build())
    private var user: User? = null

    init {
        GoogleSignIn.getLastSignedInAccount(context)?.loadFrom()
    }

    fun getSignInIntent() = googleSignInClient.signInIntent

    fun getSignedInUser() = user

    fun signIn(data: Intent) = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java).loadFrom()

    fun signOut(activity: Activity, onSuccess: () -> Unit) {
        googleSignInClient.signOut()?.addOnCompleteListener(activity, {
            user = null
            onSuccess()
        })
    }

    private fun GoogleSignInAccount?.loadFrom() {
        this?.let {
            val id = it.id
            val name = it.displayName
            if (id != null && name != null) {
                user = User(id, name, it.photoUrl?.toString())
            }
        }
    }
}