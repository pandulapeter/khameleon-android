package com.pandulapeter.khameleon.feature.home.chat

import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.TypefaceSpan
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.util.forceCapitalize
import java.util.*

class PollViewModel(model: Message) {

    val background = if (model.isImportant) R.color.accent else 0
    val name: String = (model.sender?.getFormattedName() ?: "")
    val avatar = model.sender?.avatar ?: ""
    val timestamp = DateFormat.format("MMM d, HH:mm", Date(model.timestamp)).toString().forceCapitalize()
    val text = model.text
    val option1 = if (model.poll?.size ?: 0 >= 1 && model.poll?.get(0)?.optionName?.isNotEmpty() == true) model.poll[0] else null
    val option2 = if (model.poll?.size ?: 0 >= 2 && model.poll?.get(1)?.optionName?.isNotEmpty() == true) model.poll[1] else null
    val option3 = if (model.poll?.size ?: 0 >= 3 && model.poll?.get(2)?.optionName?.isNotEmpty() == true) model.poll[2] else null
    val option4 = if (model.poll?.size ?: 0 >= 4 && model.poll?.get(3)?.optionName?.isNotEmpty() == true) model.poll[3] else null
    val option5 = if (model.poll?.size ?: 0 >= 5 && model.poll?.get(4)?.optionName?.isNotEmpty() == true) model.poll[4] else null
    private val option1Voters = option1?.voters?.joinToString(",") { it.getFormattedName() } ?: ""
    private val option2Voters = option2?.voters?.joinToString(",") { it.getFormattedName() } ?: ""
    private val option3Voters = option3?.voters?.joinToString(",") { it.getFormattedName() } ?: ""
    private val option4Voters = option4?.voters?.joinToString(",") { it.getFormattedName() } ?: ""
    private val option5Voters = option5?.voters?.joinToString(",") { it.getFormattedName() } ?: ""
    val option1Text = getFormattedString(option1?.optionName ?: "", option1Voters)
    val option2Text = getFormattedString(option2?.optionName ?: "", option2Voters)
    val option3Text = getFormattedString(option3?.optionName ?: "", option3Voters)
    val option4Text = getFormattedString(option4?.optionName ?: "", option4Voters)
    val option5Text = getFormattedString(option5?.optionName ?: "", option5Voters)

    fun onOption1Clicked() = Unit

    fun onOption2Clicked() = Unit

    fun onOption3Clicked() = Unit

    fun onOption4Clicked() = Unit

    fun onOption5Clicked() = Unit

    private fun getFormattedString(firstPart: String, secondPart: String) =
        if (secondPart.isEmpty()) SpannableString(firstPart) else SpannableString("$firstPart\n$secondPart").apply {
            //            setSpan(TypefaceSpan("sans-serif-medium"), 0, firstPart.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            setSpan(TypefaceSpan("sans-serif-medium"), firstPart.length + 1, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
}