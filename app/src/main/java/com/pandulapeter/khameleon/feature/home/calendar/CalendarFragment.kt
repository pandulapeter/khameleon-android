package com.pandulapeter.khameleon.feature.home.calendar

import android.app.TimePickerDialog
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.text.style.AbsoluteSizeSpan
import android.text.style.TextAppearanceSpan
import android.view.View
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
import com.pandulapeter.khameleon.integration.AppShortcutManager
import com.pandulapeter.khameleon.util.color
import com.pandulapeter.khameleon.util.drawable
import com.pandulapeter.khameleon.util.showSnackbar
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import org.koin.android.ext.android.inject
import java.util.*

class CalendarFragment : KhameleonFragment<CalendarFragmentBinding, CalendarViewModel>(R.layout.fragment_calendar),
    ChangeEventListener,
    DayDetailBottomSheetFragment.OnDialogItemSelectedListener,
    DescriptionInputDialogFragment.OnDialogTextEnteredListener {

    companion object {
        private const val TIME_FORMAT = "%02d:%02d"
    }

    override val viewModel = CalendarViewModel()
    override val title = R.string.calendar
    private val calendarRepository by inject<CalendarRepository>()
    private val chatRepository by inject<ChatRepository>()
    private val userRepository by inject<UserRepository>()
    private val appShortcutManager by inject<AppShortcutManager>()
    private val events = FirebaseArray(calendarRepository.calendarDatabase, ClassSnapshotParser(Day::class.java))
    private var isInvalidationScheduled = false
    private val invalidateRunnable = Runnable {
        if (isAdded) {
            binding.calendarView.invalidateDecorators()
        }
        isInvalidationScheduled = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appShortcutManager.onCalendarOpened()
        val today = CalendarDay.today()
        binding.calendarView.state().edit().setMinimumDate(today).commit()
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                DayDetailBottomSheetFragment.show(
                    childFragmentManager,
                    events.findLast { it.timestamp.normalize() == date.calendar.timeInMillis.normalize() } ?: Day(date.calendar.timeInMillis.normalize()))
                widget.clearSelection()
            }
        }
        val sizeSpan = AbsoluteSizeSpan(0)

        fun getDayType(day: CalendarDay?) = events.find { it.timestamp.normalize() == day?.calendar?.timeInMillis?.normalize() }?.type ?: Day.EMPTY

        fun getDisabledDecorator(type: Int, @DrawableRes resourceId: Int) = object : DayViewDecorator {
            private val span = IconSpan(view.context.drawable(resourceId)?.mutate()?.apply {
                setTint(view.context.color(R.color.disabled))
            } ?: throw IllegalStateException("Drawable cannot be null"))

            override fun shouldDecorate(day: CalendarDay) = day.isBefore(today) && getDayType(day) == type

            override fun decorate(view: DayViewFacade) = view.run {
                addSpan(sizeSpan)
                addSpan(span)
            }
        }

        fun getEnabledDecorator(type: Int, @DrawableRes resourceId: Int) = object : DayViewDecorator {
            private val span = IconSpan(view.context.drawable(resourceId) ?: throw IllegalStateException("Drawable cannot be null"))

            override fun shouldDecorate(day: CalendarDay) = !day.isBefore(today) && getDayType(day) == type

            override fun decorate(view: DayViewFacade) = view.run {
                addSpan(sizeSpan)
                addSpan(span)
            }
        }

        binding.calendarView.addDecorators(
            object : DayViewDecorator {
                override fun shouldDecorate(day: CalendarDay) = day == today

                override fun decorate(view: DayViewFacade) = view.addSpan(TextAppearanceSpan(context, R.style.CalendarToday))
            },
            getDisabledDecorator(Day.BUSY, R.drawable.ic_day_busy_24dp),
            getEnabledDecorator(Day.BUSY, R.drawable.ic_day_busy_24dp),
            getDisabledDecorator(Day.REHEARSAL, R.drawable.ic_day_rehearsal_24dp),
            getEnabledDecorator(Day.REHEARSAL, R.drawable.ic_day_rehearsal_24dp),
            getDisabledDecorator(Day.GIG, R.drawable.ic_day_gig_24dp),
            getEnabledDecorator(Day.GIG, R.drawable.ic_day_gig_24dp),
            getDisabledDecorator(Day.MEETUP, R.drawable.ic_day_meetup_24dp),
            getEnabledDecorator(Day.MEETUP, R.drawable.ic_day_meetup_24dp)
        )
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
        when (itemType) {
            Day.BUSY,
            Day.EMPTY -> updateDay(day.apply { type = itemType })
            Day.REHEARSAL -> context?.let {
                TimePickerDialog(it, R.style.AlertDialog, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    updateDay(day.apply {
                        type = itemType
                        description = String.format(Locale.getDefault(), TIME_FORMAT, hourOfDay, minute)
                    })
                }, 20, 0, false).show()
            }
            Day.GIG -> DescriptionInputDialogFragment.show(childFragmentManager, day.apply { type = itemType }, R.string.new_gig)
            Day.MEETUP -> DescriptionInputDialogFragment.show(childFragmentManager, day.apply { type = itemType }, R.string.new_meetup)
        }
    }

    override fun onDataChanged() = Unit

    override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) = updateEvents()

    override fun onError(e: DatabaseError) = binding.root.showSnackbar(R.string.something_went_wrong)

    override fun onTextEntered(text: String, day: Day) = updateDay(day.apply { description = text })

    private fun updateEvents() {
        if (!isInvalidationScheduled) {
            isInvalidationScheduled = true
            binding.calendarView.postDelayed(invalidateRunnable, 100)
        }

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