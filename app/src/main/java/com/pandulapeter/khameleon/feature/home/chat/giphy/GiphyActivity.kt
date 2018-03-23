package com.pandulapeter.khameleon.feature.home.chat.giphy


import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.MenuItem
import android.view.View
import com.pandulapeter.khameleon.GiphyActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.shared.KhameleonActivity
import com.pandulapeter.khameleon.util.consume
import com.pandulapeter.khameleon.util.dimension
import com.pandulapeter.khameleon.util.drawable
import com.pandulapeter.khameleon.util.onTextChanged
import xyz.klinker.giphy.Giphy

class GiphyActivity : KhameleonActivity<GiphyActivityBinding>(R.layout.activity_giphy) {

    companion object {
        const val RESULT_GIF_URL = "resultGifUrl"
        private const val QUERY_DELAY = 200L
    }

    private var lastKeyPressTimestamp = 0L
    private var helper: GiphyApiHelper? = null
    private var giphyAdapter = GiphyAdapter(object : GiphyAdapter.Callback {
        override fun onClick(item: GiphyApiHelper.Gif) {
            setResult(Activity.RESULT_OK, Intent().putExtra(RESULT_GIF_URL, item.previewImage))
            supportFinishAfterTransition()
        }
    })
    private var queryRunnable = Runnable {
        if (System.currentTimeMillis() - lastKeyPressTimestamp >= QUERY_DELAY) {
            binding.searchView.text?.toString()?.let {
                if (it.isEmpty()) {
                    loadTrending()
                } else {
                    executeQuery(it)
                }
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper = GiphyApiHelper("dc6zaTOxFJmzC", GiphyApiHelper.NO_SIZE_LIMIT, Giphy.PREVIEW_MEDIUM, GiphyApiHelper.NO_SIZE_LIMIT.toLong())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(drawable(R.drawable.ic_close_24dp))
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = giphyAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                val space = context.dimension(R.dimen.small_content_padding)

                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                    outRect.apply {
                        left = space
                        top = space
                        right = space
                        bottom = space
                    }
                }
            })
        }
        binding.searchView.onTextChanged {
            lastKeyPressTimestamp = System.currentTimeMillis()
            binding.searchView.postDelayed(queryRunnable, QUERY_DELAY)
        }
        loadTrending()
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> consume { supportFinishAfterTransition() }
        else -> super.onOptionsItemSelected(item)
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
        if (!isFinishing && !isDestroyed) {
            binding.loadingIndicator.visibility = View.VISIBLE
            helper?.search(query, object : GiphyApiHelper.Callback {
                override fun onResponse(gifs: List<GiphyApiHelper.Gif>) {
                    updateAdapter(gifs)
                }
            })
        }
    }

    private fun updateAdapter(gifs: List<GiphyApiHelper.Gif>) {
        binding.loadingIndicator.visibility = View.GONE
        giphyAdapter.setItems(gifs)
    }
}