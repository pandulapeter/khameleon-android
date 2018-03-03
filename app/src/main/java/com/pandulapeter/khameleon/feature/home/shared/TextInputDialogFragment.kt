package com.pandulapeter.khameleon.feature.home.shared

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.TextInputDialogBinding
import com.pandulapeter.khameleon.util.*

class TextInputDialogFragment : AppCompatDialogFragment() {

    companion object {
        private var Bundle?.title by BundleArgumentDelegate.Int("title")
        private var Bundle?.hint by BundleArgumentDelegate.Int("hint")
        private var Bundle?.positiveButton by BundleArgumentDelegate.Int("positiveButton")
        private var Bundle?.negativeButton by BundleArgumentDelegate.Int("negativeButton")
        private var Bundle?.isSingleLine by BundleArgumentDelegate.Boolean("isSingleLine")

        fun show(
            fragmentManager: FragmentManager,
            @StringRes title: Int,
            @StringRes hint: Int,
            @StringRes positiveButton: Int,
            @StringRes negativeButton: Int,
            isSingleLine: Boolean
        ) {
            TextInputDialogFragment().setArguments {
                it.title = title
                it.hint = hint
                it.positiveButton = positiveButton
                it.negativeButton = negativeButton
                it.isSingleLine = isSingleLine
            }.run { (this as DialogFragment).show(fragmentManager, tag) }
        }
    }

    private lateinit var binding: TextInputDialogBinding
    private val positiveButton by lazy { (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE) }
    private val onDialogTextEnteredListener get() = parentFragment as? OnDialogTextEnteredListener ?: activity as? OnDialogTextEnteredListener

    override fun onCreateDialog(savedInstanceState: Bundle?) = context?.let { context ->
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_text_input, null, false)
        binding.inputField.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                positiveButton.isEnabled = text.isTextValid()
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        })
        binding.inputFieldContainer.hint = context.getString(arguments.hint)
        AlertDialog.Builder(context, R.style.AlertDialog)
            .setView(binding.root)
            .setTitle(arguments.title)
            .setPositiveButton(arguments.positiveButton, { _, _ -> onOkButtonPressed() })
            .setNegativeButton(arguments.negativeButton, null)
            .create()
    } ?: super.onCreateDialog(savedInstanceState)

    override fun onStart() {
        super.onStart()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding.root.post {
            showKeyboard(binding.inputField)
            binding.inputField.setSelection(binding.inputField.text?.length ?: 0)
        }
        positiveButton.isEnabled = binding.inputField.text.isTextValid()
        if (arguments.isSingleLine) {
            binding.inputField.maxLines = 1
            binding.inputField.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    consume { onOkButtonPressed() }
                } else false
            }
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard(activity?.currentFocus)
    }

    private fun CharSequence?.isTextValid() = !isNullOrBlank()

    private fun onOkButtonPressed() {
        binding.inputField.text?.let {
            if (it.isTextValid()) {
                onDialogTextEnteredListener?.onTextEntered(it.toString())
                dismiss()
            }
        }
    }

    interface OnDialogTextEnteredListener {

        fun onTextEntered(text: String)
    }
}