package com.example.feedbackapp.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feedbackapp.R

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
        val uri = intent.getStringExtra("photoUrl")?:""

        if (uri.isNotBlank()) {
            val inputStream = contentResolver.openInputStream(uri.toUri())

            // 将输入流解析为Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // 关闭输入流
            inputStream!!.close()
            val photoImageView = findViewById<com.github.chrisbanes.photoview.PhotoView>(R.id.photoImageView)
            // 显示Bitmap到ImageView
            photoImageView.setImageBitmap(bitmap)
            val quitIV: ImageView = findViewById<ImageView>(R.id.quit_iv)
            quitIV.setOnClickListener {
                this.finish()
            }
        }

    }
}