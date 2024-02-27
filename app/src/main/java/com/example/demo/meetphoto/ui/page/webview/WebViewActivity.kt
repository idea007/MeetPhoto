package com.example.demo.meetphoto.ui.page.webview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.demo.meetphoto.R
import com.example.demo.meetphoto.databinding.ActivityWebViewBinding
import com.example.demo.meetphoto.ui.base.BaseThemeActivity
import com.dafay.demo.lib.base.utils.error

class WebViewActivity : BaseThemeActivity<ActivityWebViewBinding>() {

    companion object {
        fun startActivity(activity: Activity, uri: Uri) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra("url", uri.toString())
            activity.startActivity(intent)
        }
    }

    lateinit var passUrl: String
    override fun resolveIntent(intent: Intent?) {
        super.resolveIntent(intent)
        val thum = intent?.getStringExtra("url")
        if (thum.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        passUrl = thum
    }

    override fun initViews() {
        super.initViews()
        initToolBar()
        initVebView()
    }

    private fun initToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//添加默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true)//设置返回键可用
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        supportActionBar?.title = passUrl
    }

    private fun initVebView() {
        binding.wvWebview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                return if (url.startsWith("http://") || url.startsWith("https://")) {
                    false
                } else {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        view.context.startActivity(intent)
                        true
                    } catch (e: Exception) {
                        error("shouldOverrideUrlLoading Exception", e)
                        true
                    }
                }
            }
        }
        binding.wvWebview.settings.javaScriptEnabled = true
    }

    override fun initializeData() {
        super.initializeData()
        binding.wvWebview.loadUrl(passUrl)
    }
}