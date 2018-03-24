package com.pandulapeter.khameleon.feature.home.chat.gifPicker

import android.databinding.ObservableBoolean
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancel
import org.json.JSONObject
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext

class GifPickerViewModel {

    companion object {
        private const val API_KEY = "dc6zaTOxFJmzC"
    }

    val loadingIndicatorVisible = ObservableBoolean(true)
    val shouldShowErrorMessage = ObservableBoolean()
    private var coroutine: CoroutineContext? = null

    fun searchForGifs(query: String, callback: (List<String>) -> Unit) {
        loadingIndicatorVisible.set(true)
        coroutine?.cancel()
        coroutine = async(UI) {
            callback(async(CommonPool) {
                request("http://api.giphy.com/v1/gifs/search?q=${URLEncoder.encode(query, "UTF-8")}&limit=-1&api_key=$API_KEY")
            }.await())
            loadingIndicatorVisible.set(false)
        }
    }

    fun getTrendingGifs(callback: (List<String>) -> Unit) {
        loadingIndicatorVisible.set(true)
        coroutine?.cancel()
        coroutine = async(UI) {
            callback(async(CommonPool) {
                request("http://api.giphy.com/v1/gifs/trending?api_key=$API_KEY")
            }.await())
            loadingIndicatorVisible.set(false)
        }
    }

    private fun request(url: String) = mutableListOf<String>().apply {
        try {
            val urlConnection = URL(url).openConnection() as HttpURLConnection
            val inputStream = BufferedInputStream(urlConnection.inputStream)
            val data = JSONObject(Scanner(inputStream).useDelimiter("\\A").next()).getJSONArray("data")
            try {
                inputStream.close()
            } catch (e: Exception) {
            }
            try {
                urlConnection.disconnect()
            } catch (e: Exception) {
            }
            for (i in 0 until data.length()) {
                add(URLDecoder.decode(data.getJSONObject(i).getJSONObject("images").getJSONObject("fixed_width").getString("url"), "UTF-8"))
            }
        } catch (ignored: Exception) {
            shouldShowErrorMessage.set(true)
        }
    }
}