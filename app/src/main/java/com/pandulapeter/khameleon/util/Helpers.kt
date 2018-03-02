package com.pandulapeter.khameleon.util

inline fun consume(action: () -> Unit): Boolean {
    action()
    return true
}