package com.pandulapeter.khameleon.feature.home.calendar

import android.os.Bundle
import android.view.View
import com.applandeo.materialcalendarview.EventDay
import com.pandulapeter.khameleon.CalendarFragmentBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.KhameleonFragment
import java.util.*

class CalendarFragment : KhameleonFragment<CalendarFragmentBinding, CalendarViewModel>(R.layout.fragment_calendar) {

    override val viewModel = CalendarViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.calendarView.setMinimumDate(Calendar.getInstance().apply {
            set(Calendar.YEAR, 2018)
            set(Calendar.MONTH, 2)
            set(Calendar.DAY_OF_MONTH, 1)
        })
        binding.calendarView.showCurrentMonthPage()
        binding.calendarView.setOnDayClickListener { binding.calendarView.setEvents(listOf(EventDay(it.calendar, R.drawable.ic_day_busy_24dp))) }
    }
}