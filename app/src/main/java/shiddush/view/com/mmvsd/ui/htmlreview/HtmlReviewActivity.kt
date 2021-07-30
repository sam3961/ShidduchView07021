package shiddush.view.com.mmvsd.ui.htmlreview

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import shiddush.view.com.mmvsd.R

class HtmlReviewActivity : AppCompatActivity() {
    var browser: WebView? = null
    var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_html_review)
        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.activity_html_review)
        val window = dialog!!.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        dialog!!.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog!!.show()
        val email = "aa@aaa.com"
        val sessionid = "adsadsad"
        val toId = "2424234"
        val fromId = "sdfsfsfds"
        browser = dialog!!.findViewById<View>(R.id.webChart) as WebView
        browser!!.settings.javaScriptEnabled = true
        browser!!.loadUrl("https://review.shidduchview.com/chatzapier/index.php?os=android")
        browser!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val js = "javascript:loadData('$email','$sessionid','$toId','$fromId')"
                println(js)
                browser!!.loadUrl(js)
            }
        }
        browser!!.settings.javaScriptEnabled = true
        browser!!.addJavascriptInterface(WebViewJavaScriptInterface(this), "Android")
    }

    inner class WebViewJavaScriptInterface /*
         * Need a reference to the context in order to sent a post message
         */(private val context: Context) {
        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        fun loadResult(result: Boolean) {
            println("loader worked")
            dialog!!.dismiss()
        }

    }

    override fun onPause() {
        super.onPause()
        browser!!.onPause()
    }
    override fun onResume() {
        super.onResume()
        browser!!.onResume()
    }
}