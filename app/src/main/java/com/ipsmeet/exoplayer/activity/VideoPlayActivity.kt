package com.ipsmeet.exoplayer.activity

import android.content.pm.ActivityInfo
import android.media.MediaFormat
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.*
import androidx.media3.exoplayer.video.VideoFrameMetadataListener
import com.bumptech.glide.Glide
import com.google.common.net.HttpHeaders.USER_AGENT
import com.ipsmeet.exoplayer.R
import com.ipsmeet.exoplayer.databinding.ActivityVideoPlayBinding
import com.ipsmeet.exoplayer.dataclass.MediaFileDataClass

@UnstableApi
class VideoPlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayBinding

    private lateinit var exoPlayer: ExoPlayer
    private var position = 0
    private lateinit var title: String
    private var videoPlayList = arrayListOf<MediaFileDataClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setFullScreen()
        setContentView(binding.root)

        position = intent.getIntExtra("position", 1)
        title = intent.getStringExtra("displayName").toString()
        val bundle = intent.getBundleExtra("bundle")!!  // we have passed bundle, so we need to fetch it as bundle first
        videoPlayList = bundle.getParcelableArrayList("videoPlayList")!!    // bundle contains ArrayList

        // setting title of video in `custom_control_layout.xml
        findViewById<TextView>(R.id.txt_videoTitle).text = title

        playVideo()
    }

    private fun playVideo() {
        val mediaItem = MediaItem.fromUri(videoPlayList[position].path!!)
        val dataSourceFactory = DefaultDataSourceFactory(this, USER_AGENT)
        val concatenatingMediaSource = ConcatenatingMediaSource()

        var currentOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        // listen for video size changes
        val videoListener = object : VideoFrameMetadataListener {
            override fun onVideoFrameAboutToBeRendered(presentationTimeUs: Long, releaseTimeNs: Long, format: Format, mediaFormat: MediaFormat?) {
                val newOrientation = if (format.width > format.height) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }

                // update the activity's orientation if it has changed
                if (newOrientation != currentOrientation) {
                    currentOrientation = newOrientation
                    requestedOrientation = currentOrientation
                }
            }
        }

        for (i in 0..videoPlayList.size) {
            val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
            concatenatingMediaSource.addMediaSource(mediaSourceFactory)
        }

        concatenatingMediaSource.addEventListener(Handler(), object : MediaSourceEventListener {
            override fun onLoadCompleted(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?, loadEventInfo: LoadEventInfo, mediaLoadData: MediaLoadData) {
                super.onLoadCompleted(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData)
                if (mediaLoadData.trackFormat != null) {
                    requestedOrientation = if (mediaLoadData.trackFormat!!.width > mediaLoadData.trackFormat!!.height) {
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                }
            }
        })

        exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.apply {
            player = exoPlayer
            keepScreenOn = true
        }

        exoPlayer.apply {
            prepare(concatenatingMediaSource)
            play()
            playWhenReady = true
            seekTo(position, C.TIME_UNSET)
            setVideoFrameMetadataListener(videoListener)
        }

        videoController()
    }

    private fun videoController() {
        //  PLAY-PAUSE
        findViewById<ImageView>(R.id.imgV_playPause).setOnClickListener {
            if (exoPlayer.playWhenReady) {
                exoPlayer.playWhenReady = false
                exoPlayer.pause()
                Glide.with(this).load(R.drawable.round_play_arrow_24)
                    .into(findViewById(R.id.imgV_playPause))
            } else {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
                Glide.with(this).load(R.drawable.round_pause_24)
                    .into(findViewById(R.id.imgV_playPause))
            }
        }

        //  NEXT
        findViewById<ImageView>(R.id.imgV_next).setOnClickListener {
            try {
                exoPlayer.stop()
                position++
                findViewById<TextView>(R.id.txt_videoTitle).text = videoPlayList[position].displayName
                playVideo()
            }
            catch (e: Exception) {
                finish()
            }
        }

        //  PREVIOUS
        findViewById<ImageView>(R.id.imgV_previous).setOnClickListener {
            try {
                exoPlayer.stop()
                position--
                findViewById<TextView>(R.id.txt_videoTitle).text = videoPlayList[position].displayName
                playVideo()
            }
            catch (e: Exception) {
                finish()
            }
        }

        //  FORWARD
        findViewById<ImageView>(R.id.imgV_forward).setOnClickListener {
            exoPlayer.seekTo(exoPlayer.currentPosition + 5000)
        }

        //  REPLAY
        findViewById<ImageView>(R.id.imgV_replay).setOnClickListener {
            exoPlayer.seekTo(exoPlayer.currentPosition - 5000)
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.apply {
            playWhenReady = false
            pause()
            playbackState
        }
    }

    override fun onRestart() {
        super.onRestart()
        exoPlayer.apply {
            playWhenReady = true
            playbackState
        }
    }

    private fun setFullScreen() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}