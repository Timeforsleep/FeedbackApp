package com.example.feedbackapp.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.feedbackapp.viewmodel.MainViewModel
import com.example.feedbackapp.R
import com.example.feedbackapp.bean.TypeBean
import com.example.feedbackapp.util.CommonUtil

class QuestionTypeAdapter(private val viewModel: MainViewModel):RecyclerView.Adapter<QuestionTypeAdapter.questionTypeViewHolder>() {
    val questionTypeList:MutableList<TypeBean> = mutableListOf()
    var questionTypeSeleted: TypeBean? = viewModel.questionSelectedScene.value

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
    @SuppressLint("NotifyDataSetChanged")
    fun updateSelectedTypeBean(newTypeBean: TypeBean) {
        this.questionTypeSeleted = newTypeBean
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): questionTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_type,parent,false)
        return questionTypeViewHolder((view))
    }

    override fun getItemCount(): Int = questionTypeList.size

    override fun onBindViewHolder(holder: questionTypeViewHolder, position: Int) {
        val data = questionTypeList[position]
        if (questionTypeSeleted?.typeName == data.typeName && questionTypeSeleted?.id == data.id) {
            CommonUtil.setBackGroundBlue(holder.itemView)
        }else{
            CommonUtil.setBackGroundGray(holder.itemView)
        }
        holder.questionTypeTxt.text = data.typeName
        holder.itemView.setOnClickListener {
            CommonUtil.setBackGroundBlue(it)
            viewModel.questionSelectedScene.value = TypeBean(data.id, data.typeName)
        }
//        Glide.with(holder.itemView)
//            .load(data.iconUrl)
//            .placeholder(R.drawable.add_image)
//            .into(holder.questionTypeIcon)
    }
}