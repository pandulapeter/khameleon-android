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
import com.google.firebase.database.ValueEventListener
import com.pandulapeter.khameleon.CalendarFragmentBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Day
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.data.repository.CalendarRepository
import com.pandulapeter.khameleon.data.repository.ChatRepository
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.util.showSnackbar
import org.koin.android.ext.android.inject
import java.util.*

class CalendarFragment : KhameleonFragment<CalendarFragmentBinding, CalendarViewModel>(R.layout.fragment_calendar),
    ChangeEventListener, DayDetailBottomSheetFragment.OnDialogItemSelectedListener {

    override val viewModel = CalendarViewModel()
    private val calendarRepository by inject<CalendarRepository>()
    private val chatRepository by inject<ChatRepository>()
    private val userRepository by inject<UserRepository>()
    private val events = FirebaseArray(calendarRepository.calendarDatabase, ClassSnapshotParser(Day::class.java))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.calendarView.setMinimumDate(Calendar.getInstance().apply { timeInMillis -= 24 * 60 * 60 * 1000 })
        binding.calendarView.showCurrentMonthPage()
        binding.calendarView.setOnDayClickListener { eventDay ->
            if (eventDay.calendar.after(Calendar.getInstance())) {
                DayDetailBottomSheetFragment.show(
                    childFragmentManager,
                    events.findLast { it.timestamp.normalize() == eventDay.calendar.timeInMillis.normalize() } ?: Day(eventDay.calendar.timeInMillis.normalize()))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!events.isListening(this)) {
            events.addChangeEventListener(this)
        }
    }

    override fun onStop() {
        super.onStop()
        events.removeChangeEventListener(this)
    }

    override fun onItemClicked(itemType: Int, day: Day) {
        updateDay(day.apply { type = itemType })
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

    override fun onDataChanged() = Unit

    override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) = updateEvents()

    override fun onError(e: DatabaseError) = binding.root.showSnackbar(R.string.something_went_wrong)

    private fun updateEvents() {
        binding.calendarView.setEvents(events.map {
            EventDay(
                Calendar.getInstance().apply { timeInMillis = it.timestamp.normalize() },
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

    private fun updateDay(day: Day) {
        day.timestamp = day.timestamp.normalize()
        calendarRepository.calendarDatabase
            .orderByChild("timestamp")
            .equalTo(day.timestamp.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) = binding.root.showSnackbar(R.string.something_went_wrong)

                override fun onDataChange(p0: DataSnapshot?) {
                    p0?.let {
                        if (it.hasChildren()) {
                            if (day.type == Day.EMPTY) {
                                it.children.iterator().next().ref.removeValue()
                            } else {
                                it.children.iterator().next().ref.setValue(day)
                                events.findLast { it.timestamp.normalize() == day.timestamp }?.apply {
                                    type = day.type
                                    description = day.description
                                }
                            }
                            sendAutomaticChatMessage(day)
                            updateEvents()
                            return
                        }
                    }
                    sendAutomaticChatMessage(day)
                    calendarRepository.calendarDatabase
                        .push()
                        .setValue(day)
                }
            })
    }

    private fun sendAutomaticChatMessage(day: Day) {
        userRepository.getSignedInUser()?.let { user ->
            chatRepository.chatDatabase.push()
                .setValue(Message(UUID.randomUUID().toString(), "", user, false, day))
        }
    }

    private fun Long.normalize() = Calendar.getInstance().apply {
        timeInMillis = this@normalize
        timeZone = TimeZone.getTimeZone("GMT")
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}