package com.pandulapeter.khameleon.feature.home.calendar

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatDialogFragment
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import com.pandulapeter.khameleon.DayDetailDialogBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Day
import com.pandulapeter.khameleon.feature.home.shared.CustomWidthBottomSheetDialog
import com.pandulapeter.khameleon.util.BundleArgumentDelegate
import com.pandulapeter.khameleon.util.color
import com.pandulapeter.khameleon.util.setArguments
import java.util.*

class DayDetailBottomSheetFragment : AppCompatDialogFragment() {

    companion object {
        private var Bundle.day by BundleArgumentDelegate.Parcelable<Day>("day")

        fun show(fragmentManager: FragmentManager, day: Day) {
            DayDetailBottomSheetFragment().setArguments {
                it.day = day
            }.run { (this as DialogFragment).show(fragmentManager, tag) }
        }
    }

    private lateinit var binding: DayDetailDialogBinding
    private val onDialogItemSelectedListener get() = parentFragment as? OnDialogItemSelectedListener ?: activity as? OnDialogItemSelectedListener
    private val behavior: BottomSheetBehavior<*> by lazy { ((binding.root.parent as View).layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetBehavior<*> }

    override fun onCreateDialog(savedInstanceState: Bundle?) = context?.let { context ->
        CustomWidthBottomSheetDialog(context, theme).apply {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_day_detail, null, false)
            binding.empty.setOnClickListener { handleClick(Day.EMPTY) }
            binding.busy.setOnClickListener { handleClick(Day.BUSY) }
            binding.rehearsal.setOnClickListener { handleClick(Day.REHEARSAL) }
            binding.gig.setOnClickListener { handleClick(Day.GIG) }
            binding.meetup.setOnClickListener { handleClick(Day.MEETUP) }
            arguments?.day?.also {
                it.timestamp.let { binding.label.text = DateFormat.format("EEEE, MMMM d", Date(it)) }
                when (it.type) {
                    Day.EMPTY -> binding.empty
                    Day.BUSY -> binding.busy
                    Day.REHEARSAL -> binding.rehearsal
                    Day.GIG -> binding.gig
                    Day.MEETUP -> binding.meetup
                    else -> null
                }?.run {
                    setBackgroundColor(context.color(R.color.accent))
                    setOnClickListener(null)
                    it.description.let {
                        if (it.isNotEmpty()) {
                            text = context.getString(R.string.description_pattern, text, it)
                        }
                    }
                }
            }
            setContentView(binding.root)
            binding.root.run { post { behavior.peekHeight = height } }
        }
    } ?: super.onCreateDialog(savedInstanceState)

    private fun handleClick(type: Int) {
        arguments?.day?.let { onDialogItemSelectedListener?.onItemClicked(type, it) }
        dismiss()
    }

    interface OnDialogItemSelectedListener {

        fun onItemClicked(itemType: Int, day: Day)
    }
}