package com.example.loadfile.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.loadfile.databinding.ActivityMainBinding
import com.example.loadfile.databinding.DialogConfirmDeleteBinding
import com.example.loadfile.databinding.DialogGetMediaBinding
import com.example.loadfile.databinding.DialogOptionBinding
import com.example.loadfile.databinding.DialogRenameBinding
import com.example.loadfile.fragment.AudioFragment
import com.example.loadfile.fragment.VideoFragment
import com.example.loadfile.adapter.ViewPagerAdapter
import com.example.loadfile.model.MediaItem
import com.example.orderfood.base.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("NotifyDataSetChanged")
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val REQUEST_CODE_STORAGE = 1
    private val audioList = mutableListOf<MediaItem>()
    private val videoList = mutableListOf<MediaItem>()

    override fun initView() {


    }

    override fun bindView() {
        binding.add.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE
                )
            } else {
                showDialogGetMedia()
            }
        }

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

    private fun showDialogConfirmDelete(mediaItem: MediaItem) {
        val dialogBinding = DialogConfirmDeleteBinding.inflate(layoutInflater)

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        dialogBinding.confirm.setOnClickListener { removeFile(mediaItem) }
        dialogBinding.cancel.setOnClickListener { dialog.dismiss() }

    }

    private fun shareFile(mediaItem: MediaItem) {
        val uri =
            if (mediaItem.isAudio) MediaStore.Audio.Media.EXTERNAL_CONTENT_URI else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
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

    private fun showDialogRename(mediaItem: MediaItem) {
        val dialogBinding = DialogRenameBinding.inflate(layoutInflater)

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
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

        val adapter = ViewPagerAdapter(this)
        dialogBinding.viewPager.adapter = adapter

        dialogBinding.tvAudio.setOnClickListener {
            dialogBinding.viewPager.currentItem = 0
        }

        dialogBinding.tvVideo.setOnClickListener {
            dialogBinding.viewPager.currentItem = 1
        }

        dialogBinding.edtSearch.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            val currentFragment = adapter.getFragment(dialogBinding.viewPager.currentItem)
            if (currentFragment is AudioFragment) {
                currentFragment.filterAudioList(query)
            } else if (currentFragment is VideoFragment) {
                currentFragment.filterVideoList(query)
            }
        }
    }


}