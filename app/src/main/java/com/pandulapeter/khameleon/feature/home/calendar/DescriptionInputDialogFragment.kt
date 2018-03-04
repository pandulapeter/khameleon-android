package com.pandulapeter.khameleon.feature.home.calendar

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.WindowManager
import com.pandulapeter.khameleon.DescriptionInputDialogBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Day
import com.pandulapeter.khameleon.util.BundleArgumentDelegate
import com.pandulapeter.khameleon.util.hideKeyboard
import com.pandulapeter.khameleon.util.setArguments
import com.pandulapeter.khameleon.util.showKeyboard

class DescriptionInputDialogFragment : AppCompatDialogFragment() {

    companion object {
        private var Bundle?.title by BundleArgumentDelegate.Int("title")
        private var Bundle?.day by BundleArgumentDelegate.Parcelable<Day>("day")

        fun show(fragmentManager: FragmentManager, day: Day, @StringRes title: Int) {
            DescriptionInputDialogFragment().setArguments {
                it.day = day
                it.title = title
            }.run { (this as DialogFragment).show(fragmentManager, tag) }
        }
    }

    private lateinit var binding: DescriptionInputDialogBinding
    private val onDialogTextEnteredListener get() = parentFragment as? OnDialogTextEnteredListener ?: activity as? OnDialogTextEnteredListener

    override fun onCreateDialog(savedInstanceState: Bundle?) = context?.let { context ->
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_description_input, null, false)
        AlertDialog.Builder(context, R.style.AlertDialog)
            .setTitle(arguments.title)
            .setView(binding.root)
            .setPositiveButton(R.string.save, { _, _ -> onOkButtonPressed() })
            .setNegativeButton(R.string.cancel, null)
            .create()
    } ?: super.onCreateDialog(savedInstanceState)

    override fun onStart() {
        super.onStart()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding.root.post {
            showKeyboard(binding.inputField)
            binding.inputField.setSelection(binding.inputField.text?.length ?: 0)
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard(activity?.currentFocus)
    }

    private fun onOkButtonPressed() {
        binding.inputField.text?.let { text ->
            arguments?.day?.let {
                onDialogTextEnteredListener?.onTextEntered(text.toString(), it)
                dismiss()
            }
        }
    }

    interface OnDialogTextEnteredListener {

        fun onTextEntered(text: String, day: Day)
    }
}