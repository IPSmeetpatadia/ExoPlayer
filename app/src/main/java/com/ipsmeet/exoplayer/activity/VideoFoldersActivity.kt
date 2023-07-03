package com.ipsmeet.exoplayer.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ipsmeet.exoplayer.adapter.VideoFolderAdapter
import com.ipsmeet.exoplayer.databinding.ActivityVideoFoldersBinding
import com.ipsmeet.exoplayer.dataclass.MediaFileDataClass
import java.io.File

class VideoFoldersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoFoldersBinding

    private var fileList = arrayListOf<MediaFileDataClass>()
    private var folderPath = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoFoldersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = "Video Player"

        //  CHECK FOR EXTERNAL STORAGE PERMISSION
        checkPermission(this)
        displayFolders()

        binding.swipeRefresh.setOnRefreshListener {
            displayFolders()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun displayFolders() {
        fileList = fetchMedia()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@VideoFoldersActivity, LinearLayoutManager.VERTICAL, false)
            adapter = VideoFolderAdapter(this@VideoFoldersActivity, folderPath)
        }
    }

    @SuppressLint("Range")
    private fun fetchMedia(): ArrayList<MediaFileDataClass> {
        val folderList = ArrayList<MediaFileDataClass>()
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                val size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                val duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))

                val mediaData = MediaFileDataClass(id, title, displayName, size, duration, path, dateAdded)

                if (!folderPath.contains(File(path).parentFile!!.name)) {
                    folderPath.add(File(path).parentFile!!.name)
                }
                folderList.add(mediaData)
            }
        }
        return folderList
    }

    private fun checkPermission(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // system-default permission request permission dialog
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_MEDIA_VIDEO",
                    "android.permission.READ_MEDIA_AUDIO"
                ), 1)
            return
        }
    }
}