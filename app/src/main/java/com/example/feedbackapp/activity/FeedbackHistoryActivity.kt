package com.example.feedbackapp.activity

import AddAlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feedbackapp.R
import com.example.feedbackapp.adapter.FeedbackHistoryAdapter
import com.example.feedbackapp.net.NetworkInstance
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FeedbackHistoryActivity : AppCompatActivity() {
    private val backIV: ImageView by lazy { findViewById(R.id.back_iv) }
    private val feedbackHistoryRV:RecyclerView by lazy { findViewById(R.id.feedback_history_rv) }
    private val feedbackHistoryAdapter by lazy { FeedbackHistoryAdapter(this@FeedbackHistoryActivity) }
    var addAlertDialog = AddAlertDialog(this)

    // 对话框消失时的回调

    fun onDialogDismissed() {
        refreshFeedbackHistory()
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feedback_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backIV.setOnClickListener {
            onBackPressed()
        }
        refreshFeedbackHistory()
    }

//    override fun onResume() {
//        super.onResume()
//        Log.w("onResu", "onResume: ", )
//    }

    fun refreshFeedbackHistory() {
        lifecycleScope.launch {
            NetworkInstance.getFeedbackHistory(18809761).collectLatest {
//                mainViewModel.typeBeans.value = it.result
                if (it.returnCode == 0) {
                    // 将 Map 转换为 List<TypeBean>
                    val feedbackHistoryList = it.result
                    Log.w("gyk", "refreshFeedbackHistory: ${it.result.toString()}", )
                    feedbackHistoryRV.adapter = feedbackHistoryAdapter
                    feedbackHistoryRV.layoutManager = LinearLayoutManager(this@FeedbackHistoryActivity,LinearLayoutManager.VERTICAL,false)
                    feedbackHistoryAdapter.updateFeedbackList(feedbackHistoryList)
                } else {
                    // 处理 API 错误，例如记录日志
                    Log.e("MyViewModel", "API Error: ${it.message}")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        addAlertDialog.onActivityResult(requestCode, resultCode, data)
    }

}