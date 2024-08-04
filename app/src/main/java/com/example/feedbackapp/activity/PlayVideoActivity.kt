package com.example.feedbackapp.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feedbackapp.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

class PlayVideoActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_play_video)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        playerView = findViewById(R.id.playerView)
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        val videoFilePath = intent.getStringExtra("videoFilePath")
        videoFilePath?.let { playVideo(it) }
    }

    private fun playVideo(videoFilePath: String) {
        Log.w("gykVideoPath", "playVideo: ${videoFilePath}", )
        // 准备并播放视频
        val mediaItem = MediaItem.fromUri(Uri.parse(videoFilePath))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}