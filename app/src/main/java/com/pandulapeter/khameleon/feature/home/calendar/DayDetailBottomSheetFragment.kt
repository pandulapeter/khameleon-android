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
import android.view.ViewGroup
import com.pandulapeter.khameleon.DayDetailDialogBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.UserItemBinding
import com.pandulapeter.khameleon.data.model.Day
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.home.shared.CustomWidthBottomSheetDialog
import com.pandulapeter.khameleon.util.*
import org.koin.android.ext.android.inject
import java.util.*

class DayDetailBottomSheetFragment : AppCompatDialogFragment() {

    companion object {
        private var Bundle.day by BundleArgumentDelegate.Parcelable<Day>("day")
        private const val CUSTOM_TYPE = -4

        fun show(fragmentManager: FragmentManager, day: Day) {
            DayDetailBottomSheetFragment().setArguments {
                it.day = day
            }.run { (this as DialogFragment).show(fragmentManager, tag) }
        }
    }

    private lateinit var binding: DayDetailDialogBinding
    private val userRepository by inject<UserRepository>()
    private val onDialogItemSelectedListener get() = parentFragment as? OnDialogItemSelectedListener ?: activity as? OnDialogItemSelectedListener
    private val behavior: BottomSheetBehavior<*> by lazy { ((binding.root.parent as View).layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetBehavior<*> }
    private val viewModel = DayDetailBottomSheetViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?) = context?.let { context ->
        CustomWidthBottomSheetDialog(context, theme).apply {
            val layoutInflater = LayoutInflater.from(context)
            binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_day_detail, null, false)
            binding.viewModel = viewModel
            binding.empty.setOnClickListener { handleClick(Day.EMPTY) }
            binding.rehearsal.setOnClickListener { handleClick(Day.REHEARSAL) }
            binding.gig.setOnClickListener { handleClick(Day.GIG) }
            binding.meetup.setOnClickListener { handleClick(Day.MEETUP) }
            arguments?.day?.also {
                it.timestamp.let { binding.label.text = DateFormat.format("EEEE, MMMM d", Date(it)).toString().forceCapitalize() }
                binding.status.text = getString(
                    if (it.notGoodFor == null || it.notGoodFor?.isEmpty() == true) {
                        if (it.type == Day.BUSY) {
                            R.string.somebody_is_busy_on_this_day
                        } else {
                            R.string.the_day_is_good_for_everybody
                        }
                    } else {
                        R.string.who_has_other_plans
                    }
                )
                it.notGoodFor?.forEach {
                    binding.busyUsersContainer.addView(
                        DataBindingUtil.inflate<UserItemBinding>(layoutInflater, R.layout.item_user, binding.busyUsersContainer, false).apply {
                            model = it
                        }.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                when (it.type) {
                    Day.EMPTY, Day.BUSY -> binding.empty
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
                userRepository.getSignedInUser()?.let { user ->
                    if (it.notGoodFor?.any { it.id == user.id } == true) {
                        viewModel.goodForMe.set(false)
                    }
                }
                viewModel.goodForMe.onPropertyChanged { isItGoodForMe ->
                    arguments?.day?.let {
                        onDialogItemSelectedListener?.onItemClicked(CUSTOM_TYPE, it.apply {
                            userRepository.getSignedInUser()?.let { me ->
                                val currentItems = it.notGoodFor ?: listOf()
                                if (isItGoodForMe) {
                                    it.notGoodFor = currentItems.toMutableList().filter { it.id != me.id }
                                    if (it.notGoodFor?.isEmpty() == true && it.type == Day.BUSY) {
                                        it.type = Day.EMPTY
                                    }
                                } else {
                                    if (!currentItems.any { it.id == me.id }) {
                                        it.notGoodFor = currentItems.toMutableList().apply { add(me) }
                                    }
                                    if (it.type == Day.EMPTY) {
                                        it.type = Day.BUSY
                                    }
                                }
                            }
                        })
                    }
                    dismiss()
                }
            }
            setContentView(binding.root)
            binding.root.run { post { behavior.peekHeight = height } }
        }
    } ?: super.onCreateDialog(savedInstanceState)

    private fun handleClick(type: Int) {
        arguments?.day?.let {
            if (type == Day.EMPTY && (it.notGoodFor?.isEmpty() == false)) {
                onDialogItemSelectedListener?.onItemClicked(Day.BUSY, it)
            } else {
                onDialogItemSelectedListener?.onItemClicked(type, it)
            }
        }
        dismiss()
    }

    interface OnDialogItemSelectedListener {

        fun onItemClicked(itemType: Int, day: Day)
    }
}