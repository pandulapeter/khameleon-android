package com.pandulapeter.khameleon.feature.home.chat

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError
import com.pandulapeter.khameleon.MessageItemBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message

class MessageAdapter(
    options: FirebaseRecyclerOptions<Message>,
    private val onDataChangedCallback: () -> Unit,
    private val onErrorCallback: (String) -> Unit,
    private val onItemClickedCallback: (Message) -> Unit
) : FirebaseRecyclerAdapter<Message, MessageAdapter.MessageViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MessageViewHolder.create(parent) { onItemClickedCallback(getItem(it)) }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
        holder.messageViewModel = MessageViewModel(model, holder.itemView.context)
    }

    override fun onDataChanged() = onDataChangedCallback()

    override fun onError(error: DatabaseError) = onErrorCallback(error.message)

    class MessageViewHolder(private val binding: MessageItemBinding, private val onItemClicked: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        var messageViewModel
            get() = binding.viewModel
            set(value) {
                binding.viewModel = value
            }

        init {
            binding.root.setOnLongClickListener {
                adapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        onItemClicked(it)
                        true
                    } else false
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup, onItemClicked: (Int) -> Unit) =
                MessageViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_message, parent, false), onItemClicked)
        }
    }
}