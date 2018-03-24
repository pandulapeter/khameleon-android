package com.pandulapeter.khameleon.feature.home.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pandulapeter.khameleon.ChatFragmentBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.data.model.User
import com.pandulapeter.khameleon.data.repository.ChatRepository
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.feature.home.HomeActivity
import com.pandulapeter.khameleon.feature.home.chat.gifPicker.GifPickerActivity
import com.pandulapeter.khameleon.feature.home.chat.poll.CreatePollActivity
import com.pandulapeter.khameleon.feature.home.shared.AlertDialogFragment
import com.pandulapeter.khameleon.integration.AppShortcutManager
import com.pandulapeter.khameleon.util.BundleArgumentDelegate
import com.pandulapeter.khameleon.util.consume
import com.pandulapeter.khameleon.util.showSnackbar
import org.koin.android.ext.android.inject
import java.util.*


class ChatFragment : KhameleonFragment<ChatFragmentBinding, ChatViewModel>(R.layout.fragment_chat),
    MessageInputDialogFragment.OnDialogTextEnteredListener,
    MessageModifyBottomSheetFragment.OnDialogItemSelectedListener,
    AlertDialogFragment.OnDialogItemsSelectedListener {

    companion object {
        private const val MESSAGE_LIMIT = 300
        //        private const val MESSAGE_MODIFY_LIMIT = 1000L * 60 * 60 * 6
        private const val REQUEST_GIF = 1
        private const val REQUEST_POLL = 2
    }

    override val viewModel = ChatViewModel()
    override val title = R.string.chat
    private var Bundle.messageToDelete by BundleArgumentDelegate.Parcelable<Message>("message_to_delete")
    private var Bundle.messageToEdit by BundleArgumentDelegate.Parcelable<Message>("message_to_edit")
    private var messageToDelete: Message? = null
    private var messageToEdit: Message? = null
    private var isScrolledToBottom = true
    private var myChange = false
    private val userRepository by inject<UserRepository>()
    private val messageRepository by inject<ChatRepository>()
    private val appShortcutManager by inject<AppShortcutManager>()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val messageAdapter = MessageAdapter(
        options = FirebaseRecyclerOptions.Builder<Message>().setQuery(messageRepository.chatDatabase.limitToLast(MESSAGE_LIMIT), Message::class.java).build(),
        onDataChangedCallback = {
            if (!isScrolledToBottom) {
                if (!myChange) {
                    viewModel.newMessagesVisibility.set(true)
                    updateNewMessagesIndicatorVisibility()
                }
                myChange = false
            } else {
                scrollToBottom()
            }
        },
        onErrorCallback = { error -> context?.let { binding.root.showSnackbar(it.getString(R.string.something_went_wrong_reason, error)) } },
        onItemClickedCallback = { message, _ ->
            when {
                message.song != null -> consume { (activity as? HomeActivity)?.openSongsScreen() }
                message.event != null -> consume { (activity as? HomeActivity)?.openCalendarScreen(message.event.timestamp) }
                else -> false
            }
        },
        onItemLongClickedCallback = { message, isImage ->
            userRepository.getSignedInUser()?.let { user ->
                if (message.sender?.id == user.id) {
                    if (message.event != null || message.song != null) {
                        binding.root.showSnackbar(R.string.message_modification_error_automatic)
                    } else {
//                        if (System.currentTimeMillis() - message.timestamp > MESSAGE_MODIFY_LIMIT) {
//                            binding.root.showSnackbar(R.string.message_too_old)
//                        } else {
                        MessageModifyBottomSheetFragment.show(childFragmentManager, message, isImage)
//                        }
                    }
                } else {
                    binding.root.showSnackbar(R.string.message_modification_error)
                }
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appShortcutManager.onChatOpened()
        savedInstanceState?.let {
            messageToDelete = it.messageToDelete
            messageToEdit = it.messageToEdit
        }
        linearLayoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
        binding.floatingActionMenu.setClosedOnTouchOutside(true)
        binding.thumbsUp.setOnClickListener {
            messageToEdit = null
            onTextEntered("\uD83D\uDC4D", false)
            closeFloatingActionMenu()
        }
        binding.createPoll.setOnClickListener {
            startActivityForResult(Intent(context, CreatePollActivity::class.java), REQUEST_POLL)
            closeFloatingActionMenu()
        }
        binding.sendGif.setOnClickListener {
            startActivityForResult(Intent(context, GifPickerActivity::class.java), REQUEST_GIF)
            closeFloatingActionMenu()
        }
        binding.newMessage.setOnClickListener {
            messageToEdit = null
            MessageInputDialogFragment.show(childFragmentManager, R.string.new_message, R.string.send)
            closeFloatingActionMenu()
        }
        binding.recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = messageAdapter
            setHasFixedSize(true)
            context?.let { addItemDecoration(DividerItemDecoration(it)) }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    updateNewMessagesIndicatorVisibility()
                }
            })
        }
        binding.newMessagesIndicator.setOnClickListener { scrollToBottom() }
        val home = activity as? HomeActivity
        home?.defaultMessage?.let {
            home.defaultMessage = null
            messageToEdit = null
            messageRepository.workInProgressMessageText = it
            MessageInputDialogFragment.show(childFragmentManager, R.string.new_message, R.string.send)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_GIF -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.getStringExtra(GifPickerActivity.RESULT_GIF_URL)?.let { gifUrl ->
                        userRepository.getSignedInUser()?.let {
                            sendGif(it, gifUrl)
                            sendNotification(it, "GIF")
                            scrollToBottom()
                        }
                    }
                }
            }
            REQUEST_POLL -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.getParcelableExtra<Message>(CreatePollActivity.RESULT_MESSAGE)?.let { message ->
                        messageRepository.chatDatabase
                            .push()
                            .setValue(message)
                        //TODO: userRepository.getSignedInUser()?.let { user -> sendNotification(user, message.text) }
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
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

    override fun onBackPressed() = if (binding.floatingActionMenu.isOpened) {
        closeFloatingActionMenu()
        true
    } else {
        false
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
            messageRepository.chatDatabase
                .orderByChild("id")
                .equalTo(edit.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) = binding.root.showSnackbar(R.string.something_went_wrong)

                    override fun onDataChange(p0: DataSnapshot?) {
                        p0?.let {
                            if (it.hasChildren()) {
                                myChange = true
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
        MessageInputDialogFragment.show(childFragmentManager, R.string.edit_message, R.string.ok)
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

    private fun deleteMessage(message: Message) = messageRepository.chatDatabase
        .orderByChild("id")
        .equalTo(message.id)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = binding.root.showSnackbar(R.string.something_went_wrong)

            override fun onDataChange(p0: DataSnapshot?) {
                p0?.let {
                    if (it.hasChildren()) {
                        myChange = true
                        it.children.iterator().next().ref.removeValue()
                        messageToDelete = null
                        return
                    }
                }
                binding.root.showSnackbar(R.string.something_went_wrong)
            }
        })

    private fun sendGif(user: User, gifUrl: String) = messageRepository.chatDatabase
        .push()
        .setValue(Message(UUID.randomUUID().toString(), "", user, false, null, null, gifUrl))

    private fun sendMessage(user: User, message: String, isImportant: Boolean) = messageRepository.chatDatabase
        .push()
        .setValue(Message(UUID.randomUUID().toString(), message, user, isImportant))

    private fun sendNotification(user: User, message: String) = messageRepository.notificationsDatabase
        .push()
        .setValue("${user.getFormattedName()}: $message")

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

    private fun closeFloatingActionMenu() {
        binding.floatingActionMenu.close(true)
    }
}