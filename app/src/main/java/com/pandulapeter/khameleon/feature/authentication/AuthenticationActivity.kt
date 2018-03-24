package com.pandulapeter.khameleon.feature.authentication

import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.ClassSnapshotParser
import com.firebase.ui.database.FirebaseArray
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.pandulapeter.khameleon.AuthenticationActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.repository.ChatRepository
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.home.HomeActivity
import com.pandulapeter.khameleon.feature.shared.KhameleonActivity
import com.pandulapeter.khameleon.util.showSnackbar
import org.koin.android.ext.android.inject


class AuthenticationActivity : KhameleonActivity<AuthenticationActivityBinding>(R.layout.activity_authentication), ChangeEventListener {

    companion object {
        private const val AUTHENTICATION_REQUEST = 435
        private const val BACKGROUND_IMAGE_URL =
            "https://scontent.fomr1-1.fna.fbcdn.net/v/t31.0-8/27164591_1597697366986225_33167602144533526_o.jpg?oh=46bf46b9a89d225a77fbb77b4b629eec&oe=5B3F6B7E"
    }

    private val userRepository by inject<UserRepository>()
    private val messageRepository by inject<ChatRepository>()
    private val whitelistedEmailAddresses = FirebaseArray(userRepository.whitelistedEmailAddressDataBase, ClassSnapshotParser(String::class.java))

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Authentication)
        super.onCreate(savedInstanceState)
        if (userRepository.getSignedInUser() == null) {
            messageRepository.setPushNotificationsEnabled(false)
            binding.signInButton.setSize(SignInButton.SIZE_WIDE)
            binding.signInButton.setOnClickListener { startActivityForResult(userRepository.getSignInIntent(), AUTHENTICATION_REQUEST) }
            Glide.with(this)
                .load(BACKGROUND_IMAGE_URL)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.logo)
        } else {
            startHomeScreen()
        }
    }


    override fun onStart() {
        super.onStart()
        if (!whitelistedEmailAddresses.isListening(this)) {
            whitelistedEmailAddresses.addChangeEventListener(this)
        }
    }

    override fun onStop() {
        super.onStop()
        whitelistedEmailAddresses.removeChangeEventListener(this)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == AUTHENTICATION_REQUEST) {
            try {
                userRepository.signIn(data, whitelistedEmailAddresses)
            } catch (exception: ApiException) {
                binding.root.showSnackbar(getString(R.string.something_went_wrong_reason, GoogleSignInStatusCodes.getStatusCodeString(exception.statusCode)))
            } catch (exception: UserRepository.NotAMemberException) {
                binding.root.showSnackbar(getString(R.string.authentication_not_a_member))
            }
            if (userRepository.getSignedInUser() != null) {
                startHomeScreen()
            } else {
                userRepository.signOut(this)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDataChanged() = Unit

    override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) = Unit

    override fun onError(e: DatabaseError) = binding.root.showSnackbar(R.string.something_went_wrong)

    private fun startHomeScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}