package com.pandulapeter.khameleon.feature.home.chat

import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message


class MessageViewModel(model: Message) {
    val background = if (model.event != null || model.song != null) R.color.primary else if (model.isImportant) R.color.accent else 0
    val name = model.sender?.name ?: ""
    val avatar = model.sender?.avatar ?: ""
    val timestamp = model.timestamp
    val text = model.text
}