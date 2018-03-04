package com.pandulapeter.khameleon.data.repository

import com.google.firebase.database.FirebaseDatabase

class SongRepository {
    companion object {
        private const val SONGS = "songs"
    }

    private val databaseReference = FirebaseDatabase.getInstance().reference
    val songsDarabase = databaseReference.child(SONGS)!!
}