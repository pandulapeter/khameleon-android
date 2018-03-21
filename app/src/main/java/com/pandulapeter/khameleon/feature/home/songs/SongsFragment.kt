package com.pandulapeter.khameleon.feature.home.songs

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
        options = FirebaseRecyclerOptions.Builder<Song>().setQuery(songsRepository.songsDarabase.orderByChild("order"), Song::class.java).build(),
        onErrorCallback = { error -> context?.let { binding.root.showSnackbar(it.getString(R.string.something_went_wrong_reason, error)) } },
        onItemClickedCallback = { SongInputDialogFragment.show(childFragmentManager, R.string.edit_song, R.string.edit, it) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(context)
        binding.floatingActionButton.setOnClickListener { SongInputDialogFragment.show(childFragmentManager, R.string.new_song, R.string.add) }
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.songs, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.export -> consume {
            var text = ""
            (0 until songAdapter.itemCount).forEach {
                songAdapter.getItem(it).run {
                    text = "$text\n$artist - $title (${if (key.isEmpty()) "?" else key}" + if (bpm == 0) ")" else ", $bpm)"
                }
            }
            startActivity(Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
            }.putExtra(Intent.EXTRA_TEXT, text), getString(R.string.export_list)))
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        songAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        songAdapter.stopListening()
    }

    override fun onSongEntered(song: Song, autoOrder: Boolean, isUpdate: Boolean) {
        if (isUpdate) {
            updateSong(song)
        } else {
            songsRepository.songsDarabase.push().setValue(song.apply {
                if (autoOrder) {
                    order = -1//if (songAdapter.itemCount == 0) 0 else songAdapter.getItem(songAdapter.itemCount - 1).order + 1
                }
            })
            sendAutomaticChatMessage(song, true)
        }
        binding.recyclerView.smoothScrollToPosition(Math.max(0, song.order))
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
                            context?.let { binding.root.showSnackbar(it.getString(R.string.song_deleted, song.title)) { onSongEntered(song, false, false) } }
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