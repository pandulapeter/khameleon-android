package com.pandulapeter.khameleon.util

import android.databinding.BindingAdapter
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.TextAppearanceSpan
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.pandulapeter.khameleon.R
import java.util.*

@BindingAdapter("avatar")
fun setAvatar(view: ImageView, url: String) {
    GlideApp.with(view)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .into(view)
}

@BindingAdapter(value = ["title", "description"], requireAll = false)
fun setTitleDescription(view: TextView, title: String?, description: String?) {
    val text = SpannableString("${title ?: ""}\n${description ?: ""}")
    description?.let {
        text.setSpan(
            TextAppearanceSpan(view.context, R.style.TextAppearance_AppCompat_Caption),
            (title?.length ?: 0) + 1,
            text.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }
    view.text = text
}

@BindingAdapter("date")
fun setDate(view: TextView, date: Long) {
    view.text = DateFormat.format("MMM d, HH:mm", Date(date))
}