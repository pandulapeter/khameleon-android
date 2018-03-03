package com.pandulapeter.khameleon.feature.home.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import com.pandulapeter.khameleon.ChatFragmentBinding
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.feature.KhameleonFragment
import com.pandulapeter.khameleon.feature.home.shared.TextInputDialogFragment

class ChatFragment : KhameleonFragment<ChatFragmentBinding, ChatViewModel>(R.layout.fragment_chat), TextInputDialogFragment.OnDialogTextEnteredListener {

    override val viewModel = ChatViewModel()
    override val title = R.string.chat

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            TextInputDialogFragment.show(
                childFragmentManager,
                R.string.new_message,
                R.string.enter_your_message,
                R.string.send,
                R.string.cancel,
                false
            )
        }
    }

    override fun onTextEntered(text: String) {
        Log.d("DEBUG", text)
    }
}