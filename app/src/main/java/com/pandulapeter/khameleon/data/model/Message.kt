package com.pandulapeter.khameleon.data.model

data class Message(
    var text: String = "",
    var sender: User? = null,
    var isImportant: Boolean = false,
    var timestamp: Long = System.currentTimeMillis()
)