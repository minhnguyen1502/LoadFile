package com.example.loadfile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.loadfile.databinding.ItemAudioBinding
import com.example.loadfile.databinding.ItemVideoBinding
import com.example.loadfile.model.MediaItem

class MediaAdapter(
    private val audioList: List<MediaItem>,
    private val videoList: List<MediaItem>,
    private val onItemLongClick: (MediaItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val AUDIO_TYPE = 0
        private const val VIDEO_TYPE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < audioList.size) AUDIO_TYPE else VIDEO_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == AUDIO_TYPE) {
            val binding = ItemAudioBinding.inflate(layoutInflater, parent, false)
            AudioViewHolder(binding)
        } else {
            val binding = ItemVideoBinding.inflate(layoutInflater, parent, false)
            VideoViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AudioViewHolder) {
            holder.bind(audioList[position])
        } else if (holder is VideoViewHolder) {
            holder.bind(videoList[position - audioList.size])
        }
    }

    override fun getItemCount(): Int {
        return audioList.size + videoList.size
    }

    inner class AudioViewHolder(private val binding: ItemAudioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(audioItem: MediaItem) {
            binding.tvName.text = audioItem.title
            binding.tvSize.text = "${audioItem.size / 1024} KB"
            binding.tvDuration.text = "${formatDuration(audioItem.duration)}"
            binding.root.setOnLongClickListener {
                onItemLongClick(audioItem)
                true
            }
        }
    }

    inner class VideoViewHolder(private val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(videoItem: MediaItem) {
            binding.tvName.text = videoItem.title
            binding.tvDuration.text = "${formatDuration(videoItem.duration)}"
            binding.root.setOnLongClickListener {
                onItemLongClick(videoItem)
                true
            }
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

