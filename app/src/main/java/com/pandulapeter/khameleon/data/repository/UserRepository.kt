package com.pandulapeter.khameleon.data.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.database.FirebaseDatabase
import com.pandulapeter.khameleon.data.model.User
import com.pandulapeter.khameleon.integration.AppShortcutManager

class UserRepository(context: Context, private val appShortcutManager: AppShortcutManager) {

    companion object {
        private const val USERS = "users"
    }

    private val googleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build())
    private var user: User? = null
        set(value) {
            field = value
            if (value == null) {
                appShortcutManager.removeAppShortcuts()
            } else {
                appShortcutManager.updateAppShortcuts()
            }
        }
    val whitelistedEmailAddressDataBase = FirebaseDatabase.getInstance().reference.child(UserRepository.USERS)!!

    init {
        GoogleSignIn.getLastSignedInAccount(context)?.loadFrom()
    }

    fun getSignInIntent() = googleSignInClient.signInIntent

    fun getSignedInUser() = user

    fun signIn(data: Intent, whitelistedEmailAddresses: List<String>) =
        GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java).loadFrom(whitelistedEmailAddresses)

    fun signOut(activity: Activity, onSuccess: () -> Unit = {}) {
        googleSignInClient.signOut()?.addOnCompleteListener(activity, {
            user = null
            onSuccess()
        })
    }

    private fun GoogleSignInAccount?.loadFrom(whitelistedEmailAddresses: List<String>? = null) {
        this?.let {
            val id = it.id
            val name = it.displayName
            if (id != null && name != null) {
                if (whitelistedEmailAddresses == null || whitelistedEmailAddresses.contains(it.email)) {
                    user = User(id, name, it.photoUrl?.toString())
                } else {
                    throw NotAMemberException()
                }
            }
        }
    }

    class NotAMemberException : Exception()
}