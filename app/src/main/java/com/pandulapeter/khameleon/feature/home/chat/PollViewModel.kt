package com.pandulapeter.khameleon.feature.home.chat

import android.text.format.DateFormat
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.util.forceCapitalize
import java.util.*

class PollViewModel(model: Message) {

    val background = if (model.isImportant) R.color.accent else 0
    val name: String = (model.sender?.getFormattedName() ?: "")
    val avatar = model.sender?.avatar ?: ""
    val timestamp = DateFormat.format("MMM d, HH:mm", Date(model.timestamp)).toString().forceCapitalize()
    val text = model.text
}