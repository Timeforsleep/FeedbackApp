package com.example.feedbackapp.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.feedbackapp.R
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebConfig
import com.just.agentweb.DefaultWebClient

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AgentWebFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgentWebFragment(val webUrl:String) : Fragment() {
    private lateinit var mAgentWeb: AgentWeb
    private lateinit var backView:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_agent_web, container, false)
        backView = view.findViewById(R.id.back_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = webUrl
        backView.setOnClickListener {
            activity?.finish()
        }
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(view as FrameLayout, -1, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            .useDefaultIndicator(-1, 3)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
//            .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
            .interceptUnkownUrl()
            .createAgentWeb()
            .ready()
            .go(url)

        AgentWebConfig.debug()

        mAgentWeb.webCreator.webView.apply {
            overScrollMode = WebView.OVER_SCROLL_NEVER
            settings.apply {
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                useWideViewPort = true
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                loadsImagesAutomatically = true
//                needInitialFocus = true
                loadWithOverviewMode = true
                domStorageEnabled = true
                builtInZoomControls = true
                setSupportZoom(true)
                allowFileAccess = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
            }
            setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && canGoBack()) {
                        goBack()
                        true
                    } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                        activity?.moveTaskToBack(true)
                    }
                }
                false
            }
        }
    }

    override fun onResume() {
        mAgentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onPause() {
        mAgentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        mAgentWeb.webLifeCycle.onDestroy()
        super.onDestroyView()
    }
}
