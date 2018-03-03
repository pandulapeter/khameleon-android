package com.pandulapeter.khameleon.feature.home.chat

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.StyleRes
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatDialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pandulapeter.khameleon.MessageEditDialogBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.util.BundleArgumentDelegate
import com.pandulapeter.khameleon.util.dimension
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
    private val behavior: BottomSheetBehavior<*> by lazy { ((binding.root.parent as View).layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetBehavior<*> }

    override fun onCreateDialog(savedInstanceState: Bundle?) = context?.let {
        CustomWidthBottomSheetDialog(it, theme).apply {
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
            binding.root.run { post { behavior.peekHeight = height } }
        }
    } ?: super.onCreateDialog(savedInstanceState)

    private class CustomWidthBottomSheetDialog(context: Context, @StyleRes theme: Int) : BottomSheetDialog(context, theme) {
        private val width = context.dimension(R.dimen.bottom_sheet_width)
        val isFullWidth = width == 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (!isFullWidth) {
                window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                window.setGravity(Gravity.BOTTOM)
            }
        }
    }

    interface OnDialogItemSelectedListener {

        fun onEditSelected(message: Message)

        fun onDeleteSelected(message: Message)
    }
}