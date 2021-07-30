package shiddush.view.com.mmvsd.ui.tnc

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.jaychang.st.SimpleText

import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityTermsAndConBinding
import shiddush.view.com.mmvsd.repository.WebConstants
import shiddush.view.com.mmvsd.ui.commonloginscreen.CommonLoginActivity
import shiddush.view.com.mmvsd.ui.signin.SignInActivity
import shiddush.view.com.mmvsd.ui.signup.SignUpActivity
import shiddush.view.com.mmvsd.utill.*


class TermsAndConActivity : AppCompatActivity() {

    lateinit var binding: ActivityTermsAndConBinding
    private var onClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms_and_con)
        changeNavBack()
        setTextSizes()
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
        val size250 = getFontSize(this, 250)
        val size150 = getFontSize(this, 150)
        val size40 = getFontSize(this, 40)
        val size30 = getFontSize(this, 30)
        val size25 = getFontSize(this, 25)
        val size20 = getFontSize(this, 20)
        val size18 = getFontSize(this, 18)

        binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.btnSubmit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvPrivacyPolicy.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
        binding.tvTermsAndCondition.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
        binding.tvEULA.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
        binding.tvWelcomeTo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)

        try {
            val size20 = getFontSize(this, 20)
            val ivHeightWidth = dpToPxs(size20.toInt())
            val ivHeaderHeight = dpToPxs(size150.toInt())
            val ivShidduchViewWidth = dpToPxs(size250.toInt())
            val ivShidduchViewHeight = dpToPxs(size40.toInt())
            binding.backArrow.layoutParams.height = ivHeightWidth
            binding.backArrow.layoutParams.width = ivHeightWidth
            binding.ivHeader.layoutParams.height = ivHeaderHeight
            binding.ivHeader.layoutParams.width = ivHeaderHeight
            binding.ivShidduchView.layoutParams.height = ivShidduchViewHeight
            binding.ivShidduchView.layoutParams.width = ivShidduchViewWidth
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //set different colors
        try {
            val ppText = SimpleText.from(getString(R.string.privacy_policy))
                    .first(getString(R.string.privacy_policy_click))
                    .textColor(R.color.colorDarkOrange)
                    .onClick(binding.tvPrivacyPolicy) { text, range, tag ->
                        goForWebView(getString(R.string.privacy_policy_click), WebConstants.PRIVACY_URL)
                    }
            binding.tvPrivacyPolicy.text = ppText

            val tncText = SimpleText.from(getString(R.string.terms_cond))
                    .first(getString(R.string.terms_cond_click))
                    .textColor(R.color.colorDarkOrange)
                    .onClick(binding.tvTermsAndCondition) { text, range, tag ->
                        goForWebView(getString(R.string.terms_and_conditions_title), WebConstants.TERMS_URL)
                    }
            binding.tvTermsAndCondition.text = tncText

            val eulaText = SimpleText.from(getString(R.string.and_eula))
                    .first(getString(R.string.eula))
                    .textColor(R.color.colorDarkOrange)
                    .onClick(binding.tvEULA) { text, range, tag ->
                        goForWebView(getString(R.string.eula), WebConstants.EULA_URL)
                    }
            binding.tvEULA.text = eulaText
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //click on I Agree Button
        binding.btnSubmit.setOnClickListener {
            binding.btnSubmit.isEnabled = false
            goForSignUP()
        }

        //on back click listener
        binding.backView.setOnClickListener {
            binding.backView.isEnabled = false
            goBack()
        }

    }

    override fun onResume() {
        super.onResume()
        onClick = false
        try {
            AppInstance.userObj = getUserObject(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        goBack()
    }

    /**
     * navigate to web view
     */
    private fun goForWebView(title: String, url: String) {
        if (!onClick) {
            onClick = true
            val intent = Intent(this@TermsAndConActivity, WebViewActivity::class.java)
            intent.putExtra(TITLE, title)
            intent.putExtra(WEB_URL, url)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
        }
    }

    /**
     * navigate to signup
     */
    private fun goForSignUP() {
        val extras = intent.extras
        if (extras != null) {
            val intent = Intent(this@TermsAndConActivity, SignUpActivity::class.java)
            intent.putExtra(SOURCE, extras.getString(SOURCE, ""))
            intent.putExtra(VIA, extras.getString(VIA, ""))
            startActivity(intent)
            this@TermsAndConActivity.finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
        }
    }

    /**
     * navigate to signin or common login
     */
    private fun goBack() {
        val extras = intent.extras
        var source: String = ""
        if (extras != null) {
            source = extras.getString(SOURCE, "")
        }
        val intent = Intent(this, CommonLoginActivity::class.java)
        startActivity(intent)
        this@TermsAndConActivity.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
    }
}
