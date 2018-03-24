package com.pandulapeter.khameleon.feature.home.songs

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.SongInputDialogBinding
import com.pandulapeter.khameleon.data.model.Song
import com.pandulapeter.khameleon.util.*
import java.util.*


class SongInputDialogFragment : AppCompatDialogFragment() {

    companion object {
        private var Bundle?.title by BundleArgumentDelegate.Int("title")
        private var Bundle?.doneButton by BundleArgumentDelegate.Int("done_button")
        private var Bundle?.song by BundleArgumentDelegate.Parcelable<Song>("song")

        fun show(fragmentManager: FragmentManager, @StringRes title: Int, @StringRes doneButton: Int, song: Song? = null) {
            SongInputDialogFragment().setArguments {
                it.title = title
                it.doneButton = doneButton
                it.song = song
            }.run { (this as DialogFragment).show(fragmentManager, tag) }
        }
    }

    private lateinit var binding: SongInputDialogBinding
    private val positiveButton by lazy { (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE) }
    private val onSongEnteredListener get() = parentFragment as? OnSongEnteredListener ?: activity as? OnSongEnteredListener

    override fun onCreateDialog(savedInstanceState: Bundle?) = context?.let { context ->
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_song_input, null, false)
        val adapter = ArrayAdapter.createFromResource(context, R.array.keys, R.layout.item_key)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.keyInputField.adapter = adapter
        arguments.song?.let { song ->
            binding.artistInputField.setText(song.artist)
            binding.titleInputField.setText(song.title)
            binding.keyInputField.setSelection(Math.max(0, context.resources.getStringArray(R.array.keys).indexOf(song.key)))
            if (song.bpm != 0) {
                binding.bpmInputField.setText(song.bpm.toString())
            }
            binding.checkboxHighlight.isChecked = song.isHighlighted
            binding.checkboxHighlight.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.checkboxArchive.isChecked = false
                }
            }
            binding.checkboxArchive.isChecked = song.isArchived
            binding.checkboxArchive.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.checkboxHighlight.isChecked = false
                }
            }
        }
        binding.artistInputField.onTextChanged { validateInputs() }
        binding.titleInputField.onTextChanged { validateInputs() }
        AlertDialog.Builder(context, R.style.AlertDialog)
            .setTitle(arguments.title)
            .setView(binding.root)
            .setPositiveButton(arguments.doneButton, { _, _ -> onOkButtonPressed() })
            .setNegativeButton(R.string.cancel, null)
            .create()
    } ?: super.onCreateDialog(savedInstanceState)

    override fun onStart() {
        super.onStart()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding.root.post {
            showKeyboard(binding.titleInputField)
            binding.artistInputField.setSelection(binding.artistInputField.text?.length ?: 0)
            binding.titleInputField.setSelection(binding.titleInputField.text?.length ?: 0)
        }
        validateInputs()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard(activity?.currentFocus)
    }

    private fun onOkButtonPressed() {
        if (areInputsValid()) {
            val isUpdate = arguments?.song != null
            onSongEnteredListener?.onSongEntered(
                Song(
                    arguments?.song?.id ?: UUID.randomUUID().toString(),
                    binding.artistInputField.text.toString(),
                    binding.titleInputField.text.toString(),
                    binding.keyInputField.selectedItem.toString(),
                    arguments?.song?.order ?: 0,
                    binding.bpmInputField.text.toInt(),
                    binding.checkboxHighlight.isChecked,
                    binding.checkboxArchive.isChecked
                ), !isUpdate, isUpdate
            )
            dismiss()
        }
    }

    private fun CharSequence?.toInt() = if (isNullOrEmpty()) 0 else Integer.parseInt(toString())

    private fun validateInputs() {
        positiveButton.isEnabled = areInputsValid()
    }

    private fun areInputsValid() = binding.artistInputField.text.trim().isNotEmpty() && binding.titleInputField.text.trim().isNotEmpty()

    interface OnSongEnteredListener {
        fun onSongEntered(song: Song, autoOrder: Boolean, isUpdate: Boolean)
    }
}