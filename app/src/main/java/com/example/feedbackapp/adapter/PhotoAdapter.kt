package com.example.feedbackapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.feedbackapp.R
import com.example.feedbackapp.activity.WatchPicActivity
import com.example.feedbackapp.util.CommonUtil

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUrl = photosList[position]
        holder.photoImageView.setOnClickListener {
            val intent = Intent(context, WatchPicActivity::class.java)
            intent.putExtra("photoUrl", photoUrl)
            context.startActivity(intent)
        }
        try {
            CommonUtil.loadBase64Image(photoUrl, holder.photoImageView)
        } catch (e: Exception) {

            holder.photoImageView.load(photoUrl){
                crossfade(true)
                placeholder(R.drawable.add_image)
            }
        }
//        Glide.with(context)
//            .load(photoUrl)
//            .placeholder(R.drawable.add_image)
//            .into(holder.photoImageView)
    }

    override fun getItemCount(): Int = photosList.size
}
