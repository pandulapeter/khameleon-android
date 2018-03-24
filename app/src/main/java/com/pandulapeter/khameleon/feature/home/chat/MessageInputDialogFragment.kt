package com.pandulapeter.khameleon.feature.home.chat

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.WindowManager
import com.pandulapeter.khameleon.MessageInputDialogBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.repository.ChatRepository
import com.pandulapeter.khameleon.util.*
import org.koin.android.ext.android.inject

class MessageInputDialogFragment : AppCompatDialogFragment() {

    companion object {
        private var Bundle?.title by BundleArgumentDelegate.Int("title")
        private var Bundle?.doneButton by BundleArgumentDelegate.Int("done_button")

        fun show(fragmentManager: FragmentManager, @StringRes title: Int, @StringRes doneButton: Int) {
            MessageInputDialogFragment().setArguments {
                it.title = title
                it.doneButton = doneButton
            }.run { (this as DialogFragment).show(fragmentManager, tag) }
        }
    }

    private lateinit var binding: MessageInputDialogBinding
    private val positiveButton by lazy { (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE) }
    private val onDialogTextEnteredListener get() = parentFragment as? OnDialogTextEnteredListener ?: activity as? OnDialogTextEnteredListener
    private val messageRepository by inject<ChatRepository>()

    override fun onCreateDialog(savedInstanceState: Bundle?) = context?.let { context ->
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_message_input, null, false)
        binding.inputField.onTextChanged {
            positiveButton.isEnabled = it.isTextValid()
            messageRepository.workInProgressMessageText = it
        }
        binding.checkboxAnnouncement.setOnCheckedChangeListener { _, isChecked -> messageRepository.workInProgressMessageImportant = isChecked }
        AlertDialog.Builder(context, R.style.AlertDialog)
            .setTitle(arguments.title)
            .setView(binding.root)
            .setPositiveButton(arguments.doneButton, { _, _ -> onOkButtonPressed() })
            .setNegativeButton(R.string.cancel, { _, _ ->
                messageRepository.workInProgressMessageText = ""
                messageRepository.workInProgressMessageImportant = false
            })
            .create()
    } ?: super.onCreateDialog(savedInstanceState)

    override fun onStart() {
        super.onStart()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding.inputField.setText(messageRepository.workInProgressMessageText)
        binding.checkboxAnnouncement.isChecked = messageRepository.workInProgressMessageImportant
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

    private fun CharSequence?.isTextValid() = this?.trim()?.isNotEmpty() == true

    private fun onOkButtonPressed() {
        binding.inputField.text?.let {
            if (it.isTextValid()) {
                onDialogTextEnteredListener?.onTextEntered(it.toString(), binding.checkboxAnnouncement.isChecked)
                messageRepository.workInProgressMessageText = ""
                messageRepository.workInProgressMessageImportant = false
                dismiss()
            }
        }
    }

    interface OnDialogTextEnteredListener {

        fun onTextEntered(text: String, isImportant: Boolean)
    }
}