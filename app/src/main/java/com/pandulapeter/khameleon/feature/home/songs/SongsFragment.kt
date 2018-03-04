package com.pandulapeter.khameleon.feature.home.songs

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SongsFragmentBinding
import com.pandulapeter.khameleon.data.model.Message
import com.pandulapeter.khameleon.data.model.Song
import com.pandulapeter.khameleon.data.repository.ChatRepository
import com.pandulapeter.khameleon.data.repository.SongRepository
import com.pandulapeter.khameleon.data.repository.UserRepository
import com.pandulapeter.khameleon.feature.KhameleonFragment
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
            .setQuery(songsRepository.songsDarabase, Song::class.java)
            .build(),
        onErrorCallback = { error -> context?.let { binding.root.showSnackbar(it.getString(R.string.something_went_wrong_reason, error)) } },
        onItemClickedCallback = { song -> binding.root.showSnackbar("${song.artist} - ${song.title}") }
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
        }
    }

    override fun onStart() {
        super.onStart()
        songAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        songAdapter.stopListening()
    }

    override fun onSongEntered(song: Song) {
        songsRepository.songsDarabase.push().setValue(song)
        sendAutomaticChatMessage(song)
    }


    private fun sendAutomaticChatMessage(song: Song) {
        userRepository.getSignedInUser()?.let { user ->
            chatRepository.chatDatabase.push().setValue(Message(UUID.randomUUID().toString(), "", user, false, null, song))
        }
    }
}