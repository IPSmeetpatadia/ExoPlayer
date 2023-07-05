package com.ipsmeet.exoplayer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ipsmeet.exoplayer.adapter.VideoFolderAdapter
import com.ipsmeet.exoplayer.databinding.ActivityVideoFoldersBinding
import com.ipsmeet.exoplayer.dataclass.MediaFileDataClass
import com.ipsmeet.exoplayer.viewmodel.VideoFolderListViewModel

class VideoFoldersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoFoldersBinding

    private lateinit var viewModel: VideoFolderListViewModel
    private var fileList = arrayListOf<MediaFileDataClass>()
    private var folderPath = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoFoldersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Video Player"

        viewModel = ViewModelProvider(this)[VideoFolderListViewModel::class.java]

        displayFolders()

        binding.swipeRefresh.setOnRefreshListener {
            displayFolders()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun displayFolders() {
        fileList = viewModel.fetchMedia(this, folderPath)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@VideoFoldersActivity, LinearLayoutManager.VERTICAL, false)
            adapter = VideoFolderAdapter(this@VideoFoldersActivity, folderPath)
        }
    }
}