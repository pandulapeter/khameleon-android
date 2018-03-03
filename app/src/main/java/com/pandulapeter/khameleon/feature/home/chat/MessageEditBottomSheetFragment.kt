package com.pandulapeter.khameleon.feature.home.chat

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import com.pandulapeter.khameleon.MessageEditDialogBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.util.BundleArgumentDelegate
import com.pandulapeter.khameleon.util.setArguments

class MessageEditBottomSheetFragment : AppCompatDialogFragment() {

    companion object {
        private var Bundle.message by BundleArgumentDelegate.Parcelable<Message>("message")

        fun show(fragmentManager: FragmentManager, message: Message) {
            MessageEditBottomSheetFragment().setArguments {
                it.message = message
            }.run { (this as DialogFragment).show(fragmentManager, tag) }
        }
    }

    private lateinit var binding: MessageEditDialogBinding
    private val onDialogItemSelectedListener get() = parentFragment as? OnDialogItemSelectedListener ?: activity as? OnDialogItemSelectedListener

    override fun onCreateDialog(savedInstanceState: Bundle?) = context?.let {
        BottomSheetDialog(it, theme).apply {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_message_edit, null, false)
            binding.editMessage.setOnClickListener {
                arguments?.message?.let { onDialogItemSelectedListener?.onEditSelected(it) }
                dismiss()
            }
            binding.deleteMessage.setOnClickListener {
                arguments?.message?.let { onDialogItemSelectedListener?.onDeleteSelected(it) }
                dismiss()
            }
            setContentView(binding.root)
        }
    } ?: super.onCreateDialog(savedInstanceState)

    interface OnDialogItemSelectedListener {

        fun onEditSelected(message: Message)

        fun onDeleteSelected(message: Message)
    }
}