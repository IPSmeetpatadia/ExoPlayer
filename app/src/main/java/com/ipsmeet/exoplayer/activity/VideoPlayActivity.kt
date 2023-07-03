package com.ipsmeet.exoplayer.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.ipsmeet.exoplayer.R
import com.ipsmeet.exoplayer.databinding.ActivityVideoPlayBinding

class VideoPlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayBinding

    private lateinit var exoPlayer: ExoPlayer
    private var position = 0
    private lateinit var title: String
    lateinit var path: String
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.player = exoPlayer

        position = intent.getIntExtra("position", 1)
        Log.d("position", position.toString())
        title = intent.getStringExtra("displayName").toString()
        Log.d("title", title)
        findViewById<TextView>(R.id.txt_videoTitle).text = title
        path = intent.getStringExtra("path").toString()
        Log.d("videoData", path)

        val mediaItem = MediaItem.fromUri(path)
        exoPlayer.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }

        isPlaying = true

        findViewById<ImageView>(R.id.imgV_playPause).setOnClickListener {
            if (isPlaying) {
                isPlaying = false
                exoPlayer.pause()
                Glide.with(this).load(R.drawable.round_play_arrow_24).into(findViewById(R.id.imgV_playPause))
            } else {
                isPlaying = true
                exoPlayer.play()
                Glide.with(this).load(R.drawable.round_pause_24).into(findViewById(R.id.imgV_playPause))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isPlaying = false
        exoPlayer.pause()
        Glide.with(this).load(R.drawable.round_play_arrow_24).into(findViewById(R.id.imgV_playPause))
    }

    override fun onStop() {
        super.onStop()
        isPlaying = false
        exoPlayer.stop()
        Glide.with(this).load(R.drawable.round_play_arrow_24).into(findViewById(R.id.imgV_playPause))
    }
}