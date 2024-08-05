package com.example.feedbackapp.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feedbackapp.R
import com.example.feedbackapp.fragment.AgentWebFragment

class AgentWebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agent_web_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        findViewById<View>(R.id.back_view).setOnClickListener {
//            this.finish()
//        }
        val webUrl = intent.getStringExtra("webUrl")?:""
        val isFromFeedbackHistory = intent.getBooleanExtra("isFromFeedbackHistory",false)
        if (webUrl.isNotBlank() && isFromFeedbackHistory) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AgentWebFragment(webUrl,isFromFeedbackHistory))
                .commit()
        }else if (webUrl.isNotBlank() && !isFromFeedbackHistory) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AgentWebFragment(webUrl))
                .commit()
        } else {
            this.finish()
        }
    }

}