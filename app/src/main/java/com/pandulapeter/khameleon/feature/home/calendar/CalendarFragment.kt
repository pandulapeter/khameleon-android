package com.pandulapeter.khameleon.feature.home.calendar

import android.os.Bundle
import android.view.View
import com.applandeo.materialcalendarview.EventDay
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.ClassSnapshotParser
import com.firebase.ui.database.FirebaseArray
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.pandulapeter.khameleon.CalendarFragmentBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Day
import com.pandulapeter.khameleon.data.repository.CalendarRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.util.showSnackbar
import org.koin.android.ext.android.inject
import java.util.*

class CalendarFragment : KhameleonFragment<CalendarFragmentBinding, CalendarViewModel>(R.layout.fragment_calendar),
    ChangeEventListener, DayDetailBottomSheetFragment.OnDialogItemSelectedListener {

    override val viewModel = CalendarViewModel()
    private val calendarRepository by inject<CalendarRepository>()
    private val events = FirebaseArray(calendarRepository.calendarDatabase, ClassSnapshotParser(Day::class.java))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.calendarView.setMinimumDate(Calendar.getInstance().apply { timeInMillis -= 24 * 60 * 60 * 1000 })
        binding.calendarView.showCurrentMonthPage()
        binding.calendarView.setOnDayClickListener {
            if (it.calendar.after(Calendar.getInstance())) {
                DayDetailBottomSheetFragment.show(childFragmentManager, Day(it.calendar.timeInMillis))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        events.addChangeEventListener(this)
    }

    override fun onStop() {
        super.onStop()
        events.removeChangeEventListener(this)
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

    override fun onDataChanged() {
        updateEvents()
    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) {
        updateEvents()
    }

    override fun onError(e: DatabaseError) = binding.root.showSnackbar(R.string.something_went_wrong)

    private fun updateEvents() {
        binding.calendarView.setEvents(events.map {
            EventDay(
                Calendar.getInstance().apply { timeInMillis = it.timestamp },
                when (it.type) {
                    Day.BUSY -> R.drawable.ic_day_busy_24dp
                    Day.GIG -> R.drawable.ic_day_gig_24dp
                    Day.MEETUP -> R.drawable.ic_day_meetup_24dp
                    Day.REHEARSAL -> R.drawable.ic_day_rehearsal_24dp
                    else -> 0
                }
            )
        })
    }
}