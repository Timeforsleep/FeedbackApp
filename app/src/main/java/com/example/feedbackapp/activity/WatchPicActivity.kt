package com.example.feedbackapp.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feedbackapp.R
import com.example.feedbackapp.util.CommonUtil

class WatchPicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_watch_pic)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var base64String = intent.getStringExtra("photoUrl")?:""
        val quitIV: ImageView = findViewById<ImageView>(R.id.quit_iv)
        quitIV.setOnClickListener {
            this.finish()
        }
        val photoImageView = findViewById<com.github.chrisbanes.photoview.PhotoView>(R.id.photoImageView)
        CommonUtil.loadBase64Image(base64String,photoImageView)
    }
}