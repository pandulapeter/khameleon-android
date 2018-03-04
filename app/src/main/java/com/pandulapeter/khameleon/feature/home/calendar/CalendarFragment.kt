package com.pandulapeter.khameleon.feature.home.calendar

import android.os.Bundle
import android.view.View
import com.pandulapeter.khameleon.CalendarFragmentBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Day
import com.pandulapeter.khameleon.feature.KhameleonFragment
import java.util.*

class CalendarFragment : KhameleonFragment<CalendarFragmentBinding, CalendarViewModel>(R.layout.fragment_calendar), DayDetailBottomSheetFragment.OnDialogItemSelectedListener {

    override val viewModel = CalendarViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.calendarView.setMinimumDate(Calendar.getInstance().apply { timeInMillis -= 24 * 60 * 60 * 1000 })
        binding.calendarView.showCurrentMonthPage()
        binding.calendarView.setOnDayClickListener {
            if (it.calendar.after(Calendar.getInstance())) {
                DayDetailBottomSheetFragment.show(childFragmentManager, Day(it.calendar.timeInMillis))
            }
        }
    }

    override fun onItemClicked(itemType: Int, day: Day) {
        when (itemType) {
            Day.EMPTY -> {
            }
            Day.BUSY -> {
            }
            Day.REHEARSAL -> {
            }
            Day.GIG -> {
            }
            Day.MEETUP -> {
            }
        }
    }
}