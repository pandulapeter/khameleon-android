package com.pandulapeter.khameleon.data.model

data class Message(
    var text: String = "",
    var sender: User? = null,
    var timestamp: Long = System.currentTimeMillis()
)