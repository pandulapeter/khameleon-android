package com.pandulapeter.khameleon.feature.home.songs

import android.os.Bundle
import android.view.View
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SongsFragmentBinding
import com.pandulapeter.khameleon.data.model.Song
import com.pandulapeter.khameleon.feature.KhameleonFragment

class SongsFragment : KhameleonFragment<SongsFragmentBinding, SongsViewModel>(R.layout.fragment_songs), SongInputDialogFragment.OnSongEnteredListener {

    override val viewModel = SongsViewModel()
    override val title = R.string.songs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            SongInputDialogFragment.show(
                childFragmentManager,
                R.string.new_song,
                R.string.add
            )
        }
    }

    override fun onSongEntered(song: Song) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}