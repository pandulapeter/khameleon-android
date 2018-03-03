package com.pandulapeter.khameleon.data.model

data class Message(
    val text: String,
    val sender: User,
    val timestamp: Long
)