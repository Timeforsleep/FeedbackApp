package com.example.feedbackapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.feedbackapp.bean.TypeBean

class QuestionTypeAdapter:RecyclerView.Adapter<QuestionTypeAdapter.questionTypeViewHolder>() {
    val questionTypeList:MutableList<TypeBean> = mutableListOf()

    inner class questionTypeViewHolder(view: View):RecyclerView.ViewHolder(view){
        val questionTypeTxt = view.findViewById<TextView>(R.id.type_tv)
        val questionTypeIcon = view.findViewById<ImageView>(R.id.type_iv)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTypeBeansList(newList: List<TypeBean>){
        this.questionTypeList.clear()
        this.questionTypeList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): questionTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_type,parent,false)
        return questionTypeViewHolder((view))
    }

    override fun getItemCount(): Int = questionTypeList.size

    override fun onBindViewHolder(holder: questionTypeViewHolder, position: Int) {
        val data = questionTypeList[position];
        holder.questionTypeTxt.text = data.typeName
//        Glide.with(holder.itemView)
//            .load(data.iconUrl)
//            .placeholder(R.drawable.add_image)
//            .into(holder.questionTypeIcon)
    }
}