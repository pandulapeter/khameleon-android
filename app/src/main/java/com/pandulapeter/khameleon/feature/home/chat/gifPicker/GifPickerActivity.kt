package com.pandulapeter.khameleon.feature.home.chat.gifPicker


import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.MenuItem
import android.view.View
import com.pandulapeter.khameleon.GifPickerActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.shared.KhameleonActivity
import com.pandulapeter.khameleon.util.consume
import com.pandulapeter.khameleon.util.dimension
import com.pandulapeter.khameleon.util.drawable
import com.pandulapeter.khameleon.util.onTextChanged

class GifPickerActivity : KhameleonActivity<GifPickerActivityBinding>(R.layout.activity_gif_picker) {

    companion object {
        const val RESULT_GIF_URL = "resultGifUrl"
        private const val QUERY_DELAY = 400L
    }

    private var viewModel = GifPickerViewModel()
    private var lastKeyPressTimestamp = 0L
    private var adapter = GifAdapter {
        setResult(Activity.RESULT_OK, Intent().putExtra(RESULT_GIF_URL, it))
        supportFinishAfterTransition()
    }
    private var queryRunnable = Runnable {
        if (System.currentTimeMillis() - lastKeyPressTimestamp >= QUERY_DELAY) {
            binding.searchInput.text?.toString()?.let {
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
        binding.viewModel = viewModel
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(drawable(R.drawable.ic_close_24dp))
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = adapter
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
        binding.searchInput.onTextChanged {
            lastKeyPressTimestamp = System.currentTimeMillis()
            binding.searchInput.postDelayed(queryRunnable, QUERY_DELAY)
        }
        loadTrending()
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> consume { supportFinishAfterTransition() }
        else -> super.onOptionsItemSelected(item)
    }

    private fun loadTrending() = viewModel.getTrendingGifs { adapter.setItems(it) }

    private fun executeQuery(query: String) {
        if (!isFinishing && !isDestroyed) {
            viewModel.searchForGifs(query) { adapter.setItems(it) }
        }
    }
}