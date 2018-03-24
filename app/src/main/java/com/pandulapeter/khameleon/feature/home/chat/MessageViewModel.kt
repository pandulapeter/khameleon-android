package com.pandulapeter.khameleon.feature.home.chat

import android.content.Context
import android.text.format.DateFormat
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Day
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.data.model.Song
import com.pandulapeter.khameleon.util.color
import com.pandulapeter.khameleon.util.drawable
import com.pandulapeter.khameleon.util.forceCapitalize
import java.util.*

class MessageViewModel(model: Message, context: Context) {
    companion object {
        const val SONG_ADDED = "added"
        const val SONG_REMOVED = "removed"
    }

    val systemMessage = model.event != null || model.song != null
    val background = if (model.isImportant) R.color.accent else 0
    val nameColor = context.color(if (systemMessage) R.color.light else R.color.primary)
    val linkColor = context.color(if (model.isImportant) R.color.primary else R.color.accent)
    val name: String = (model.sender?.getFormattedName() ?: "").let {
        when {
            model.event != null -> context.getString(R.string.day_modified, it, model.event.timestamp.format())
            model.song != null -> context.getString(
                if (model.text == SONG_ADDED) R.string.song_added_pattern else R.string.song_deleted_pattern,
                it,
                model.song.artist,
                model.song.title
            )
            else -> it
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
                is Song -> R.drawable.ic_icon_song_24dp
                else -> R.drawable.ic_chat_24dp
            }
        }
    )
    val avatar = model.sender?.avatar ?: ""
    val timestamp = DateFormat.format("MMM d, HH:mm", Date(model.timestamp)).toString().forceCapitalize()
    val text = if (model.song == null) model.event?.getDescription(context) ?: model.text else ""

    private fun Long.format() = DateFormat.format("EEEE, MMMM d", Date(this)).toString().forceCapitalize()

    private fun Day.getDescription(context: Context) = if (description.isEmpty()) "" else when (type) {
        Day.REHEARSAL -> context.getString(R.string.rehearsal_starts_from, description)
        Day.MEETUP -> context.getString(R.string.meetup_at, description)
        Day.GIG -> context.getString(R.string.gig_at, description)
        else -> ""
    }
}