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
import com.example.loadfile.model.MediaItem

class AudioFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MediaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_audio, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        val audioList = fetchAudioFiles()
        adapter = MediaAdapter(audioList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        return view
    }

    private fun fetchAudioFiles(): List<MediaItem> {
        val audioList = mutableListOf<MediaItem>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION
        )
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val title = it.getString(titleColumn)
                val size = it.getLong(sizeColumn)
                val duration = it.getLong(durationColumn)
                audioList.add(MediaItem(title, size, duration, isAudio = true))
            }
        }
        return audioList
    }
}

