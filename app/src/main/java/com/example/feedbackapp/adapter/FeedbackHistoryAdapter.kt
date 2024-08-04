package com.example.feedbackapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feedbackapp.R
import com.example.feedbackapp.activity.FeedbackHistoryActivity
import com.example.feedbackapp.bean.FeedbackHistoryBean
import com.example.feedbackapp.bean.FeedbackRequest
import com.example.feedbackapp.common.REPLY_TYPE_ADD
import com.example.feedbackapp.util.CommonUtil

class FeedbackHistoryAdapter(private val activity: Activity) :
    RecyclerView.Adapter<FeedbackHistoryAdapter.FeedbackViewHolder>() {

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
        private val childrenRecyclerView: RecyclerView =
            itemView.findViewById(R.id.childrenRecyclerView)
        private val feedbackProcessTV: TextView = itemView.findViewById(R.id.feedback_process_tv)
        private val additionalDescriptionTV: TextView =
            itemView.findViewById(R.id.additional_description_tv)

        fun bind(feedback: FeedbackHistoryBean) {
            timeTV.text = feedback.modifiedTime
            contentTV.text = feedback.content
            tagTV.text = feedback.tagName
            feedback.schedule?.let {
                schedule->
                when (schedule) {
                    0->{
                        feedbackProcessTV.setTextColor(Color.RED)
                        feedbackProcessTV.text="未受理"
                    }
                    1->{
                        feedbackProcessTV.setTextColor(Color.GRAY)
                        feedbackProcessTV.text="处理中"
                    }
                    2->{
                        feedbackProcessTV.setTextColor(Color.parseColor("#2E8B57"))
                        feedbackProcessTV.text="处理完成"
                    }
                }
            }
            additionalDescriptionTV.setOnClickListener {
                (activity as FeedbackHistoryActivity).addAlertDialog.feedbackRequest =
                    FeedbackRequest(
                        targetId = feedback.id,
                        targetType = REPLY_TYPE_ADD,
                        userId = feedback.userId,
                        deviceId = feedback.deviceId,
                        status = feedback.status,
                        category = feedback.category,
                        tagId = feedback.tagId,
                        tagName = feedback.tagName,
                        content = feedback.content,
                        relation = null,
                        startTime = null,
                        endTime = null
                    )
                (activity as FeedbackHistoryActivity).addAlertDialog.show()

            }

            // Set up photo RecyclerView
            if (feedback.localFile != null) {
                photoRecyclerView.visibility = View.VISIBLE
                val photoAdapter = PhotoAdapter(activity)
                photoRecyclerView.adapter = photoAdapter
                photoAdapter.updatePhotosList(feedback.localFile!!)
                photoRecyclerView.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                val spacingInPixels = CommonUtil.dpToPx(8f, itemView.context) // 将dp转换为像素
                val itemDecoration = SimpleGridSpacingItemDecoration(spacingInPixels)
            } else {
                photoRecyclerView.visibility = View.GONE
            }

            // Set up children RecyclerView if there are children
            if (!feedback.children.isNullOrEmpty()) {
                childrenRecyclerView.visibility = View.VISIBLE
                val feedbackChildAdapter = FeedbackChildAdapter(activity)
                childrenRecyclerView.adapter = feedbackChildAdapter
                feedbackChildAdapter.updateFeedbackChildrenList(feedback.children!!)
                childrenRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
                val spacingInPixels = CommonUtil.dpToPx(8f, itemView.context) // 将dp转换为像素
                val itemDecoration = SimpleGridSpacingItemDecoration(spacingInPixels)
//                photoRecyclerView.addItemDecoration(itemDecoration)
            } else {
                childrenRecyclerView.visibility = View.GONE
            }
        }
    }
}
