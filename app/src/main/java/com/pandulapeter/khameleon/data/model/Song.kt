package com.pandulapeter.khameleon.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val id: String = "",
    val artist: String = "",
    val title: String = "",
    val key: String = "",
    var order: Int = 0,
    var bpm: Int = 0,
    var isHighlighted: Boolean = false,
    var isArchived: Boolean = false
) : Parcelable