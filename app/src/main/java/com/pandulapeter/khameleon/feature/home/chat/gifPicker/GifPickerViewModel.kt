package com.pandulapeter.khameleon.feature.home.chat.gifPicker

import android.databinding.ObservableBoolean
import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

class GifPickerViewModel {

    companion object {
        private const val API_KEY = "dc6zaTOxFJmzC"
    }

    val loadingIndicatorVisible = ObservableBoolean(true)

    fun searchForGifs(query: String, callback: (List<String>) -> Unit) {
        loadingIndicatorVisible.set(true)
        SearchGiffy(API_KEY, query) {
            loadingIndicatorVisible.set(false)
            callback(it)
        }.execute()
    }

    fun getTrendingGifs(callback: (List<String>) -> Unit) {
        loadingIndicatorVisible.set(true)
        GiffyTrends(API_KEY) {
            loadingIndicatorVisible.set(false)
            callback(it)
        }.execute()
    }

    private class GiffyTrends(apiKey: String, callback: (List<String>) -> Unit) : SearchGiffy(apiKey, null, callback) {

        override fun buildSearchUrl(query: String?) = "http://api.giphy.com/v1/gifs/trending?api_key=$apiKey"
    }

    private open class SearchGiffy(
        val apiKey: String,
        private val query: String?,
        private val callback: (List<String>) -> Unit
    ) : AsyncTask<Void, Void, List<String>>() {

        override fun doInBackground(vararg arg0: Void): List<String> {
            val gifList = ArrayList<String>()
            try {
                val urlToRequest = URL(buildSearchUrl(query))
                val urlConnection = urlToRequest.openConnection() as HttpURLConnection
                val `in` = BufferedInputStream(urlConnection.inputStream)
                val root = JSONObject(getResponseText(`in`))
                val data = root.getJSONArray("data")
                try {
                    `in`.close()
                } catch (e: Exception) {
                }
                try {
                    urlConnection.disconnect()
                } catch (e: Exception) {
                }
                for (i in 0 until data.length()) {
                    val gif = data.getJSONObject(i)
                    val name = gif.getString("slug")
                    Log.d("GIF Name", name)
                    val images = gif.getJSONObject("images")
                    val previewGif = images.getJSONObject("fixed_width")
                    gifList.add(URLDecoder.decode(previewGif.getString("url"), "UTF-8"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return gifList
        }

        override fun onPostExecute(result: List<String>) {
            callback(result)
        }

        protected open fun buildSearchUrl(query: String?) = "http://api.giphy.com/v1/gifs/search?q=${URLEncoder.encode(query, "UTF-8")}&limit=-1&api_key=$apiKey"

        private fun getResponseText(inStream: InputStream) = Scanner(inStream).useDelimiter("\\A").next()
    }
}