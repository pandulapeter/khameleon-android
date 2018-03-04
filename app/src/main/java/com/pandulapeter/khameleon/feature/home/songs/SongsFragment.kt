package com.pandulapeter.khameleon.feature.home.songs

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SongsFragmentBinding
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.data.model.Song
import com.pandulapeter.khameleon.data.repository.ChatRepository
import com.pandulapeter.khameleon.data.repository.SongRepository
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.feature.home.chat.MessageViewModel
import com.pandulapeter.khameleon.util.consume
import com.pandulapeter.khameleon.util.dimension
import com.pandulapeter.khameleon.util.showSnackbar
import org.koin.android.ext.android.inject
import java.util.*

class SongsFragment : KhameleonFragment<SongsFragmentBinding, SongsViewModel>(R.layout.fragment_songs), SongInputDialogFragment.OnSongEnteredListener {

    override val viewModel = SongsViewModel()
    override val title = R.string.songs
    private val songsRepository by inject<SongRepository>()
    private val chatRepository by inject<ChatRepository>()
    private val userRepository by inject<UserRepository>()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val songAdapter = SongAdapter(
        options = FirebaseRecyclerOptions.Builder<Song>()
            .setQuery(songsRepository.songsDarabase.orderByChild("order"), Song::class.java)
            .build(),
        onErrorCallback = { error -> context?.let { binding.root.showSnackbar(it.getString(R.string.something_went_wrong_reason, error)) } },
        onItemClickedCallback = { updateSong(it.apply { isHighlighted = !isHighlighted }) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(context)
        binding.floatingActionButton.setOnClickListener {
            SongInputDialogFragment.show(
                childFragmentManager,
                R.string.new_song,
                R.string.add
            )
        }
        binding.recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = songAdapter
            context?.let { addItemDecoration(SpacesItemDecoration(it.dimension(R.dimen.content_padding))) }
            ItemTouchHelper(object : ElevationItemTouchHelperCallback((context?.dimension(R.dimen.content_padding) ?: 0).toFloat()) {

                override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) =
                    makeMovementFlags(if (adapter.itemCount > 1) ItemTouchHelper.UP or ItemTouchHelper.DOWN else 0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

                override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?) = consume {
                    viewHolder?.adapterPosition?.let { originalPosition ->
                        target?.adapterPosition?.let { targetPosition ->
                            swapSongsInPlaylist(originalPosition, targetPosition)
                        }
                    }
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                    viewHolder?.adapterPosition?.let { position -> deleteSong(songAdapter.getItem(position)) }
                }
            }).attachToRecyclerView(this)
        }
    }

    private fun swapSongsInPlaylist(originalPosition: Int, targetPosition: Int) {
        val items = MutableList(songAdapter.itemCount) { songAdapter.getItem(it) }
        if (originalPosition < targetPosition) {
            for (i in originalPosition until targetPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in originalPosition downTo targetPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        items.forEachIndexed { index, song -> updateSong(song.apply { order = index }) }
    }

    private fun deleteSong(song: Song) {
        songsRepository.songsDarabase
            .orderByChild("id")
            .equalTo(song.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) = binding.root.showSnackbar(R.string.something_went_wrong)

                override fun onDataChange(p0: DataSnapshot?) {
                    p0?.let {
                        if (it.hasChildren()) {
                            it.children.iterator().next().ref.removeValue()
                            sendAutomaticChatMessage(song, false)
                            context?.let { binding.root.showSnackbar(it.getString(R.string.song_deleted, song.title)) { onSongEntered(song, false) } }
                            return
                        }
                    }
                    binding.root.showSnackbar(R.string.something_went_wrong)
                }
            })
    }

    private fun updateSong(song: Song) {
        songsRepository.songsDarabase
            .orderByChild("id")
            .equalTo(song.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) = binding.root.showSnackbar(R.string.something_went_wrong)

                override fun onDataChange(p0: DataSnapshot?) {
                    p0?.let {
                        if (it.hasChildren()) {
                            it.children.iterator().next().ref.setValue(song)
                            return
                        }
                    }
                    binding.root.showSnackbar(R.string.something_went_wrong)
                }
            })
    }

    override fun onStart() {
        super.onStart()
        songAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        songAdapter.stopListening()
    }

    override fun onSongEntered(song: Song, autoOrder: Boolean) {
        songsRepository.songsDarabase.push().setValue(song.apply {
            if (autoOrder) {
                order = if (songAdapter.itemCount == 0) 0 else songAdapter.getItem(songAdapter.itemCount - 1).order + 1
            }
        })
        sendAutomaticChatMessage(song, true)
    }

    private fun sendAutomaticChatMessage(song: Song, added: Boolean) {
        userRepository.getSignedInUser()?.let { user ->
            chatRepository.chatDatabase.push().setValue(
                Message(
                    UUID.randomUUID().toString(),
                    if (added) MessageViewModel.SONG_ADDED else MessageViewModel.SONG_REMOVED,
                    user,
                    false,
                    null,
                    song
                )
            )
        }
    }
}