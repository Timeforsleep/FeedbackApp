package com.example.feedbackapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.feedbackapp.R
import com.example.feedbackapp.activity.AgentWebViewActivity
import com.example.feedbackapp.activity.WatchPicActivity

class PhotoAdapter(private val context: Context) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    
    private val photosList: MutableList<String> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updatePhotosList(newList:List<String>){
        photosList.clear()
        photosList.addAll(newList)
        notifyDataSetChanged()
    }
    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView:ImageView = itemView.findViewById(R.id.photoImageView)
        val isVideoIv:ImageView = itemView.findViewById(R.id.is_video_iv)
        val progressBar:ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUrl = photosList[position]
        Log.w("gyk", "onBindViewHolder: ${photoUrl}", )
        if (!photoUrl.endsWith(".mp4")) {
            holder.photoImageView.setOnClickListener {
                val intent = Intent(context, WatchPicActivity::class.java)
                intent.putExtra("photoUrl", photoUrl)
                intent.putExtra("index",position+1)
                intent.putExtra("size",photosList.size)
                context.startActivity(intent)
            }
        }
        holder.isVideoIv.setOnClickListener {
            val intent = Intent(context,AgentWebViewActivity::class.java)
            intent.putExtra("webUrl",photoUrl)
            intent.putExtra("isFromFeedbackHistory",true)
            context.startActivity(intent)
        }
        if (photoUrl.endsWith(".mp4")) {
            Glide.with(context)
                .asBitmap()
                .load(photoUrl)
                .listener(object :RequestListener<Bitmap>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false
                }

            })
                .thumbnail(0.1f) // 加载视频的缩略图，0.1f表示加载10%的帧数
                .into(holder.photoImageView)
            holder.isVideoIv.visibility = View.VISIBLE
        } else {
            Glide.with(context)
                .load(photoUrl)
                .listener(object :RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.progressBar.visibility = View.GONE
                        return false
                    }

                })
                .into(holder.photoImageView)

            holder.isVideoIv.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int = photosList.size
}
