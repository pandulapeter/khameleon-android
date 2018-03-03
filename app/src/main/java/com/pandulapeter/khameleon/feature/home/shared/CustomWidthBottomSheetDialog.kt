package com.pandulapeter.khameleon.feature.home.shared

import android.content.Context
import android.os.Bundle
import android.support.annotation.StyleRes
import android.support.design.widget.BottomSheetDialog
import android.view.Gravity
import android.view.ViewGroup
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.util.dimension

class CustomWidthBottomSheetDialog(context: Context, @StyleRes theme: Int) : BottomSheetDialog(context, theme) {
    private val width = context.dimension(R.dimen.bottom_sheet_width)
    val isFullWidth = width == 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isFullWidth) {
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.BOTTOM)
        }
    }
}