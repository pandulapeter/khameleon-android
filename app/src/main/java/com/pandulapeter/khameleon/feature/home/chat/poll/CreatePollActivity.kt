package com.pandulapeter.khameleon.feature.home.chat.poll

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.pandulapeter.khameleon.CreatePollActivityBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.shared.KhameleonActivity
import com.pandulapeter.khameleon.util.consume
import com.pandulapeter.khameleon.util.drawable
import org.koin.android.ext.android.inject
import java.util.*

class CreatePollActivity : KhameleonActivity<CreatePollActivityBinding>(R.layout.activity_create_poll) {

    companion object {
        const val RESULT_MESSAGE = "message"
    }

    private var sendMenuItem: MenuItem? = null
    private val userRepository by inject<UserRepository>()
    private val viewModel = CreatePollViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateSendButtonVisibility = {
            sendMenuItem?.isVisible = it
        }
        binding.viewModel = viewModel
        setTitle(R.string.new_poll)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(drawable(R.drawable.ic_close_24dp))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.create_poll, menu)
        sendMenuItem = menu?.findItem(R.id.send)
        sendMenuItem?.isVisible = viewModel.isValidInput()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.send -> consume {
            userRepository.getSignedInUser()?.let { user ->
                setResult(
                    RESULT_OK, Intent().putExtra(
                        RESULT_MESSAGE, Message(
                            UUID.randomUUID().toString(),
                            viewModel.question.get(),
                            user,
                            false,
                            null,
                            null,
                            null,
                            listOf(
                                Pair(viewModel.option1.get(), listOf()),
                                Pair(viewModel.option2.get(), listOf()),
                                Pair(viewModel.option3.get(), listOf()),
                                Pair(viewModel.option4.get(), listOf()),
                                Pair(viewModel.option5.get(), listOf())
                            )
                        )
                    )
                )
            }
            supportFinishAfterTransition()
        }
        android.R.id.home -> consume { supportFinishAfterTransition() }
        else -> super.onOptionsItemSelected(item)
    }
}