package com.pandulapeter.khameleon.feature.home.songs

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SongItemBinding
import com.pandulapeter.khameleon.data.model.Song

class SongAdapter(
    options: FirebaseRecyclerOptions<Song>,
    private val onErrorCallback: (String) -> Unit,
    private val onItemClickedCallback: (Song) -> Unit
) : FirebaseRecyclerAdapter<Song, SongAdapter.SongViewHolder>(options) {
    var allowNotifyEvents = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SongViewHolder.create(parent) { onItemClickedCallback(getItem(it)) }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int, model: Song) {
        holder.model = model
    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) {
        if (allowNotifyEvents) {
            super.onChildChanged(type, snapshot, newIndex, oldIndex)
        }
    }

    override fun onDataChanged() = Unit

    override fun onError(error: DatabaseError) = onErrorCallback(error.message)

    class SongViewHolder(
        private val binding: SongItemBinding,
        private val onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        var model
            get() = binding.model
            set(value) {
                binding.model = value
            }

        init {
            binding.root.setOnClickListener {
                adapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        onItemClicked(it)
                    }
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup, onItemClicked: (Int) -> Unit) =
                SongViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_song, parent, false), onItemClicked)
        }
    }
}