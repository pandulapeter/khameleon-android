package com.pandulapeter.khameleon.data.repository

import com.google.firebase.database.FirebaseDatabase

class CalendarRepository {
    companion object {
        private const val CALENDAR = "calendar"
    }

    private val databaseReference = FirebaseDatabase.getInstance().reference
    val calendarDatabase = databaseReference.child(CALENDAR)!!
}