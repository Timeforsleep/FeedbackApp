package com.example.feedbackapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feedbackapp.R
import com.example.feedbackapp.bean.FeedbackHistoryBean
import com.example.feedbackapp.util.CommonUtil

class FeedbackChildAdapter(private val context: Context) :
    RecyclerView.Adapter<FeedbackChildAdapter.FeedbackChildViewHolder>() {

    private val feedbackChildrenList: MutableList<FeedbackHistoryBean> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateFeedbackChildrenList(newList:List<FeedbackHistoryBean>){
        feedbackChildrenList.clear()
        feedbackChildrenList.addAll(newList)
        notifyDataSetChanged()
    }

    class FeedbackChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagTV:TextView = itemView.findViewById(R.id.tag_tv)
        val contentTV: TextView = itemView.findViewById(R.id.content)
        val photoRecyclerView: RecyclerView = itemView.findViewById(R.id.photoRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackChildViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feedback_child, parent, false)
        return FeedbackChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackChildViewHolder, position: Int) {
        val data = feedbackChildrenList[position]
        when(data.targetType){
            1->holder.tagTV.text ="追加"
            2->{
                holder.tagTV.text ="回复"
                holder.tagTV.setBackgroundColor(Color.parseColor("#FF8C00"))
            }
        }
        holder.contentTV.text = data.content
        val photoAdapter = PhotoAdapter(context)
        holder.photoRecyclerView.adapter = photoAdapter
        photoAdapter.updatePhotosList(data.photoList)
        holder.photoRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        val spacingInPixels = CommonUtil.dpToPx(8f, holder.itemView.context) // 将dp转换为像素
        val itemDecoration = SimpleGridSpacingItemDecoration(spacingInPixels)
        holder.photoRecyclerView.addItemDecoration(itemDecoration)
    }

    override fun getItemCount(): Int = feedbackChildrenList.size
}