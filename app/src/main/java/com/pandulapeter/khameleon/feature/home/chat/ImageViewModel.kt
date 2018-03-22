package com.pandulapeter.khameleon.feature.home.chat

import android.content.Context
import android.text.format.DateFormat
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.util.color
import java.util.*

class ImageViewModel(model: Message, context: Context) {

    val nameColor = context.color(R.color.primary)
    val name: String = (model.sender?.getFormattedName() ?: "")
    val avatar = model.sender?.avatar ?: ""
    val timestamp = DateFormat.format("MMM d, HH:mm", Date(model.timestamp)).toString().capitalize()
    val gifUrl = model.gifUrl
}