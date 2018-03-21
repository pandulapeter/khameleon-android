package com.pandulapeter.khameleon.feature.home.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.annotation.DrawableRes
import android.text.style.LineBackgroundSpan
import com.pandulapeter.khameleon.util.drawable


class IconSpan(context: Context, @DrawableRes resourceId: Int) : LineBackgroundSpan {

    private val drawable = context.drawable(resourceId)

    override fun drawBackground(
        canvas: Canvas, paint: Paint,
        left: Int, right: Int, top: Int, baseline: Int, bottom: Int,
        charSequence: CharSequence,
        start: Int, end: Int, lineNum: Int
    ) {
        drawable?.let {
            it.setBounds(
                (right / 2) - (it.intrinsicWidth / 2),
                (bottom / 2) - (it.intrinsicHeight / 2),
                (right / 2) + (it.intrinsicWidth / 2),
                (bottom / 2) + (it.intrinsicHeight / 2)
            )
            it.draw(canvas)
        }
    }
}