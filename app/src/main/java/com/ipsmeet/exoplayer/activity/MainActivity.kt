package com.ipsmeet.exoplayer.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ipsmeet.exoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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