package com.pandulapeter.khameleon.util

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View

fun Context.color(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

fun View.showSnackbar(@StringRes message: Int) = showSnackbar(context.getString(message))

fun View.showSnackbar(message: String) = Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()