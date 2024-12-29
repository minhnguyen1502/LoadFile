package com.example.loadfile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.loadfile.databinding.ItemAudioBinding
import com.example.loadfile.databinding.ItemVideoBinding
import com.example.loadfile.model.MediaItem

class MediaAdapter(private var mediaList: List<MediaItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_AUDIO = 0
        const val TYPE_VIDEO = 1
    }


    override fun getItemViewType(position: Int): Int {
        return if (mediaList[position].isAudio) TYPE_AUDIO else TYPE_VIDEO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {

            TYPE_AUDIO -> {
                val binding = ItemAudioBinding.inflate(layoutInflater, parent, false)
                AudioViewHolder(binding)
            }
            TYPE_VIDEO -> {
                val binding = ItemVideoBinding.inflate(layoutInflater, parent, false)
                VideoViewHolder(binding)
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


    inner class AudioViewHolder(private val binding: ItemAudioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(audioItem: MediaItem) {
            binding.tvName.text = audioItem.title
            binding.tvSize.text = "${audioItem.size / 1024} KB"
            binding.tvDuration.text = "${formatDuration(audioItem.duration)}"
        }
    }

    inner class VideoViewHolder(private val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(videoItem: MediaItem) {
            binding.tvName.text = videoItem.title
            binding.tvDuration.text = "${formatDuration(videoItem.duration)}"
        }
    }
    private fun formatDuration(durationMillis: Long): String {
        val seconds = (durationMillis / 1000) % 60
        val minutes = (durationMillis / (1000 * 60)) % 60
        val hours = (durationMillis / (1000 * 60 * 60)) % 24

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}

