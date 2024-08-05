package com.example.feedbackapp.dialog

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.example.feedbackapp.R
import com.example.feedbackapp.activity.MainActivity
import com.example.feedbackapp.bean.ScoreBean
import com.example.feedbackapp.common.HAVE_ADD_SCORE
import com.example.feedbackapp.net.NetworkInstance
import com.example.feedbackapp.util.MMKVUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddScoreDialog(private val activity: MainActivity) {
    private var cancelTV:TextView? = null
    private var scoreTV:TextView? = null
    private var ratingBar:RatingBar?=null


    fun show() {
        val inflater = LayoutInflater.from(activity)
        val dialogView = inflater.inflate(R.layout.rating_dialog, null)
        cancelTV = dialogView.findViewById(R.id.cancel_tv)
        scoreTV = dialogView.findViewById(R.id.add_score_tv)
        ratingBar = dialogView.findViewById(R.id.ratingBar)

        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.show()
        var ratingPoint = 2.5
        ratingBar?.setOnRatingBarChangeListener(object : RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                ratingPoint = rating.toDouble()
            }
        }
        )
        cancelTV?.setOnClickListener {
            dialog.dismiss()
        }
        scoreTV?.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                NetworkInstance.addScore(ScoreBean(score = ratingPoint)).collectLatest {
                    if (it.returnCode == 0) {
                        // 将 Map 转换为 List<TypeBean>
                        withContext(Dispatchers.Main){
                            Toast.makeText(activity, "评分成功！", Toast.LENGTH_SHORT).show()
                            MMKVUtil.putBoolean(HAVE_ADD_SCORE, true)
                            dialog.dismiss()
                        }
                    } else {
                        // 处理 API 错误，例如记录日志
                        withContext(Dispatchers.Main){
                            Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                }
            }
            dialog.dismiss()
        }
    }

}