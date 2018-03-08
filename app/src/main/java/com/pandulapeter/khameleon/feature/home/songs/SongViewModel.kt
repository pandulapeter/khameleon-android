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

    fun onPlayButtonClicked(context: Context) {
        "${song.artist} - ${song.title}".let {
            try {
                context.startActivity(getYouTubeIntent("com.lara.android.youtube", it))
            } catch (_: ActivityNotFoundException) {
                try {
                    context.startActivity(getYouTubeIntent("com.google.android.youtube", it))
                } catch (_: ActivityNotFoundException) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/#q=" + URLEncoder.encode(it, "UTF-8"))).apply { flags = multiWindowFlags })
                }
            }
        }
    }

    private fun getYouTubeIntent(packageName: String, query: String) = Intent(Intent.ACTION_SEARCH).apply {
        `package` = packageName
        flags = multiWindowFlags
    }.putExtra("query", query)
}