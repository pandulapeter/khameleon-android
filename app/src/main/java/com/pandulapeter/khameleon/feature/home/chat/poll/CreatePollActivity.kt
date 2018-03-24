package com.pandulapeter.khameleon.feature.home.chat.poll

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.pandulapeter.khameleon.CreatePollActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.shared.KhameleonActivity
import com.pandulapeter.khameleon.util.consume
import com.pandulapeter.khameleon.util.drawable
import com.pandulapeter.khameleon.util.onTextChanged

class CreatePollActivity : KhameleonActivity<CreatePollActivityBinding>(R.layout.activity_create_poll) {

    private var sendMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.new_poll)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(drawable(R.drawable.ic_close_24dp))
        }
        binding.inputField.onTextChanged { updateSendButtonVisibility() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.create_poll, menu)
        sendMenuItem = menu?.findItem(R.id.send)
        updateSendButtonVisibility()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.send -> consume {
            setResult(RESULT_OK)
            supportFinishAfterTransition()
        }
        android.R.id.home -> consume { supportFinishAfterTransition() }
        else -> super.onOptionsItemSelected(item)
    }

    private fun updateSendButtonVisibility() {
        sendMenuItem?.isVisible = binding.inputField.text.isNotEmpty()
    }
}