package shiddush.view.com.mmvsd.ui.tnc

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil


import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityWebViewBinding
import shiddush.view.com.mmvsd.utill.TITLE
import shiddush.view.com.mmvsd.utill.WEB_URL
import shiddush.view.com.mmvsd.utill.dpToPxs
import shiddush.view.com.mmvsd.utill.getFontSize

class WebViewActivity : AppCompatActivity() {

    lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        changeNavBack()
        setTextSizes()
        setWebView()
    }

    /**
     * to change navigation bg color
     */
    private fun changeNavBack() {
        //To change navigation bar color
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to set all text sizes
     */
    private fun setTextSizes() {
        binding.tvNoSiteFound.visibility = View.GONE
        val size25 = getFontSize(this, 25)
        val size18 = getFontSize(this, 18)

        binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        try {
            val size20 = getFontSize(this, 20)
            val ivHeightWidth = dpToPxs(size20.toInt())
            binding.backArrow.layoutParams.height = ivHeightWidth
            binding.backArrow.layoutParams.width = ivHeightWidth
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //on back click listener
        binding.backView.setOnClickListener {
            binding.backView.isEnabled = false
            goBack()
        }
    }

    /**
     * setup web view
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        binding.skAILoader.visibility = View.VISIBLE
        val intentval = intent.extras
        if (intentval != null) {
            val title = intentval.getString(TITLE, "")
            val url = intentval.getString(WEB_URL, "")
            binding.tvTitle.text = title

            binding.wvTnC.settings.javaScriptEnabled = true
            binding.wvTnC.settings.loadWithOverviewMode = true
            binding.wvTnC.settings.useWideViewPort = true
            binding.wvTnC.clearCache(true)
            binding.wvTnC.webViewClient = MyWebViewClient(this)
            binding.wvTnC.loadUrl(url)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.wvTnC.onPause()
    }
    override fun onResume() {
        super.onResume()
        binding.wvTnC.onResume()
    }

    inner class MyWebViewClient internal constructor(private val activity: Activity) :
            WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
        ): Boolean {
            val url: String = request?.url.toString()
            view?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
        ) {
            binding.tvNoSiteFound.visibility = View.VISIBLE
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.skAILoader.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        goBack()
    }

    /**
     * navigate to tnc
     */
    private fun goBack() {
        this@WebViewActivity.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
    }


}
