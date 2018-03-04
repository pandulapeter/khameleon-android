package com.pandulapeter.khameleon.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Day(
    val timestamp: Long = 0L,
    val type: Int = EMPTY,
    val description: String = ""
) : Parcelable {

    companion object {
        const val EMPTY = -1
        const val BUSY = 0
        const val REHEARSAL = 1
        const val GIG = 2
        const val MEETUP = 3
    }
}