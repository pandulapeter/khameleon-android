package com.pandulapeter.khameleon.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message(
    val id: String = "",
    val text: String = "",
    val sender: User? = null,
    var isImportant: Boolean = false,
    val event: Day? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable