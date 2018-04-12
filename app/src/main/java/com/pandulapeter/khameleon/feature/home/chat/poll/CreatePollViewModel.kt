package com.pandulapeter.khameleon.feature.home.chat.poll

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.pandulapeter.khameleon.util.onPropertyChanged

class CreatePollViewModel {
    val question = ObservableField("")
    val option1 = ObservableField("")
    val option2 = ObservableField("")
    val option3 = ObservableField("")
    val option4 = ObservableField("")
    val option5 = ObservableField("")
    val option1Visible = ObservableBoolean()
    val option2Visible = ObservableBoolean()
    val option3Visible = ObservableBoolean()
    val option4Visible = ObservableBoolean()
    val option5Visible = ObservableBoolean()
    var updateSendButtonVisibility: ((Boolean) -> Unit)? = null

    init {
        question.onPropertyChanged {
            option1Visible.set(it.isNotEmpty())
            updateSendButtonVisibility?.invoke(isValidInput())
        }
        option1.onPropertyChanged {
            option2Visible.set(it.isNotEmpty())
            updateSendButtonVisibility?.invoke(isValidInput())
        }
        option2.onPropertyChanged {
            option3Visible.set(it.isNotEmpty())
            updateSendButtonVisibility?.invoke(isValidInput())
        }
        option3.onPropertyChanged { option4Visible.set(it.isNotEmpty()) }
        option4.onPropertyChanged { option5Visible.set(it.isNotEmpty()) }
        option1Visible.onPropertyChanged {
            if (!it) {
                option2Visible.set(false)
            }
        }
        option2Visible.onPropertyChanged {
            if (!it) {
                option3Visible.set(false)
            }
        }
        option3Visible.onPropertyChanged {
            if (!it) {
                option4Visible.set(false)
            }
        }
        option4Visible.onPropertyChanged {
            if (!it) {
                option5Visible.set(false)
            }
        }
    }

    fun isValidInput() = (question.get() ?: "").isNotEmpty() && (option1.get() ?: "").isNotEmpty() && (option2.get() ?: "").isNotEmpty()
}