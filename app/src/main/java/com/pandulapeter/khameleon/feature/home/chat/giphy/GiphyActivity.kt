package com.pandulapeter.khameleon.feature.home.chat.giphy


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.View
import android.view.Window
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.pandulapeter.khameleon.GiphyActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.shared.KhameleonActivity
import com.pandulapeter.khameleon.util.hideKeyboard
import xyz.klinker.giphy.Giphy

class GiphyActivity : KhameleonActivity<GiphyActivityBinding>(R.layout.activity_giphy) {

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

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper = GiphyApiHelper("dc6zaTOxFJmzC", GiphyApiHelper.NO_SIZE_LIMIT, Giphy.PREVIEW_MEDIUM, GiphyApiHelper.NO_SIZE_LIMIT.toLong())
        try {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        } catch (e: Exception) {
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@GiphyActivity)
            adapter = giphyAdapter
        }
        binding.searchView.setVoiceSearch(false)
        binding.searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                executeQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String) = false
        })
        binding.searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() = Unit

            override fun onSearchViewClosed() {
                if (queried) {
                    queried = false
                    binding.searchView.setQuery("", false)
                    loadTrending()
                    Handler().postDelayed({ binding.searchView.showSearch(false) }, 25)
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
        binding.searchView.showSearch(false)
    }

    override fun onBackPressed() {
        if (queried) {
            queried = false
            binding.searchView.setQuery("", false)
            loadTrending()
        } else {
            setResult(Activity.RESULT_CANCELED)
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        binding.searchView.setMenuItem(menu.findItem(R.id.action_search))
        return true
    }

    private fun loadTrending() {
        binding.loadingIndicator.visibility = View.VISIBLE
        helper?.trends(object : GiphyApiHelper.Callback {
            override fun onResponse(gifs: List<GiphyApiHelper.Gif>) {
                updateAdapter(gifs)
            }
        })
    }

    private fun executeQuery(query: String) {
        queried = true
        binding.loadingIndicator.visibility = View.VISIBLE
        hideKeyboard(currentFocus)
        helper?.search(query, object : GiphyApiHelper.Callback {
            override fun onResponse(gifs: List<GiphyApiHelper.Gif>) {
                updateAdapter(gifs)
            }
        })
    }

    private fun updateAdapter(gifs: List<GiphyApiHelper.Gif>) {
        binding.loadingIndicator.visibility = View.GONE
        giphyAdapter.setItems(gifs)
    }
}