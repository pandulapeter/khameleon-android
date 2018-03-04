package com.pandulapeter.khameleon.feature.home.chat

import android.content.Context
import android.text.format.DateFormat
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Day
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.data.model.Song
import com.pandulapeter.khameleon.util.color
import com.pandulapeter.khameleon.util.drawable
import java.util.*

class MessageViewModel(model: Message, context: Context) {
    val systemMessage = model.event != null || model.song != null
    val background = if (model.isImportant) R.color.accent else 0
    val nameColor = context.color(if (systemMessage) R.color.light else R.color.primary)
    val name: String = (model.sender?.name ?: "").let {
        when (model.event?.type) {
            Day.EMPTY -> context.getString(R.string.day_cleared, it, model.event.timestamp.format())
            Day.BUSY -> context.getString(R.string.day_marked_busy, it, model.event.timestamp.format())
            Day.REHEARSAL -> context.getString(R.string.day_marked_rehearsal, it, model.event.timestamp.format())
            Day.GIG -> context.getString(R.string.day_marked_gig, it, model.event.timestamp.format())
            Day.MEETUP -> context.getString(R.string.day_marked_meetup, it, model.event.timestamp.format())
            else -> when (model.song) {
                is Song -> context.getString(R.string.song_added, it, model.song.artist, model.song.title, model.song.key)
                else -> it
            }
        }
    }
    val icon = context.drawable(
        when (model.event?.type) {
            Day.EMPTY -> R.drawable.ic_day_empty_24dp
            Day.BUSY -> R.drawable.ic_day_busy_24dp
            Day.REHEARSAL -> R.drawable.ic_day_rehearsal_24dp
            Day.GIG -> R.drawable.ic_day_gig_24dp
            Day.MEETUP -> R.drawable.ic_day_meetup_24dp
            else -> when (model.song) {
                is Song -> R.drawable.ic_songs_24dp
                else -> R.drawable.ic_chat_24dp
            }
        }
    )
    val avatar = model.sender?.avatar ?: ""
    val timestamp = DateFormat.format("MMM d, HH : mm", Date(model.timestamp))
    val text = model.event?.description ?: model.text

    private fun Long.format() = DateFormat.format("EEEE, MMMM d", Date(this))
}