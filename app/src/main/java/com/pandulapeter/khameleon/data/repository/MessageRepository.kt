package com.pandulapeter.khameleon.data.repository

import com.google.firebase.messaging.FirebaseMessaging

class MessageRepository {
    companion object {
        private const val CHAT_TOPIC = "chat"
    }

    var workInProgressMessageText = ""

    fun setPushNotificationsEnabled(enabled: Boolean) {
        FirebaseMessaging.getInstance().run {
            if (enabled) {
                subscribeToTopic(CHAT_TOPIC)
            } else {
                unsubscribeFromTopic(CHAT_TOPIC)
            }
        }
    }
}