package com.pandulapeter.khameleon.feature.home.chat.giphy


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.pandulapeter.khameleon.util.setGifUrl
import xyz.klinker.giphy.R

class GiphyAdapter(private val callback: GiphyAdapter.Callback) : RecyclerView.Adapter<GiphyAdapter.GifViewHolder>() {
    private val gifs = mutableListOf<GiphyApiHelper.Gif>()

    interface Callback {
        fun onClick(item: GiphyApiHelper.Gif)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GifViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_item_gif, parent, false))

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        holder.bind(gifs[position])
    }

    override fun getItemCount() = gifs.size

    fun setItems(list: List<GiphyApiHelper.Gif>) {
        gifs.clear()
        gifs.addAll(list)
        notifyDataSetChanged()
    }

    inner class GifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gifIv = itemView.findViewById<ImageView>(R.id.gif)
        private val gifPreview = itemView.findViewById<ImageView>(R.id.gifpreview)

        fun bind(gif: GiphyApiHelper.Gif) {
            gifIv.visibility = View.GONE
            setGifUrl(gifPreview, gif.previewGif)
            gifPreview.setOnClickListener { callback.onClick(gif) }
        }
    }
}