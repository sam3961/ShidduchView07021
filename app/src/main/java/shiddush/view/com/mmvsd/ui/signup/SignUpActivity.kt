package shiddush.view.com.mmvsd.ui.signup

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment


import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivitySignUpBinding
import shiddush.view.com.mmvsd.ui.commonloginscreen.CommonLoginActivity
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.ui.signin.SignInActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidColorConstants
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon


/**
 * Created by Sumit Kumar.
 */
class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    private var dialogVisible: Boolean = false
    private var calledIntent: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        changeNavBack()

        try {
            if (AppInstance.userObj!!.getIsBlocked()!!) { // isBlocked true
                signUpSuccessOrFailed(SIGNUP_FAILED)
            } else if (!AppInstance.userObj!!.getIsSignUpPerformed()!!) {
                callFrag(SignUpOneFragment(), ONE_FRAGMENT)
            } else if (!AppInstance.userObj!!.getIsNormalQuizComplete()!!) {
                callFrag(SignUpTwoFragment(), TWO_FRAGMENT)
            } else if (!AppInstance.userObj!!.getIsBibleQuizComplete()!!) {
                callFrag(SignUpThreeFragment(), THREE_FRAGMENT)
            } else {
                callFrag(SignUpOneFragment(), ONE_FRAGMENT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callFrag(SignUpOneFragment(), ONE_FRAGMENT)
        }

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

    override fun onResume() {
        super.onResume()
        try {
            AppInstance.userObj = getUserObject(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * show fragments
     */
    private fun callFrag(fragment: Fragment, tag: String) {
        // Get the support fragment manager instance
        val manager = supportFragmentManager

        // Begin the fragment transition using support fragment manager
        val transaction = manager.beginTransaction()

        // Replace the fragment on container
        transaction.replace(R.id.signup_container, fragment, tag)
        transaction.addToBackStack(null)

        // Finishing the transition
        transaction.commit()
    }

    /**
     * show alert on press og back button
     */
    override fun onBackPressed() {
        if (!dialogVisible) {
            dialogVisible = true
            SwapdroidAlertDialog.Builder(this)
                    .setTitle(getString(R.string.sure_to_close_this))
                    .isMessageVisible(false)
                    .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                    .setNegativeBtnText(getString(R.string.no))
                    .isNegativeVisible(true)
                    .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                    .setPositiveBtnText(getString(R.string.yes))
                    .isPositiveVisible(true)
                    .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                    .setAnimation(SwapdroidAnimation.POP)
                    .isCancellable(false)
                    .showCancelIcon(true)
                    .setIcon(R.drawable.ic_leave_icon, SwapdroidIcon.Visible)  //ic_star_border_black_24dp
                    .OnNegativeClicked {
                        // nothing
                        dialogVisible = false
                    }.OnPositiveClicked {
                        dialogVisible = false
                        goBack()
                    }.OnCancelClicked {
                        // nothing
                        dialogVisible = false
                    }
                    .build()
        }
    }

    /**
     * navigate to signin or common login
     */
    private fun goBack() {
        if (!calledIntent) {
            calledIntent = true
            var source: String = ""
            try {
                val extras = intent.extras
                if (extras != null) {
                    source = extras.getString(SOURCE, "")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val intent = Intent(this, CommonLoginActivity::class.java)
            startActivity(intent)
            this@SignUpActivity.finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
        }
    }

    /**
     * navigate to other screens
     */
    private fun signUpSuccessOrFailed(tag: String) {
        val intent = Intent(this, OtherScreensActivity::class.java)
        intent.putExtra(SOURCE, tag)
        startActivity(intent)
        this@SignUpActivity.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

}
