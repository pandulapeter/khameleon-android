package com.pandulapeter.khameleon.feature.home.chat

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.pandulapeter.khameleon.MessageItemBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message

class MessageAdapter(options: FirebaseRecyclerOptions<Message>) : FirebaseRecyclerAdapter<Message, MessageAdapter.MessageViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MessageViewHolder.create(parent)

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
        holder.message = model
    }

    class MessageViewHolder(private val binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root) {

        var message
            get() = binding.model
            set(value) {
                binding.model = value
            }

        companion object {
            fun create(parent: ViewGroup) = MessageViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_message, parent, false))
        }
    }
}