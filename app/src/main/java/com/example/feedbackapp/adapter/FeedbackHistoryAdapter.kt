package com.example.feedbackapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feedbackapp.R
import com.example.feedbackapp.bean.FeedbackHistoryBean
import com.example.feedbackapp.util.CommonUtil

class FeedbackHistoryAdapter(private val context: Context) : RecyclerView.Adapter<FeedbackHistoryAdapter.FeedbackViewHolder>() {

    private val feedbackList: MutableList<FeedbackHistoryBean> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feedback_history, parent, false)
        return FeedbackViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFeedbackList(newList: List<FeedbackHistoryBean>) {
        feedbackList.clear()
        feedbackList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.bind(feedback)
    }

    override fun getItemCount(): Int = feedbackList.size

    inner class FeedbackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeTV: TextView = itemView.findViewById(R.id.time_tv)
        private val tagTV: TextView = itemView.findViewById(R.id.tag_tv)
        private val contentTV: TextView = itemView.findViewById(R.id.content)
        private val photoRecyclerView: RecyclerView = itemView.findViewById(R.id.photoRecyclerView)
        private val childrenRecyclerView: RecyclerView = itemView.findViewById(R.id.childrenRecyclerView)

        fun bind(feedback: FeedbackHistoryBean) {
            timeTV.text = feedback.modifiedTime
            contentTV.text = feedback.content
            tagTV.text = feedback.tagName

            // Set up photo RecyclerView
            if (!feedback.photoList.isNullOrEmpty()) {
                val photoAdapter = PhotoAdapter(context)
                photoRecyclerView.adapter = photoAdapter
                photoAdapter.updatePhotosList(feedback.photoList)
                photoRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                val spacingInPixels = CommonUtil.dpToPx(8f, itemView.context) // 将dp转换为像素
                val itemDecoration = SimpleGridSpacingItemDecoration(spacingInPixels)
                photoRecyclerView.addItemDecoration(itemDecoration)
            }

            // Set up children RecyclerView if there are children
            if (!feedback.children.isNullOrEmpty()) {
                val feedbackChildAdapter = FeedbackChildAdapter(context)
                childrenRecyclerView.adapter = feedbackChildAdapter
                feedbackChildAdapter.updateFeedbackChildrenList(feedback.children)
                childrenRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
                val spacingInPixels = CommonUtil.dpToPx(8f, itemView.context) // 将dp转换为像素
                val itemDecoration = SimpleGridSpacingItemDecoration(spacingInPixels)
                photoRecyclerView.addItemDecoration(itemDecoration)
            }
        }
    }
}
