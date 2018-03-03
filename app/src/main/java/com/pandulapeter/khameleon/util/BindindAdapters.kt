package com.pandulapeter.khameleon.util

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("avatar")
fun setAvatar(view: ImageView, url: String) {
    GlideApp.with(view)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .into(view)
}