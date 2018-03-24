package com.pandulapeter.khameleon.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PollOption(
    val optionName: String = "",
    var voters: List<User>? = null
) : Parcelable