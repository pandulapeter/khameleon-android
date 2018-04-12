package com.pandulapeter.khameleon.feature.home.calendar

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.LineBackgroundSpan


class IconSpan(private val drawable: Drawable) : LineBackgroundSpan {

    override fun drawBackground(
        canvas: Canvas, paint: Paint,
        left: Int, right: Int, top: Int, baseline: Int, bottom: Int,
        charSequence: CharSequence,
        start: Int, end: Int, lineNum: Int
    ) {
        drawable.run {
            setBounds(
                (right / 2) - (intrinsicWidth / 4),
                bottom,
                (right / 2) + (intrinsicWidth / 4),
                bottom + (intrinsicHeight / 2)
            )
            draw(canvas)
        }
    }
}