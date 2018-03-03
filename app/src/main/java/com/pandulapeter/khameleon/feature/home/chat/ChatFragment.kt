package com.pandulapeter.khameleon.feature.home.chat

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.pandulapeter.khameleon.ChatFragmentBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.feature.home.shared.TextInputDialogFragment
import org.koin.android.ext.android.inject


class ChatFragment : KhameleonFragment<ChatFragmentBinding, ChatViewModel>(R.layout.fragment_chat), TextInputDialogFragment.OnDialogTextEnteredListener {

    companion object {
        private const val CHAT = "chat"
        private const val MESSAGE_LIMIT = 500
    }

    override val viewModel = ChatViewModel()
    override val title = R.string.chat
    private val userRepository by inject<UserRepository>()
    private val adapter = MessageAdapter(
        FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(
                FirebaseDatabase.getInstance()
                    .reference
                    .child(CHAT)
                    .limitToLast(MESSAGE_LIMIT), Message::class.java
            )
            .build()
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            TextInputDialogFragment.show(
                childFragmentManager,
                R.string.new_message,
                R.string.enter_your_message,
                R.string.send,
                R.string.cancel,
                false
            )
        }
        binding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onTextEntered(text: String) {
        userRepository.getSignedInUser()?.let {
            FirebaseDatabase.getInstance()
                .reference
                .child(CHAT)
                .push()
                .setValue(Message(text, it))
        }
    }
}