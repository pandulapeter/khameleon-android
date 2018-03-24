package com.pandulapeter.khameleon.data.repository

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.pandulapeter.khameleon.integration.EventJobService
import java.util.concurrent.TimeUnit


class CalendarRepository {
    companion object {
        private const val CALENDAR = "calendar"
        private const val JOB_ID = 1001
    }

    private val databaseReference = FirebaseDatabase.getInstance().reference
    val calendarDatabase = databaseReference.child(CALENDAR)!!

    fun setEventReminderPushNotificationsEnabled(context: Context, isEnabled: Boolean) {
        val jobScheduler = context.applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        if (isEnabled) {
            jobScheduler.cancel(JOB_ID)
            jobScheduler.schedule(
                JobInfo.Builder(JOB_ID, ComponentName(context.applicationContext, EventJobService::class.java))
                    .setPeriodic(TimeUnit.HOURS.toMillis(1))
                    .setPersisted(true)
                    .build()
            )
        } else {
            jobScheduler.cancel(JOB_ID)
        }
    }
}