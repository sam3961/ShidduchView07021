package shiddush.view.com.mmvsd.ui.forgotpassword

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.LOGIN

import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityForgotPasswordBinding
import shiddush.view.com.mmvsd.model.forgotpassword.ForgotPasswordResponse
import shiddush.view.com.mmvsd.repository.ResponseCodes
import shiddush.view.com.mmvsd.ui.commonloginscreen.CommonLoginActivity
import shiddush.view.com.mmvsd.ui.tnc.TermsAndConActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.widget.loader.UtilLoader
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidColorConstants
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon


/**
 * Created by Sumit Kumar.
 *  This activity send  forgot password request
 */

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityForgotPasswordBinding
    lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var mUtilLoader: UtilLoader
    private var firebaseAnalytics: FirebaseAnalytics?=null
    private val TAG =ForgotPasswordActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        viewModel = ViewModelProviders.of(this).get(ForgotPasswordViewModel::class.java)
        binding.viewModel = viewModel
        mUtilLoader = UtilLoader(this)
        changeNavBack()
        setTextSizes()
        attachObserver()

        binding.resetPassword.setOnClickListener {
            hideVirtualKeyboard(this)
            if (checkValidation()) {
                if (isNetworkAvailable(this)) {
                    viewModel.onForgotPassword(it)
                } else {
                    showDialogNoInternet(this, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
                }
            }
        }

        binding.backView.setOnClickListener {
            binding.backView.isEnabled = false
            goBack()
        }

        binding.etEmail.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideVirtualKeyboard(this)
                true
            } else {
                hideVirtualKeyboard(this)
                false
            }
        }

    }

    /**
     * to change navigation bar back color
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
     * to set text sizes
     */
    private fun setTextSizes() {
        val size25 = getFontSize(this, 25)
        val size21 = getFontSize(this, 21)
        val size18 = getFontSize(this, 18)
        val size13 = getFontSize(this, 13)

        binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        binding.trouble.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.instruction.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.tvEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.etEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size21)
        binding.resetPassword.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)

        //set Background Image
        val bgImage = getBackImageSize(this)
        binding.clMainContainer.background = ContextCompat.getDrawable(this, bgImage)

        try {
            val size20 = getFontSize(this, 20)
            val ivHeightWidth = dpToPxs(size20.toInt())
            binding.backArrow.layoutParams.height = ivHeightWidth
            binding.backArrow.layoutParams.width = ivHeightWidth
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val size26 = getFontSize(this, 26)
            val size2 = getFontSize(this, 2)
            val etHeight = dpToPxs(size26.toInt())
            val etPadding = dpToPxs(size2.toInt())
            //Edit texts
            binding.etEmail.layoutParams.height = etHeight
            binding.ivEmail.layoutParams.height = etHeight
            binding.ivEmail.layoutParams.width = etHeight
            binding.ivEmail.setPadding(etPadding, etPadding, etPadding, etPadding)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //check validations
    private fun checkValidation(): Boolean {
        var isValid = false
        if (binding.etEmail.text!!.trim().isEmpty()) {
            binding.etEmail.error = getString(R.string.please_enter_email)
            binding.etEmail.requestFocus()
            isValid = false
        } else if (!isValidEmail(binding.etEmail.text!!)) {
            binding.etEmail.error = getString(R.string.please_enter_valid_email)
            binding.etEmail.requestFocus()
            isValid = false
            logFirebaseEvents("checkValidation",getString(R.string.please_enter_valid_email))
        } else {
            isValid = true
            binding.etEmail.error = null
        }
        return isValid
    }

    private fun attachObserver() {
        viewModel.isLoading.observe(this, Observer<Boolean> {
            it?.let { showLoadingDialog(it) }
        })
        viewModel.apiError.observe(this, Observer<String> {
            it?.let {
                //nothing to do
                showDialogNoInternet(this, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)

                logFirebaseEvents("apiError",getString(R.string.failure_response))

            }
        })
        viewModel.apiResponse.observe(this, Observer<Any> {
            it?.let {
                if (it is ForgotPasswordResponse) {
                    when(it.getCode()) {
                         ResponseCodes.Success -> SwapdroidAlertDialog.Builder(this)
                                .setTitle(getString(R.string.check_your_email))
                                .isMessageVisible(true)
                                .setMessage(getString(R.string.email_sent))
                                .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                                .setNegativeBtnText(getString(R.string.ok))
                                .isNegativeVisible(false)
                                .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                                .setPositiveBtnText(getString(R.string.ok))
                                .isPositiveVisible(true)
                                .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                                .setAnimation(SwapdroidAnimation.POP)
                                .isCancellable(false)
                                 .showCancelIcon(false)
                                .setIcon(R.drawable.ic_mailbox_icon, SwapdroidIcon.Visible)  //ic_star_border_black_24dp
                                .OnPositiveClicked {
                                    goBack()
                                }
                                .build()
                        ResponseCodes.NoUserFound -> SwapdroidAlertDialog.Builder(this)
                                .setTitle(getString(R.string.register_yourself))
                                .isMessageVisible(true)
                                .setMessage(getString(R.string.register_yourself_message))
                                .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                                .setNegativeBtnText(getString(R.string.try_again))
                                .isNegativeVisible(true)
                                .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                                .setPositiveBtnText(getString(R.string.register))
                                .isPositiveVisible(true)
                                .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                                .setAnimation(SwapdroidAnimation.POP)
                                .isCancellable(false)
                                .showCancelIcon(false)
                                .setIcon(R.drawable.ic_error_icon, SwapdroidIcon.Visible)  //ic_star_border_black_24dp
                                .OnNegativeClicked {
                                    //stay here
                                }.OnPositiveClicked {
                                    gotoSignup()
                                }
                                .build()
                        else -> {
                            showDialogNoInternet(this, it.getMessage()!!, "", R.drawable.ic_alert_icon)
                            logFirebaseEvents("apiResponse",it.getMessage()!!)

                        }
                    }
                }
            }
        })
    }

    private fun showLoadingDialog(show: Boolean) {
        if (show) mUtilLoader.startLoader(this) else mUtilLoader.stopLoader()
    }

    override fun onBackPressed() {
        goBack()
    }

    /**
     * go back to signup
     */
    private fun goBack() {
        val intent = Intent(this, CommonLoginActivity::class.java)
        this.startActivity(intent)
        this@ForgotPasswordActivity.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
    }

    /**
     * navigate to signup
     */
    private fun gotoSignup() {
        var email = viewModel.email?.get() as String
        addData(this@ForgotPasswordActivity, 0, "", "", "", email, 0, "", "", false, false, false, "", "", "", false, false, false, false,"")
        val intent = Intent(this, TermsAndConActivity::class.java)
        intent.putExtra(SOURCE, LOGIN)
        intent.putExtra(VIA, NORMAL)
        startActivity(intent)
        this@ForgotPasswordActivity.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    fun logFirebaseEvents(key: String, value: String){
        val params = Bundle()
        params.putString(key,value)
        firebaseAnalytics?.logEvent(TAG, params)
    }

}
