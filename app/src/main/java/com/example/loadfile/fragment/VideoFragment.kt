package com.example.loadfile.fragment

import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loadfile.R
import com.example.loadfile.adapter.MediaAdapter
import com.example.loadfile.model.MediaItem

class VideoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MediaAdapter
    private var videoList = mutableListOf<MediaItem>()
    private var filteredVideoList = mutableListOf<MediaItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        videoList = fetchVideoFiles()
        filteredVideoList.addAll(videoList)

        adapter = MediaAdapter(filteredVideoList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return view
    }

    private fun fetchVideoFiles(): MutableList<MediaItem> {
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION
        )
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

            while (it.moveToNext()) {
                val title = it.getString(titleColumn)
                val size = it.getLong(sizeColumn)
                val duration = it.getLong(durationColumn)
                videoList.add(MediaItem(title, size, duration, isAudio = false))
            }
        }
        return videoList
    }

    fun filterVideoList(query: String) {
        filteredVideoList.clear()
        if (query.isEmpty()) {
            filteredVideoList.addAll(videoList)
        } else {
            filteredVideoList.addAll(videoList.filter { it.title.contains(query, ignoreCase = true) })
        }
        adapter.notifyDataSetChanged()
    }

}
