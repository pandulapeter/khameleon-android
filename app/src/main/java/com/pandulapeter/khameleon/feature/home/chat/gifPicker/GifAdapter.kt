package com.pandulapeter.khameleon.feature.home.chat.gifPicker


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.pandulapeter.khameleon.R
import com.pandulapeter.khameleon.util.setImage

class GifAdapter(private val onItemClicked: (String) -> Unit) : RecyclerView.Adapter<GifAdapter.GifViewHolder>() {

    private val gifUrls = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GifViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_giphy, parent, false)) {
            onItemClicked(gifUrls[it])
        }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        holder.bind(gifUrls[position])
    }

    override fun getItemCount() = gifUrls.size

    fun setItems(list: List<String>) {
        gifUrls.clear()
        gifUrls.addAll(list)
        notifyDataSetChanged()
    }

    class GifViewHolder(itemView: View, onItemClicked: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val gifPreview = itemView.findViewById<ImageView>(R.id.gif)

        init {
            gifPreview.setOnClickListener { onItemClicked(adapterPosition) }
        }

        fun bind(gifUrl: String) {
            setImage(gifPreview, gifUrl)
        }
    }
}