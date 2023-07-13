package com.ipsmeet.exoplayer.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import com.ipsmeet.exoplayer.R
import com.ipsmeet.exoplayer.activity.MusicListActivity

@UnstableApi class NotificationService : Service() {

    var serviceBinder: IBinder = ServiceBinder()
    lateinit var exoPlayer: ExoPlayer
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var mediaSession: MediaSessionCompat

    private val notificationId = 111
    private val channelId = "music_player_channel"
    private val channelName = "Music Player"

    inner class ServiceBinder: Binder() {
        fun getPlayerService(): NotificationService = this@NotificationService
    }

    override fun onBind(intent: Intent): IBinder {
        return serviceBinder
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(applicationContext).build()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        exoPlayer.setAudioAttributes(audioAttributes, true)

        val title = exoPlayer.currentMediaItem?.mediaMetadata?.displayTitle
        Log.d("title", title.toString())

        //  Initialize notification
        playerNotificationManager = PlayerNotificationManager.Builder(this, notificationId, channelId)
            .setNotificationListener(notificationListener)
            .setMediaDescriptionAdapter(descriptionAdapter)
            .setChannelNameResourceId(R.string.notification_channel_name)
            .setChannelImportance(IMPORTANCE_HIGH)
            .setSmallIconResourceId(R.drawable.round_music_note_24)
            .setChannelDescriptionResourceId(R.string.app_name)
            .setNextActionIconResourceId(R.drawable.round_skip_next_24)
            .setPreviousActionIconResourceId(R.drawable.round_skip_previous_24)
            .setPlayActionIconResourceId(R.drawable.round_play_arrow_24)
            .setPauseActionIconResourceId(R.drawable.round_pause_24)
            .build()

        //  Set-up Media session
        mediaSession = MediaSessionCompat(applicationContext, "Music Player")
        mediaSession.apply {
            isActive = true
            setMetadata(MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "title")
//                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, "description")
                .build())
        }

        /*val mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(exoPlayer)*/

        //  Set player to notification manager
        playerNotificationManager.apply {
            setPlayer(exoPlayer)
            setPriority(NotificationCompat.PRIORITY_MAX)
            setUseRewindAction(false)
            setUseFastForwardAction(false)
            setMediaSessionToken(mediaSession.sessionToken)
        }
    }

    //  Notification listener
    private val notificationListener = object : PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            super.onNotificationCancelled(notificationId, dismissedByUser)
            stopForeground(notificationId)
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            }
        }

        override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
            super.onNotificationPosted(notificationId, notification, ongoing)
            startForeground(notificationId, notification)
        }
    }

    //  Notification description adapter
    private val descriptionAdapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return "player.currentMediaItem?.mediaMetadata?.displayTitle!!"
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            val intent = Intent(applicationContext, MusicListActivity::class.java)
            return PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return null
        }

        override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap {
            val imageView = ImageView(applicationContext)
            imageView.setImageURI(player.currentMediaItem?.mediaMetadata?.artworkUri)

            var bitmapDrawable = imageView.drawable
            if (bitmapDrawable == null) {
                bitmapDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.round_music_note_24)
            }

            return  bitmapDrawable.toBitmap()
        }
    }

    override fun onDestroy() {
        mediaSession.isActive = false
        if (exoPlayer.isPlaying) {
            exoPlayer.stop()
        }
        playerNotificationManager.setPlayer(null)
        exoPlayer.release()
        stopForeground(true)
        stopSelf()
        super.onDestroy()
    }

}