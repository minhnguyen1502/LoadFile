package com.example.loadfile.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loadfile.R
import com.example.loadfile.model.MediaItem

class MediaAdapter(private val mediaList: List<MediaItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_AUDIO = 0
        const val TYPE_VIDEO = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (mediaList[position].isAudio) TYPE_AUDIO else TYPE_VIDEO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_AUDIO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_audio, parent, false)
                AudioViewHolder(view)
            }
            TYPE_VIDEO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_video, parent, false)
                VideoViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mediaItem = mediaList[position]
        when (holder) {
            is AudioViewHolder -> holder.bind(mediaItem)
            is VideoViewHolder -> holder.bind(mediaItem)
        }
    }

    override fun getItemCount(): Int = mediaList.size

    class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivMedia: ImageView = itemView.findViewById(R.id.iv_media)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvSize: TextView = itemView.findViewById(R.id.tvSize)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)

        fun bind(mediaItem: MediaItem) {
            ivMedia.setImageResource(R.drawable.audio_file)
            tvName.text = mediaItem.title
            tvSize.text = "${mediaItem.size / (1024 * 1024)} MB"
            tvDuration.text = "${mediaItem.duration / 1000}s"
        }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)

        fun bind(mediaItem: MediaItem) {
            tvName.text = mediaItem.title
            tvDuration.text = "${mediaItem.duration / 1000}s"
        }
    }
}
