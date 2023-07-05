package com.ipsmeet.exoplayer.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ipsmeet.exoplayer.databinding.ActivityMainBinding
import com.ipsmeet.exoplayer.viewmodel.PermissionViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionModel: PermissionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionModel = ViewModelProvider(this)[PermissionViewModel::class.java]
        permissionModel.requestPermission(this)

        binding.layoutMusicPlayer.setOnClickListener {
            Toast.makeText(this, "Get premium for access MUSIC PLAYER", Toast.LENGTH_SHORT).show()
        }

        binding.layoutVideoPlayer.setOnClickListener {
            startActivity(
                Intent(
                    this, VideoFoldersActivity::class.java
                )
            )
        }
    }
}