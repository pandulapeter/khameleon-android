package com.pandulapeter.khameleon.feature.songs

import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SongsFragmentBinding
import com.pandulapeter.khameleon.feature.KhameleonFragment

class SongsFragment : KhameleonFragment<SongsFragmentBinding, SongsViewModel>(R.layout.fragment_songs) {
    override val viewModel = SongsViewModel()
    override val title = R.string.songs
}