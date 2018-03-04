package com.pandulapeter.khameleon.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val artist: String = "",
    val title: String = "",
    val key: String = "",
    val tag: Int = 0
) : Parcelable