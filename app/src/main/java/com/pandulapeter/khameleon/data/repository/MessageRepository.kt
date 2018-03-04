package com.pandulapeter.khameleon.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class MessageRepository {
    companion object {
        private const val CHAT_TOPIC = "chat"
        private const val CHAT = "chat"
        private const val NOTIFICATIONS = "notificationRequests"
    }

    private val databaseReference = FirebaseDatabase.getInstance().reference
    var workInProgressMessageText = ""
    var workInProgressMessageImportant = false
    val chatDatabase = databaseReference.child(CHAT)!!
    val notificationsDatabase = databaseReference.child(NOTIFICATIONS)!!

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