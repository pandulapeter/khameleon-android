package com.pandulapeter.khameleon.feature.home.calendar

import com.pandulapeter.khameleon.CalendarFragmentBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.KhameleonFragment

class CalendarFragment : KhameleonFragment<CalendarFragmentBinding, CalendarViewModel>(R.layout.fragment_calendar) {

    override val viewModel = CalendarViewModel()
    override val title = R.string.calendar
}