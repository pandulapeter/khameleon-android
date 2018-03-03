package com.pandulapeter.khameleon.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val avatar: String? = null
) : Parcelable