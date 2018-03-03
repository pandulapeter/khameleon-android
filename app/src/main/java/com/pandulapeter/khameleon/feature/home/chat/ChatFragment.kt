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
import com.pandulapeter.khameleon.data.repository.MessageRepository
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.feature.home.shared.AlertDialogFragment
import com.pandulapeter.khameleon.util.BundleArgumentDelegate
import com.pandulapeter.khameleon.util.showSnackbar
import org.koin.android.ext.android.inject
import java.util.*


class ChatFragment : KhameleonFragment<ChatFragmentBinding, ChatViewModel>(R.layout.fragment_chat),
    MessageInputDialogFragment.OnDialogTextEnteredListener,
    MessageEditBottomSheetFragment.OnDialogItemSelectedListener,
    AlertDialogFragment.OnDialogItemsSelectedListener {

    companion object {
        private const val CHAT = "chat"
        private const val NOTIFICATIONS = "notificationRequests"
        private const val MESSAGE_LIMIT = 300
        private const val MESSAGE_MODIFY_LIMIT = 1000L * 60 * 60 * 6
    }

    override val viewModel = ChatViewModel()
    override val title = R.string.chat
    private var Bundle.messageToDelete by BundleArgumentDelegate.Parcelable<Message>("message_to_delete")
    private var Bundle.messageToEdit by BundleArgumentDelegate.Parcelable<Message>("message_to_edit")
    private var messageToDelete: Message? = null
    private var messageToEdit: Message? = null
    private var isScrolledToBottom = true
    private val userRepository by inject<UserRepository>()
    private val messageRepository by inject<MessageRepository>()
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
        onItemClickedCallback = { message ->
            userRepository.getSignedInUser()?.let { user ->
                if (message.sender?.id == user.id) {
                    if (System.currentTimeMillis() - message.timestamp > MESSAGE_MODIFY_LIMIT) {
                        binding.root.showSnackbar(R.string.message_too_old)
                    } else {
                        MessageEditBottomSheetFragment.show(childFragmentManager, message)
                    }
                } else {
                    binding.root.showSnackbar(R.string.message_modification_error)
                }
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            messageToDelete = it.messageToDelete
            messageToEdit = it.messageToEdit
        }
        binding.floatingActionButton.setOnClickListener {
            messageToEdit = null
            MessageInputDialogFragment.show(childFragmentManager, R.string.new_message)
        }
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        messageToDelete?.let { outState.messageToDelete = it }
        messageToEdit?.let { outState.messageToEdit = it }
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
        val edit = messageToEdit
        if (edit == null) {
            userRepository.getSignedInUser()?.let {
                sendMessage(it, text, isImportant)
                sendNotification(it, text)
                scrollToBottom()
            }
        } else {
            FirebaseDatabase.getInstance()
                .reference
                .child(CHAT)
                .orderByChild("id")
                .equalTo(edit.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) = binding.root.showSnackbar(R.string.something_went_wrong)

                    override fun onDataChange(p0: DataSnapshot?) {
                        p0?.let {
                            if (it.hasChildren()) {
                                it.children.iterator().next().ref.setValue(Message(edit.id, text, edit.sender, isImportant))
                                messageToEdit = null
                                return
                            }
                        }
                        binding.root.showSnackbar(R.string.something_went_wrong)
                    }
                })
        }
    }

    override fun onEditSelected(message: Message) {
        messageToEdit = message
        messageRepository.workInProgressMessageText = message.text
        messageRepository.workInProgressMessageImportant = message.isImportant
        MessageInputDialogFragment.show(childFragmentManager, R.string.edit_message)
    }

    override fun onDeleteSelected(message: Message) {
        messageToDelete = message
        AlertDialogFragment.show(
            childFragmentManager,
            R.string.delete_message_title,
            R.string.delete_message_message,
            R.string.delete,
            R.string.cancel
        )
    }

    override fun onPositiveButtonSelected() {
        messageToDelete?.let { deleteMessage(it) }
        messageToDelete = null
    }

    private fun deleteMessage(message: Message) = FirebaseDatabase.getInstance()
        .reference
        .child(CHAT)
        .orderByChild("id")
        .equalTo(message.id)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = binding.root.showSnackbar(R.string.something_went_wrong)

            override fun onDataChange(p0: DataSnapshot?) {
                p0?.let {
                    if (it.hasChildren()) {
                        it.children.iterator().next().ref.removeValue()
                        messageToDelete = null
                        return
                    }
                }
                binding.root.showSnackbar(R.string.something_went_wrong)
            }
        })

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