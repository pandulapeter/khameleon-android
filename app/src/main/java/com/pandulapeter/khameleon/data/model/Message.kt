package com.pandulapeter.khameleon.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Message(
    var id: String = "",
    var text: String = "",
    var sender: User? = null,
    var isImportant: Boolean = false,
    var timestamp: Long = System.currentTimeMillis()
) : Parcelable