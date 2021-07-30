package shiddush.view.com.mmvsd.ui.createResume

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_create_resume.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityCreateResumeBinding
import shiddush.view.com.mmvsd.repository.WebConstants
import java.util.*
import kotlin.concurrent.schedule

class CreateResumeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateResumeBinding
    private var browser: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@CreateResumeActivity, R.layout.activity_create_resume)

        clickListeners()
        loadWebView()
    }

    override fun onPause() {
        super.onPause()
        browser?.onPause()
    }
    override fun onResume() {
        super.onResume()
        browser?.onResume()
    }

    private fun loadWebView() {
        var count = 0;
        browser = findViewById<View>(R.id.wvResume) as WebView
        browser!!.settings.javaScriptEnabled = true
        browser!!.loadUrl(WebConstants.CREATE_RESUME_URL)
        browser!!.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                skLoader.visibility = View.GONE
                println(url)
                if (count == 1) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
                    }, 1000)
                }
                count++
                //browser!!.loadUrl(js)
            }

        }
        browser!!.settings.javaScriptEnabled = true

    }

    private fun clickListeners() {
        backView.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
        }
    }
}