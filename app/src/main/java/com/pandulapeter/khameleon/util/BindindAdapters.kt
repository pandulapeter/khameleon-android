package com.pandulapeter.khameleon.util

import android.databinding.BindingAdapter
import android.support.annotation.ColorRes
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.pandulapeter.khameleon.R
import java.util.*

@BindingAdapter("android:visibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("android:background")
fun setBackground(view: View, @ColorRes color: Int) {
    if (color == 0) {
        view.background = null
    } else {
        view.setBackgroundColor(view.context.color(color))
    }
}

@BindingAdapter("avatar")
fun setAvatar(view: ImageView, url: String) {
    GlideApp.with(view)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .placeholder(view.context.drawable(R.drawable.bg_placeholder))
        .into(view)
}

@BindingAdapter(value = ["title", "description"], requireAll = false)
fun setTitleDescription(view: TextView, title: String?, description: String?) {
    view.text = SpannableString("${title ?: ""}\n${description ?: ""}").apply {
        title?.let {
            setSpan(TypefaceSpan("sans-serif-medium"), 0, it.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            setSpan(ForegroundColorSpan(view.context.color(R.color.dark)), 0, it.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        description?.let {
            setSpan(TextAppearanceSpan(view.context, R.style.TextAppearance_AppCompat_Caption), (title?.length ?: 0) + 1, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            setSpan(TypefaceSpan("sans-serif-regular"), (title?.length ?: 0) + 1, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }
}

@BindingAdapter("date")
fun setDate(view: TextView, date: Long) {
    view.text = DateFormat.format("MMM d, HH:mm", Date(date))
}