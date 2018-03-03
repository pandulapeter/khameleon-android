package com.pandulapeter.khameleon.feature.home.chat

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pandulapeter.khameleon.ChatFragmentBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.data.model.User
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.util.showSnackbar
import org.koin.android.ext.android.inject
import java.util.*


class ChatFragment : KhameleonFragment<ChatFragmentBinding, ChatViewModel>(R.layout.fragment_chat), MessageInputDialogFragment.OnDialogTextEnteredListener {

    companion object {
        private const val CHAT = "chat"
        private const val NOTIFICATIONS = "notificationRequests"
        private const val MESSAGE_LIMIT = 300
    }

    override val viewModel = ChatViewModel()
    override val title = R.string.chat
    private var isScrolledToBottom = true
    private val userRepository by inject<UserRepository>()
    private val linearLayoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
    private val messageAdapter = MessageAdapter(
        options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(
                FirebaseDatabase.getInstance()
                    .reference
                    .child(CHAT)
                    .limitToLast(MESSAGE_LIMIT), Message::class.java
            )
            .build(),
        onDataChangedCallback = {
            if (!isScrolledToBottom) {
                viewModel.newMessagesVisibility.set(true)
                updateNewMessagesIndicatorVisibility()
            } else {
                scrollToBottom()
            }
        },
        onErrorCallback = { error -> context?.let { binding.root.showSnackbar(it.getString(R.string.something_went_wrong_reason, error)) } },
        onItemClickedCallback = { deleteMessage(it.id) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener { MessageInputDialogFragment.show(childFragmentManager) }
        binding.recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = messageAdapter
            context?.let { addItemDecoration(DividerItemDecoration(it)) }
        }
        binding.newMessagesIndicator.setOnClickListener { scrollToBottom() }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                updateNewMessagesIndicatorVisibility()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        messageAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        messageAdapter.stopListening()
    }

    override fun onTextEntered(text: String, isImportant: Boolean) {
        userRepository.getSignedInUser()?.let {
            sendMessage(it, text, isImportant)
            sendNotification(it, text)
            scrollToBottom()
        }
    }

    private fun sendMessage(user: User, message: String, isImportant: Boolean) = FirebaseDatabase.getInstance()
        .reference
        .child(CHAT)
        .push()
        .setValue(Message(UUID.randomUUID().toString(), message, user, isImportant))

    private fun sendNotification(user: User, message: String) = FirebaseDatabase.getInstance()
        .reference
        .child(NOTIFICATIONS)
        .push()
        .setValue("${user.name}: $message")

    private fun deleteMessage(id: String) = FirebaseDatabase.getInstance()
        .reference
        .child(CHAT)
        .orderByChild("id")
        .equalTo(id)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = Unit

            override fun onDataChange(p0: DataSnapshot?) {
                p0?.let {
                    if (it.hasChildren()) {
                        it.children.iterator().next().ref.removeValue()
                    }
                }
            }
        })

    private fun scrollToBottom() {
        binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
    }

    private fun updateNewMessagesIndicatorVisibility() {
        isScrolledToBottom = if (linearLayoutManager.findFirstVisibleItemPosition() + linearLayoutManager.childCount >= linearLayoutManager.itemCount) {
            viewModel.newMessagesVisibility.set(false)
            true
        } else {
            false
        }
    }
}