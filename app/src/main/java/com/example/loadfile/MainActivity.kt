package com.example.loadfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loadfile.adapter.MediaAdapter
import com.example.loadfile.databinding.ActivityMainBinding
import com.example.loadfile.databinding.DialogConfirmDeleteBinding
import com.example.loadfile.databinding.DialogGetMediaBinding
import com.example.loadfile.databinding.DialogOptionBinding
import com.example.loadfile.databinding.DialogRenameBinding
import com.example.loadfile.fragment.ViewPagerAdapter
import com.example.loadfile.model.MediaItem
import com.example.orderfood.base.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("NotifyDataSetChanged")
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val REQUEST_CODE_STORAGE = 1
    private val audioList = mutableListOf<MediaItem>()
    private val videoList = mutableListOf<MediaItem>()

    override fun initView() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_STORAGE
            )
        } else {
            loadMediaFiles()
        }

        binding.recycleView.layoutManager = LinearLayoutManager(this)
        val mediaAdapter = MediaAdapter(audioList, videoList) { mediaItem ->
            showDialogOption(mediaItem)
        }
        binding.recycleView.adapter = mediaAdapter
    }

    override fun bindView() {
        binding.add.setOnClickListener { showDialogGetMedia() }

    }

    private fun loadMediaFiles() {
        val contentResolver: ContentResolver = contentResolver

        val audioUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val audioProjection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Video.Media.DATE_MODIFIED

        )
        val audioCursor: Cursor? =
            contentResolver.query(audioUri, audioProjection, null, null, null)
        audioCursor?.use {
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val sizeColumn = it.getColumnIndex(MediaStore.Audio.Media.SIZE)
            val durationColumn = it.getColumnIndex(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val audioTitle = it.getString(titleColumn)
                val audioSize = it.getLong(sizeColumn)
                val audioDuration = it.getLong(durationColumn)

                audioList.add(MediaItem(audioTitle, audioSize, audioDuration, true))
            }
        }

        val videoUri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val videoProjection = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_MODIFIED
        )
        val videoCursor: Cursor? =
            contentResolver.query(videoUri, videoProjection, null, null, null)
        videoCursor?.use {
            val titleColumn = it.getColumnIndex(MediaStore.Video.Media.TITLE)
            val sizeColumn = it.getColumnIndex(MediaStore.Video.Media.SIZE)
            val durationColumn = it.getColumnIndex(MediaStore.Video.Media.DURATION)

            while (it.moveToNext()) {
                val videoTitle = it.getString(titleColumn)
                val videoSize = it.getLong(sizeColumn)
                val videoDuration = it.getLong(durationColumn)

                videoList.add(MediaItem(videoTitle, videoSize, videoDuration, false))
            }
        }

        binding.recycleView.adapter?.notifyDataSetChanged()
    }

    private fun showDialogOption(mediaItem: MediaItem) {
        val dialogBinding = DialogOptionBinding.inflate(layoutInflater)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        dialogBinding.tvRename.setOnClickListener {
            showDialogRename(mediaItem)
            dialog.dismiss()
        }
        dialogBinding.tvShare.setOnClickListener {
            shareFile(mediaItem)
            dialog.dismiss()
        }
        dialogBinding.tvDelete.setOnClickListener {
            showDialogConfirmDelete(mediaItem)
            dialog.dismiss()
        }
        dialogBinding.tvCancel.setOnClickListener { dialog.dismiss() }
    }

    private fun removeFile(mediaItem: MediaItem) {
        if (mediaItem.isAudio) {
            audioList.remove(mediaItem)
        } else {
            videoList.remove(mediaItem)
        }

        binding.recycleView.adapter?.notifyDataSetChanged()
        Toast.makeText(this, "File removed", Toast.LENGTH_SHORT).show()
    }

    private fun showDialogConfirmDelete(mediaItem: MediaItem){
        val dialogBinding = DialogConfirmDeleteBinding.inflate(layoutInflater)

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        dialogBinding.confirm.setOnClickListener { removeFile(mediaItem) }
        dialogBinding.cancel.setOnClickListener { dialog.dismiss() }

    }

    private fun shareFile(mediaItem: MediaItem) {
        val uri = if (mediaItem.isAudio) MediaStore.Audio.Media.EXTERNAL_CONTENT_URI else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.MediaColumns.TITLE} = ?"
        val selectionArgs = arrayOf(mediaItem.title)
        val cursor = contentResolver.query(uri, null, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val idColumn = it.getColumnIndex(MediaStore.MediaColumns._ID)
                val id = it.getLong(idColumn)
                val fileUri = Uri.withAppendedPath(uri, id.toString())
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = if (mediaItem.isAudio) "audio/*" else "video/*"
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                }
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }
        }
    }

    private fun showDialogRename(mediaItem: MediaItem){
        val dialogBinding = DialogRenameBinding.inflate(layoutInflater)

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.show()

        dialogBinding.confirm.setOnClickListener {
            val newName = dialogBinding.edtRename.text.toString().trim()
            if (newName.isNotEmpty()) {
                mediaItem.title = newName
                binding.recycleView.adapter?.notifyDataSetChanged()
                Toast.makeText(this, "File renamed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialogBinding.cancel.setOnClickListener { dialog.dismiss() }
    }

    private fun showDialogGetMedia() {
        val dialogBinding = DialogGetMediaBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        // Giả sử bạn đã có danh sách audio và video từ device
        val audioList = fetchAudioFiles()  // Tạo phương thức này để lấy danh sách audio
        val videoList = fetchVideoFiles()  // Tạo phương thức này để lấy danh sách video

        val adapter = ViewPagerAdapter(this, audioList, videoList)
        dialogBinding.viewPager.adapter = adapter

        dialogBinding.tvAudio.setOnClickListener {
            dialogBinding.viewPager.currentItem = 0
        }

        dialogBinding.tvVideo.setOnClickListener {
            dialogBinding.viewPager.currentItem = 1
        }

    }

    private fun fetchAudioFiles(): List<MediaItem> {
        val audioList = mutableListOf<MediaItem>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)

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

    private fun fetchVideoFiles(): List<MediaItem> {
        val videoList = mutableListOf<MediaItem>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)

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



}