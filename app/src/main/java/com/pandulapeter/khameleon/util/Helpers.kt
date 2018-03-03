package com.pandulapeter.khameleon.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

inline fun consume(action: () -> Unit): Boolean {
    action()
    return true
}

fun showKeyboard(focusedView: View?) = focusedView?.let {
    (it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(it, 0)
}

fun hideKeyboard(focusedView: View?) = focusedView?.let {
    (it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(it.windowToken, 0)
}