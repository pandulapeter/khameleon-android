package com.pandulapeter.khameleon.feature.home.chat

import android.content.Context
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.util.color

class MessageViewModel(model: Message, context: Context) {
    val background = if (model.event !== null || model.song != null) R.color.primary else if (model.isImportant) R.color.accent else 0
    val textColor = context.color(if (model.event == null && model.song == null) R.color.dark else R.color.white)
    val nameColor = context.color(if (model.event == null && model.song == null) R.color.primary else R.color.accent)
    val name = model.sender?.name ?: ""
    val avatar = model.sender?.avatar ?: ""
    val timestamp = model.timestamp
    val text = if (model.event == null && model.song == null) model.text else "System message"
}