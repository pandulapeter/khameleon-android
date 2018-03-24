package com.pandulapeter.khameleon.integration

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.ClassSnapshotParser
import com.firebase.ui.database.FirebaseArray
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Day
import com.pandulapeter.khameleon.data.repository.CalendarRepository
import com.pandulapeter.khameleon.util.color
import org.koin.android.ext.android.inject

class EventJobService : JobService(), ChangeEventListener {

    companion object {
        private const val PRIMARY_CHANNEL = "default"
    }

    private val calendarRepository by inject<CalendarRepository>()
    private val events = FirebaseArray(calendarRepository.calendarDatabase, ClassSnapshotParser(Day::class.java))
    private var jobParameters: JobParameters? = null
    private var isNotificationScheduled = false
    private val notificationRunnable = Runnable {
        //TODO: Notify user
        Log.d("NOTDEBUG", "Notifying user")
        notifyTheUser()
        jobFinished(jobParameters, true)
        isNotificationScheduled = false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("NOTDEBUG", "Service started")
        jobParameters = params
        events.addChangeEventListener(this)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("NOTDEBUG", "Service ended")
        events.removeChangeEventListener(this)
        return true
    }

    override fun onDataChanged() = updateEvents()

    override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) = updateEvents()

    override fun onError(e: DatabaseError) {
        Log.d("NOTDEBUG", "Database error $e")
        jobFinished(jobParameters, true)
    }

    private fun updateEvents() {
        if (!isNotificationScheduled) {
            Log.d("NOTDEBUG", "Database updated")
            isNotificationScheduled = true
            Handler().postDelayed(notificationRunnable, 50)
        }
    }

    private fun notifyTheUser() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(NotificationChannel(PRIMARY_CHANNEL, getString(R.string.khameleon), NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = color(R.color.primary)
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            })
        }
        manager.notify(
            0,
            NotificationCompat.Builder(applicationContext, PRIMARY_CHANNEL)
                .setContentTitle(getString(R.string.khameleon))
                .setContentText("Job dispatched")
                .setAutoCancel(true)
                .build()
        )
    }
}