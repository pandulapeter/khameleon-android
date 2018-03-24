package com.pandulapeter.khameleon.integration

import android.app.*
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.Build
import android.os.Handler
import android.support.v4.app.NotificationCompat
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.ClassSnapshotParser
import com.firebase.ui.database.FirebaseArray
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Day
import com.pandulapeter.khameleon.data.repository.CalendarRepository
import com.pandulapeter.khameleon.feature.home.HomeActivity
import com.pandulapeter.khameleon.util.color
import com.pandulapeter.khameleon.util.normalize
import org.koin.android.ext.android.inject
import java.util.*


class EventJobService : JobService(), ChangeEventListener {

    companion object {
        private const val PRIMARY_CHANNEL = "default"
    }

    private val calendarRepository by inject<CalendarRepository>()
    private val events = FirebaseArray(calendarRepository.calendarDatabase, ClassSnapshotParser(Day::class.java))
    private var jobParameters: JobParameters? = null
    private var isNotificationScheduled = false
    private val notificationRunnable = Runnable {
        if (!isAppRunning()) {
            val now = Calendar.getInstance().timeInMillis.normalize()
            events.findLast { it.timestamp.normalize() == now }?.let {
                notifyTheUser("Event for today: $it")
            } ?: notifyTheUser("No events for today.")
        }
        isNotificationScheduled = false
        done()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        jobParameters = params
//        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
//        if (hour < 9 || hour > 15) {
//            return false
//        }
        events.addChangeEventListener(this)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        events.removeChangeEventListener(this)
        return true
    }

    override fun onDataChanged() = updateEvents()

    override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) = updateEvents()

    override fun onError(e: DatabaseError) {
        done()
    }

    private fun updateEvents() {
        if (!isNotificationScheduled) {
            isNotificationScheduled = true
            Handler().postDelayed(notificationRunnable, 50)
        }
    }

    private fun notifyTheUser(message: String) {
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
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_icon_song_24dp)
                .setDeleteIntent(PendingIntent.getActivity(this, 0, HomeActivity.getStartIntent(this, AppShortcutManager.CALENDAR_ID), 0))
                .setAutoCancel(true)
                .build()
        )
    }

    private fun done() {
        events.removeChangeEventListener(this)
        jobFinished(jobParameters, true)
    }

    private fun isAppRunning(): Boolean {
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses?.forEach {
            if (it.processName == packageName) {
                return true
            }
        }
        return false
    }
}