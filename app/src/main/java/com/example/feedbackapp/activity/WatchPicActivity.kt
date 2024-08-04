package com.example.feedbackapp.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.feedbackapp.R

class WatchPicActivity : AppCompatActivity() {
    private var photoImageView:com.github.chrisbanes.photoview.PhotoView?=null
    private var quitIV:ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_watch_pic)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        quitIV = findViewById(R.id.quit_iv)
        quitIV?.setOnClickListener {
            this.finish()
        }
        photoImageView =
            findViewById(R.id.photoImageView)
        val uri = intent.getStringExtra("photoUrl")?:""

        if (uri.isNotBlank() && !uri.endsWith(".jpg")) {
            val inputStream = contentResolver.openInputStream(uri.toUri())

            // 将输入流解析为Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // 关闭输入流
            inputStream!!.close()

            // 显示Bitmap到ImageView
            photoImageView?.setImageBitmap(bitmap)

        } else {
            photoImageView?.let {
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.add_image)
                    .into(it)
            }
        }

    }
}