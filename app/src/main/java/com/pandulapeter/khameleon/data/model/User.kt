package com.pandulapeter.khameleon.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.regex.Pattern


@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val avatar: String? = null
) : Parcelable {

    fun getFormattedName(): String {
        val capBuffer = StringBuffer()
        val capMatcher = Pattern.compile("([a-z-áéíóőúű])([a-z-áéíóőúű]*)", Pattern.CASE_INSENSITIVE).matcher(name)
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase())
        }
        return capMatcher.appendTail(capBuffer).toString()
    }
}