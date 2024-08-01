package com.example.feedbackapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feedbackapp.R

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
//            holder.photoImageView.load(photoUrl){
//                crossfade(true)
//                placeholder(R.drawable.add_image)
//            }
        Glide.with(context)
            .load(photoUrl)
            .into(holder.photoImageView)
    }

    override fun getItemCount(): Int = photosList.size
}
