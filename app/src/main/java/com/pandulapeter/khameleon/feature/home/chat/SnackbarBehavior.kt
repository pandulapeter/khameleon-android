package com.pandulapeter.khameleon.feature.home.chat

import android.content.Context
import android.graphics.Rect
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.Gravity
import android.view.View

class SnackbarBehavior @JvmOverloads constructor(context: Context? = null, attrs: AttributeSet? = null) : CoordinatorLayout.Behavior<View>(context, attrs) {

    override fun onAttachedToLayoutParams(lp: CoordinatorLayout.LayoutParams) {
        if (lp.dodgeInsetEdges == Gravity.NO_GRAVITY) {
            lp.dodgeInsetEdges = Gravity.BOTTOM
        }
    }

    override fun onLayoutChild(parent: CoordinatorLayout?, child: View?, layoutDirection: Int): Boolean {
        parent?.onLayoutChild(child, layoutDirection)
        return true
    }

    override fun getInsetDodgeRect(parent: CoordinatorLayout, child: View, rect: Rect): Boolean {
        rect.set(child.left, child.top, child.right, child.bottom)
        return true
    }
}
