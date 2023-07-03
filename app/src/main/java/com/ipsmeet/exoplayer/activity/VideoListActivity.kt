package com.ipsmeet.exoplayer.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.MediaStore.Video.Media
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ipsmeet.exoplayer.adapter.VideoFileAdapter
import com.ipsmeet.exoplayer.databinding.ActivityVideoListBinding
import com.ipsmeet.exoplayer.dataclass.MediaFileDataClass

class VideoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoListBinding

    private lateinit var folderName: String
    private var videoList = arrayListOf<MediaFileDataClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        folderName = intent.getStringExtra("folderName").toString()
        supportActionBar!!.title = folderName
        showVideoFiles()

        binding.swipeRefresh.setOnRefreshListener {
            showVideoFiles()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun showVideoFiles() {
        videoList = fetchMedia(folderName)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@VideoListActivity, LinearLayoutManager.VERTICAL, false)
            adapter = VideoFileAdapter(this@VideoListActivity, videoList)
        }
    }

    @SuppressLint("Range")
    private fun fetchMedia(folderName: String): ArrayList<MediaFileDataClass> {
        val videoFiles = arrayListOf<MediaFileDataClass>()
        val uri = Media.EXTERNAL_CONTENT_URI
        val selection = "${ Media.DATA } LIKE ?"
        val cursor = contentResolver.query(uri, null, selection, arrayOf("%/$folderName/%"), null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(Media._ID))
                val title = cursor.getString(cursor.getColumnIndex(Media.TITLE))
                val displayName = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME))
                val size = cursor.getString(cursor.getColumnIndex(Media.SIZE))
                val duration = cursor.getString(cursor.getColumnIndex(Media.DURATION))
                val path = cursor.getString(cursor.getColumnIndex(Media.DATA))
                val dateAdded = cursor.getString(cursor.getColumnIndex(Media.DATE_ADDED))

                val mediaData = MediaFileDataClass(id, title, displayName, size, duration, path, dateAdded)
                videoFiles.add(mediaData)
            }
        }
        return videoFiles
     }
}