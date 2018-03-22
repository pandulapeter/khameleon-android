package com.pandulapeter.khameleon.feature.home.songs

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SongItemBinding
import com.pandulapeter.khameleon.data.model.Song
import java.util.*

class SongAdapter(
    options: FirebaseRecyclerOptions<Song>,
    private val onErrorCallback: (String) -> Unit,
    private val onItemClickedCallback: (Song) -> Unit,
    private var onItemTouchedCallback: ((position: Int) -> Unit),
    private var updateSong: (Song) -> Unit
) : FirebaseRecyclerAdapter<Song, SongAdapter.SongViewHolder>(options) {
    private var localListCopy: MutableList<Song>? = null
    var allowNotifyEvents = true
    var isInEditMode = false
        set(value) {
            if (field != value) {
                field = value
                notifyItemRangeChanged(0, itemCount)
                if (!value) {
                    localListCopy?.forEachIndexed { index, song -> updateSong(song.apply { order = index }) }
                    localListCopy = null
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SongViewHolder.create(
        parent,
        { onItemClickedCallback(getItem(it)) },
        { onItemTouchedCallback(it) })

    override fun onBindViewHolder(holder: SongViewHolder, position: Int, model: Song) {
        holder.model = SongViewModel(model)
        holder.isInEditMode = isInEditMode
    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) {
        if (allowNotifyEvents) {
            super.onChildChanged(type, snapshot, newIndex, oldIndex)
        }
    }

    override fun onDataChanged() = Unit

    override fun onError(error: DatabaseError) = onErrorCallback(error.message)

    fun swapSongsInPlaylist(originalPosition: Int, targetPosition: Int) {
        if (localListCopy == null) {
            localListCopy = MutableList(itemCount) { getItem(it) }
        }
        if (originalPosition < targetPosition) {
            for (i in originalPosition until targetPosition) {
                Collections.swap(localListCopy, i, i + 1)
            }
        } else {
            for (i in originalPosition downTo targetPosition + 1) {
                Collections.swap(localListCopy, i, i - 1)
            }
        }
        notifyItemMoved(originalPosition, targetPosition)
    }

    class SongViewHolder(
        private val binding: SongItemBinding,
        private val onItemClicked: (Int) -> Unit,
        private val onItemTouched: ((position: Int) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {

        var isInEditMode = false
            set(value) {
                field = value
                val visibility = if (value) View.GONE else View.VISIBLE
                binding.play.visibility = visibility
                binding.lyrics.visibility = visibility
                binding.key.visibility = visibility
                binding.dragHandle.visibility = if (value) View.VISIBLE else View.GONE
            }
        var model
            get() = binding.viewModel
            set(value) {
                binding.viewModel = value
            }

        init {
            binding.root.setOnClickListener {
                adapterPosition.let {
                    if (it != RecyclerView.NO_POSITION && !isInEditMode) {
                        onItemClicked(it)
                    }
                }
            }
            binding.dragHandle.setOnTouchListener { _, event ->
                if (isInEditMode && event.actionMasked == MotionEvent.ACTION_DOWN && adapterPosition != RecyclerView.NO_POSITION) {
                    onItemTouched(adapterPosition)
                }
                false
            }
        }

        companion object {
            fun create(parent: ViewGroup, onItemClicked: (Int) -> Unit, onItemTouched: ((position: Int) -> Unit)) =
                SongViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_song, parent, false), onItemClicked, onItemTouched)
        }
    }
}