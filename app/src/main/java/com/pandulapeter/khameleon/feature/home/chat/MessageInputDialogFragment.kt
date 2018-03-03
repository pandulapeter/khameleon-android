package com.pandulapeter.khameleon.feature.home.chat

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.WindowManager
import com.pandulapeter.khameleon.MessageInputDialogBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.repository.MessageRepository
import com.pandulapeter.khameleon.util.hideKeyboard
import com.pandulapeter.khameleon.util.showKeyboard
import org.koin.android.ext.android.inject

class MessageInputDialogFragment : AppCompatDialogFragment() {

    companion object {
        fun show(fragmentManager: FragmentManager) {
            MessageInputDialogFragment().run { (this as DialogFragment).show(fragmentManager, tag) }
        }
    }

    private lateinit var binding: MessageInputDialogBinding
    private val positiveButton by lazy { (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE) }
    private val onDialogTextEnteredListener get() = parentFragment as? OnDialogTextEnteredListener ?: activity as? OnDialogTextEnteredListener
    private val messageRepository by inject<MessageRepository>()

    override fun onCreateDialog(savedInstanceState: Bundle?) = context?.let { context ->
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_message_input, null, false)
        binding.inputField.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                positiveButton.isEnabled = text.isTextValid()
                messageRepository.workInProgressMessageText = text.toString()
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        })
        AlertDialog.Builder(context, R.style.AlertDialog)
            .setView(binding.root)
            .setPositiveButton(R.string.send, { _, _ -> onOkButtonPressed() })
            .setNegativeButton(R.string.cancel, { _, _ -> messageRepository.workInProgressMessageText = "" })
            .create()
    } ?: super.onCreateDialog(savedInstanceState)

    override fun onStart() {
        super.onStart()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding.inputField.setText(messageRepository.workInProgressMessageText)
        binding.root.post {
            showKeyboard(binding.inputField)
            binding.inputField.setSelection(binding.inputField.text?.length ?: 0)
        }
        positiveButton.isEnabled = binding.inputField.text.isTextValid()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard(activity?.currentFocus)
    }

    private fun CharSequence?.isTextValid() = !isNullOrBlank()

    private fun onOkButtonPressed() {
        binding.inputField.text?.let {
            if (it.isTextValid()) {
                onDialogTextEnteredListener?.onTextEntered(it.toString(), binding.checkbox.isChecked)
                messageRepository.workInProgressMessageText = ""
                dismiss()
            }
        }
    }

    interface OnDialogTextEnteredListener {

        fun onTextEntered(text: String, isImportant: Boolean)
    }
}