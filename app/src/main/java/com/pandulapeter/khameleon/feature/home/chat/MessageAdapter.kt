package com.pandulapeter.khameleon.feature.home.chat

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError
import com.pandulapeter.khameleon.ImageItemBinding
import com.pandulapeter.khameleon.MessageItemBinding
import com.pandulapeter.khameleon.PollItemBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.data.model.Message

class MessageAdapter(
    options: FirebaseRecyclerOptions<Message>,
    private val onDataChangedCallback: () -> Unit,
    private val onErrorCallback: (String) -> Unit,
    private val onItemClickedCallback: (Message, Boolean) -> Boolean,
    private val onItemLongClickedCallback: (Message, Boolean) -> Unit
) : FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {

    companion object {
        private const val MESSAGE = 0
        private const val IMAGE = 1
        private const val POLL = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        MESSAGE -> MessageViewHolder.create(parent,
            {
                if (!onItemClickedCallback(getItem(it), false)) {
                    onItemLongClickedCallback(getItem(it), false)
                }
            },
            { onItemLongClickedCallback(getItem(it), false) })
        POLL -> PollViewHolder.create(
            parent,
            {
                if (!onItemClickedCallback(getItem(it), false)) {
                    onItemLongClickedCallback(getItem(it), false)
                }
            },
            { onItemLongClickedCallback(getItem(it), false) })
        else -> ImageViewHolder.create(parent,
            {
                if (!onItemClickedCallback(getItem(it), true)) {
                    onItemLongClickedCallback(getItem(it), true)
                }
            },
            { onItemLongClickedCallback(getItem(it), true) })
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when {
            item.gifUrl.isNullOrBlank() -> if (item.poll?.isNotEmpty() == true) POLL else MESSAGE
            else -> IMAGE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        when (holder) {
            is MessageViewHolder -> holder.messageViewModel = MessageViewModel(model, holder.itemView.context)
            is PollViewHolder -> holder.pollViewModel = PollViewModel(model)
            is ImageViewHolder -> holder.imageViewModel = ImageViewModel(model, holder.itemView.context)
        }
    }

    override fun onDataChanged() = onDataChangedCallback()

    override fun onError(error: DatabaseError) = onErrorCallback(error.message)

    class MessageViewHolder(private val binding: MessageItemBinding, private val onItemClicked: (Int) -> Unit, private val onItemLongClicked: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        var messageViewModel
            get() = binding.viewModel
            set(value) {
                binding.viewModel = value
            }

        init {
            binding.root.setOnClickListener {
                adapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        onItemClicked(it)
                    }
                }
            }
            binding.root.setOnLongClickListener {
                adapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        onItemLongClicked(it)
                        true
                    } else false
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup, onItemClicked: (Int) -> Unit, onItemLongClicked: (Int) -> Unit) = MessageViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_message, parent, false),
                onItemClicked,
                onItemLongClicked
            )
        }
    }

    class ImageViewHolder(private val binding: ImageItemBinding, private val onItemClicked: (Int) -> Unit, private val onItemLongClicked: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        var imageViewModel
            get() = binding.viewModel
            set(value) {
                binding.viewModel = value
            }

        init {
            binding.root.setOnClickListener {
                adapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        onItemClicked(it)
                    }
                }
            }
            binding.root.setOnLongClickListener {
                adapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        onItemLongClicked(it)
                        true
                    } else false
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup, onItemClicked: (Int) -> Unit, onItemLongClicked: (Int) -> Unit) = ImageViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_image, parent, false),
                onItemClicked,
                onItemLongClicked
            )
        }
    }

    class PollViewHolder(private val binding: PollItemBinding, private val onItemClicked: (Int) -> Unit, private val onItemLongClicked: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        var pollViewModel
            get() = binding.viewModel
            set(value) {
                binding.viewModel = value
            }

        init {
            binding.root.setOnClickListener {
                adapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        onItemClicked(it)
                    }
                }
            }
            binding.root.setOnLongClickListener {
                adapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        onItemLongClicked(it)
                        true
                    } else false
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup, onItemClicked: (Int) -> Unit, onItemLongClicked: (Int) -> Unit) = PollViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_poll, parent, false),
                onItemClicked,
                onItemLongClicked
            )
        }
    }
}