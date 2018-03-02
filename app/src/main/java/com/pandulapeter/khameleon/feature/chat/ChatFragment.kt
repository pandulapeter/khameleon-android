package com.pandulapeter.khameleon.feature.chat

import com.pandulapeter.khameleon.ChatFragmentBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.KhameleonFragment

class ChatFragment : KhameleonFragment<ChatFragmentBinding, ChatViewModel>(R.layout.fragment_chat) {
    override val viewModel = ChatViewModel()
    override val title = R.string.chat
}