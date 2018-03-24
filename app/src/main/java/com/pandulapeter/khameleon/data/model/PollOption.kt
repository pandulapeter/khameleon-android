package com.pandulapeter.khameleon.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PollOption(
    val optionName: String = "",
    val voters: List<User>? = null
) : Parcelable