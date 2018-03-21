package com.pandulapeter.khameleon.feature.home.songs

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.pandulapeter.khameleon.data.model.Song
import java.net.URLEncoder

class SongViewModel(val song: Song) {

    private val multiWindowFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT
    } else {
        Intent.FLAG_ACTIVITY_NEW_TASK
    }
    private val fullTitle = "${song.artist} - ${song.title}"
    val keyAndBpm = song.key + if (song.bpm == 0) "" else " \n${song.bpm}"

    fun onPlayButtonClicked(context: Context) {
        try {
            context.startActivity(getYouTubeIntent("com.lara.android.youtube", fullTitle))
        } catch (_: ActivityNotFoundException) {
            try {
                context.startActivity(getYouTubeIntent("com.google.android.youtube", fullTitle))
            } catch (_: ActivityNotFoundException) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/#q=" + URLEncoder.encode(fullTitle, "UTF-8"))).apply { flags = multiWindowFlags })
            }
        }
    }

    fun onLyricsButtonClicked(context: Context) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/#q=" + URLEncoder.encode("$fullTitle lyrics", "UTF-8"))).apply {
            flags = multiWindowFlags
        })
    }

    private fun getYouTubeIntent(packageName: String, query: String) = Intent(Intent.ACTION_SEARCH).apply {
        `package` = packageName
        flags = multiWindowFlags
    }.putExtra("query", query)
}