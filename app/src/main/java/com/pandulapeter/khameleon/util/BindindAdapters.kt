package com.pandulapeter.khameleon.util

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.ColorRes
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.pandulapeter.khameleon.R

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
    Glide.with(view)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .apply(RequestOptions.placeholderOf(R.drawable.bg_placeholder))
        .into(view)
}

@BindingAdapter("gifUrl")
fun setGifUrl(view: ImageView, url: String) {
    Glide.with(view.context)
        .asDrawable()
        .load(Uri.parse(url))
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean) = false

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                (resource as? GifDrawable)?.start()
                //TODO: Not working.
                return false
            }
        })
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