package com.pandulapeter.khameleon.feature.home.chat.giphy


import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.View
import android.view.Window
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.pandulapeter.khameleon.BuildConfig
import com.pandulapeter.khameleon.util.color
import com.pandulapeter.khameleon.util.hideKeyboard
import xyz.klinker.giphy.Giphy
import xyz.klinker.giphy.R

class GiphyActivity : AppCompatActivity() {

    companion object {
        const val RESULT_GIF_URL = "resultGifUrl"
    }

    private var queried = false
    private var helper: GiphyApiHelper? = null
    private var giphyAdapter = GiphyAdapter(object : GiphyAdapter.Callback {
        override fun onClick(item: GiphyApiHelper.Gif) {
            setResult(Activity.RESULT_OK, Intent().putExtra(RESULT_GIF_URL, item.previewImage))
            supportFinishAfterTransition()
        }
    })
    private var progressSpinner: View? = null
    private var searchView: MaterialSearchView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        @Suppress("ConstantConditionIf")
        setTaskDescription(
            ActivityManager.TaskDescription(
                getString(com.pandulapeter.khameleon.R.string.khameleon) + if (BuildConfig.BUILD_TYPE == "release") "" else " (" + BuildConfig.BUILD_TYPE + ")",
                null, color(com.pandulapeter.khameleon.R.color.primary)
            )
        )
        super.onCreate(savedInstanceState)
        helper = GiphyApiHelper("dc6zaTOxFJmzC", GiphyApiHelper.NO_SIZE_LIMIT, Giphy.PREVIEW_MEDIUM, GiphyApiHelper.NO_SIZE_LIMIT.toLong())
        try {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        } catch (e: Exception) {
        }
        setContentView(R.layout.giffy_search_activity)
        findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@GiphyActivity)
            this.adapter = giphyAdapter
        }
        progressSpinner = findViewById(R.id.list_progress)
        searchView = findViewById(R.id.search_view)
        searchView?.setVoiceSearch(false)
        searchView?.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                executeQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String) = false
        })
        searchView?.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() = Unit

            override fun onSearchViewClosed() {
                if (queried) {
                    queried = false
                    searchView?.setQuery("", false)
                    loadTrending()
                    Handler().postDelayed({ searchView?.showSearch(false) }, 25)
                } else {
                    setResult(Activity.RESULT_CANCELED)
                    supportFinishAfterTransition()
                }
            }
        })
        Handler().postDelayed({ loadTrending() }, 250)
    }

    public override fun onStart() {
        super.onStart()
        searchView?.showSearch(false)
    }

    override fun onBackPressed() {
        if (queried) {
            queried = false
            searchView?.setQuery("", false)
            loadTrending()
        } else {
            setResult(Activity.RESULT_CANCELED)
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        searchView?.setMenuItem(menu.findItem(R.id.action_search))
        return true
    }

    private fun loadTrending() {
        progressSpinner?.visibility = View.VISIBLE
        helper?.trends(object : GiphyApiHelper.Callback {
            override fun onResponse(gifs: List<GiphyApiHelper.Gif>) {
                updateAdapter(gifs)
            }
        })
    }

    private fun executeQuery(query: String) {
        queried = true
        progressSpinner?.visibility = View.VISIBLE
        hideKeyboard(currentFocus)
        helper?.search(query, object : GiphyApiHelper.Callback {
            override fun onResponse(gifs: List<GiphyApiHelper.Gif>) {
                updateAdapter(gifs)
            }
        })
    }

    private fun updateAdapter(gifs: List<GiphyApiHelper.Gif>) {
        progressSpinner?.visibility = View.GONE
        giphyAdapter.setItems(gifs)
    }
}