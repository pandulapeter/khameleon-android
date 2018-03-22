package com.pandulapeter.khameleon.feature.home.chat.giphy

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

class GiphyApiHelper(private val apiKey: String, private val limit: Int, private val previewSize: Int, private val maxSize: Long) {

    companion object {
        const val NO_SIZE_LIMIT = -1
        private val PREVIEW_SIZE = arrayOf("fixed_width_downsampled", "fixed_width", "downsized")
        private val SIZE_OPTIONS = arrayOf("original", "downsized_large", "downsized_medium", "downsized", "fixed_height", "fixed_width", "fixed_height_small", "fixed_width_small")
    }

    interface Callback {
        fun onResponse(gifs: List<Gif>)
    }

    fun search(query: String, callback: Callback) {
        SearchGiffy(apiKey, limit, previewSize, maxSize, query, callback).execute()
    }

    fun trends(callback: Callback) {
        GiffyTrends(apiKey, previewSize, maxSize, callback).execute()
    }

    private class GiffyTrends(apiKey: String, previewSize: Int, maxSize: Long, callback: Callback) :
        SearchGiffy(apiKey, -1, previewSize, maxSize, null, callback) {

        @Throws(UnsupportedEncodingException::class)
        override fun buildSearchUrl(query: String?) = "http://api.giphy.com/v1/gifs/trending?api_key=$apiKey"
    }

    private open class SearchGiffy(
        internal val apiKey: String,
        private val limit: Int,
        private val previewSize: Int,
        private val maxSize: Long,
        private val query: String?,
        private val callback: Callback?
    ) : AsyncTask<Void, Void, List<Gif>>() {

        override fun doInBackground(vararg arg0: Void): List<Gif> {
            val gifList = ArrayList<Gif>()

            try {
                // create the connection
                val urlToRequest = URL(buildSearchUrl(query))
                val urlConnection = urlToRequest.openConnection() as HttpURLConnection

                // create JSON object from content
                val `in` = BufferedInputStream(
                    urlConnection.inputStream
                )
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
                    val previewImage = images.getJSONObject("downsized_still")
                    val previewGif = images.getJSONObject(PREVIEW_SIZE[previewSize])
                    val originalSize = images.getJSONObject("original")
                    var downsized: JSONObject? = null

                    // Return the highest quality GIF under MaxSizeLimit.
                    for (size in SIZE_OPTIONS) {
                        downsized = images.getJSONObject(size)
                        Log.v("giphy", size + ": " + downsized!!.getString("size") + " bytes")

                        if (java.lang.Long.parseLong(downsized.getString("size")) < maxSize || maxSize == NO_SIZE_LIMIT.toLong()) {
                            break
                        } else {
                            downsized = null
                        }
                    }

                    if (downsized != null) {
                        gifList.add(
                            Gif(
                                name,
                                previewImage.getString("url"),
                                previewGif.getString("url"),
                                downsized.getString("url"),
                                originalSize.getString("mp4")
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return gifList
        }

        override fun onPostExecute(result: List<Gif>) {
            callback?.onResponse(result)
        }

        @Throws(UnsupportedEncodingException::class)
        protected open fun buildSearchUrl(query: String?): String {
            return "http://api.giphy.com/v1/gifs/search?q=" + URLEncoder.encode(query, "UTF-8") + "&limit=" + limit + "&api_key=" + apiKey
        }

        private fun getResponseText(inStream: InputStream): String {
            return Scanner(inStream).useDelimiter("\\A").next()
        }
    }

    class Gif(name: String, previewImage: String, previewGif: String, gifUrl: String, mp4Url: String) {
        var name = URLDecoder.decode(name, "UTF-8")
        var previewImage = URLDecoder.decode(previewImage, "UTF-8")
        var previewGif = URLDecoder.decode(previewGif, "UTF-8")
        var gifUrl = URLDecoder.decode(gifUrl, "UTF-8")
        var mp4Url = URLDecoder.decode(mp4Url, "UTF-8")
        var previewDownloaded = false
        var gifDownloaded = false
    }
}