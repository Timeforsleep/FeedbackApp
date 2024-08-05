package com.example.feedbackapp.activity

import AddAlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.feedbackapp.R
import com.example.feedbackapp.adapter.FeedbackHistoryAdapter
import com.example.feedbackapp.net.NetworkInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedbackHistoryActivity : AppCompatActivity() {
    private val backIV: ImageView by lazy { findViewById(R.id.back_iv) }
    private val feedbackHistoryRV:RecyclerView by lazy { findViewById(R.id.feedback_history_rv) }
    private val swipeRefreshLayout:SwipeRefreshLayout by lazy { findViewById(R.id.swipe_refresh_layout) }
    private val feedbackHistoryAdapter by lazy { FeedbackHistoryAdapter(this@FeedbackHistoryActivity) }
    var addAlertDialog = AddAlertDialog(this)
    private val emptyImageView by lazy { findViewById<ImageView>(R.id.empty_imageview) }

    val progressBar: ProgressBar by lazy { findViewById(R.id.progressBar) }

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
        swipeRefreshLayout.setOnRefreshListener { refreshFeedbackHistory() }
        refreshFeedbackHistory()
    }

//    override fun onResume() {
//        super.onResume()
//        Log.w("onResu", "onResume: ", )
//    }

    fun refreshFeedbackHistory() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            NetworkInstance.getFeedbackHistory(18809761).collectLatest {
//                mainViewModel.typeBeans.value = it.result
                if (it.returnCode == 0) {
                    swipeRefreshLayout.isRefreshing = false;
                    withContext(Dispatchers.Main){
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@FeedbackHistoryActivity, "获取反馈历史成功！", Toast.LENGTH_SHORT).show()
                        emptyImageView.visibility = View.GONE
                    }
                    // 将 Map 转换为 List<TypeBean>
                    val feedbackHistoryList = it.result
                    Log.w("gyk", "refreshFeedbackHistory: ${it.result.toString()}", )
                    feedbackHistoryRV.adapter = feedbackHistoryAdapter
                    feedbackHistoryRV.layoutManager = LinearLayoutManager(this@FeedbackHistoryActivity,LinearLayoutManager.VERTICAL,false)
                    feedbackHistoryAdapter.updateFeedbackList(feedbackHistoryList)
                } else {
                    swipeRefreshLayout.isRefreshing = false;
                    // 处理 API 错误，例如记录日志
                    Log.e("MyViewModel", "API Error: ${it.message}")
                    withContext(Dispatchers.Main){
                        progressBar.visibility = View.GONE
                        emptyImageView.visibility = View.VISIBLE
                        Toast.makeText(this@FeedbackHistoryActivity, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        addAlertDialog.onActivityResult(requestCode, resultCode, data)
    }

}