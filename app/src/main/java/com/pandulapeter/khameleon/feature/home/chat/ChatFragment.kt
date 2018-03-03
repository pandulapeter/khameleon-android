package com.pandulapeter.khameleon.feature.home.chat

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.pandulapeter.khameleon.ChatFragmentBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.data.model.User
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.util.showSnackbar
import org.koin.android.ext.android.inject


class ChatFragment : KhameleonFragment<ChatFragmentBinding, ChatViewModel>(R.layout.fragment_chat), MessageInputDialogFragment.OnDialogTextEnteredListener {

    companion object {
        private const val CHAT = "chat"
        private const val NOTIFICATIONS = "notificationRequests"
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
            .build(),
        { viewModel.isNewMessageAlertVisible.set(true) },
        { error -> context?.let { binding.root.showSnackbar(it.getString(R.string.something_went_wrong_reason, error)) } }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener { MessageInputDialogFragment.show(childFragmentManager) }
        binding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
            it.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
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

    override fun onTextEntered(text: String, isImportant: Boolean) {
        userRepository.getSignedInUser()?.let {
            sendMessage(it, text, isImportant)
            sendNotification(it, text)
        }
    }

    private fun sendMessage(user: User, message: String, isImportant: Boolean) = FirebaseDatabase.getInstance()
        .reference
        .child(CHAT)
        .push()
        .setValue(Message(message, user, isImportant))

    private fun sendNotification(user: User, message: String) = FirebaseDatabase.getInstance()
        .reference
        .child(NOTIFICATIONS)
        .push()
        .setValue("${user.name}: $message")
}