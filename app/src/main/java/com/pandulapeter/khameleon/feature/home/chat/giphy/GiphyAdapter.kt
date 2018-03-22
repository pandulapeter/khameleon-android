package com.pandulapeter.khameleon.feature.home.chat.giphy


import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import xyz.klinker.giphy.R

class GiphyAdapter(private val gifs: List<GiphyApiHelper.Gif>, private val callback: GiphyAdapter.Callback) : RecyclerView.Adapter<GiphyAdapter.GifViewHolder>() {

    interface Callback {
        fun onClick(item: GiphyApiHelper.Gif)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        return GifViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_item_gif, parent, false))
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        holder.bind(gifs[position])
    }

    override fun getItemCount(): Int {
        return gifs.size
    }

    inner class GifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gifIv = itemView.findViewById<ImageView>(R.id.gif)
        private val gifPreview = itemView.findViewById<ImageView>(R.id.gifpreview)
        private var previewDownloaded: Boolean = false
        private var gifDownloaded: Boolean = false

        fun bind(gif: GiphyApiHelper.Gif) {
            previewDownloaded = gif.previewDownloaded
            gifDownloaded = gif.gifDownloaded
            gifPreview.visibility = View.VISIBLE

            Glide.with(itemView.context)
                .asGif()
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).centerCrop())
                .load(Uri.parse(gif.previewGif))
                .listener(object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<GifDrawable>, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: GifDrawable, model: Any, target: Target<GifDrawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        gif.gifDownloaded = true
                        gifPreview.visibility = View.GONE
                        return false
                    }
                }).into(gifIv)

            if (!previewDownloaded) {
                Glide.with(itemView.context)
                    .load(Uri.parse(gif.previewImage))
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).centerCrop())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            gif.previewDownloaded = true
                            return false
                        }
                    }).into(gifPreview)
            } else {
                if (!gifDownloaded) {
                    Glide.with(itemView.context).load(Uri.parse(gif.previewImage)).into(gifPreview)
                } else {
                    gifPreview.visibility = View.GONE
                }
            }
            gifIv.setOnClickListener { callback.onClick(gif) }
        }
    }
}