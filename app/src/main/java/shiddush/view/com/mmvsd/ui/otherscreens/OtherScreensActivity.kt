package shiddush.view.com.mmvsd.ui.otherscreens

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.radioplayer.RadioPlayer
import shiddush.view.com.mmvsd.ui.commonloginscreen.CommonLoginActivity
import shiddush.view.com.mmvsd.ui.waitingscreen.WaitingActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidColorConstants
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon

/**
 * Created by Sumit Kumar.
 */
class OtherScreensActivity : AppCompatActivity() {

    private var calledIntent = false
    private var source: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.purple_bar)
        }
        setContentView(R.layout.activity_other_screens)
        changeNavBack()

        try {
            val extras = intent.extras
            if (extras != null) {
                source = extras.getString(SOURCE, "")
            }

            // check and show fragments
            when (source) {
                CONTACT_US -> setFragment(ContactUsFragment(), CONTACT_US_FRAGMENT)
                PARTNERSHIP -> setFragment(PartnershipFragment(), PARTNERSHIP_FRAGMENT)
                ENDORSEMENT -> setFragment(EndorsementFragment(), ENDORSEMENT_FRAGMENT)
                SIGNUP_FAILED -> setFragment(LockedAccountFragment(), SIGNUP_FAILED_FRAGMENT)
                SIGNUP_SUCCESS -> setFragment(WelcomeFragment(), SIGNUP_SUCCESS_FRAGMENT)
                SIGNUP_SUCCESS_NO_TUTORIALS -> setFragment(WelcomeFragment(), SIGNUP_SUCCESS_FRAGMENT)
                SHOW_TUTORIALS -> setFragment(WelcomeFragment(), SIGNUP_SUCCESS_FRAGMENT)
                NO_MATCH_FOUND -> setFragment(NoMatchFragment(), NO_MATCH_FOUND_FRAGMENT)
                REVIEW_SUCCESS -> setFragment(ReviewSuccessFragment(), REVIEW_SUCCESS_FRAGMENT)
                REVIEW_FAILURE -> setFragment(ReviewFailureFragment(), REVIEW_FAILURE_FRAGMENT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setFragment(ContactUsFragment(), CONTACT_US_FRAGMENT)
        }

    }

    /**
     * to change navigation bar color
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
     * to set up fragments
     */
    private fun setFragment(fragment: Fragment, tag: String) {
        // Get the support fragment manager instance
        val manager = supportFragmentManager

        // Begin the fragment transition using support fragment manager
        val transaction = manager.beginTransaction()

        // Replace the fragment on container
        transaction.replace(R.id.other_screen_container, fragment, tag)
        transaction.addToBackStack(null)

        // Finishing the transition
        transaction.commit()
    }

    override fun onResume() {
        super.onResume()
        try {
            RadioPlayer.pauseRadio()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    /**
     * check and navigate
     */
    override fun onBackPressed() {
        try {
            when {
                source.equals(SIGNUP_FAILED) -> SwapdroidAlertDialog.Builder(this)
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
                        .showCancelIcon(false)
                        .setIcon(R.drawable.ic_leave_icon, SwapdroidIcon.Visible)  //ic_star_border_black_24dp
                        .OnNegativeClicked {
                            //do nothing
                        }
                        .OnPositiveClicked {
                            gotoCommonLogin()
                        }
                        .build()
                source.equals(SIGNUP_SUCCESS) -> {
                    //nothing to do
                }
                source.equals(NO_MATCH_FOUND) || source.equals(REVIEW_SUCCESS) || source.equals(REVIEW_FAILURE) -> gotoWaitingScreen(source)
                else -> {
                    justFinish()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            justFinish()
        }
    }

    /**
     * navigate to common login
     */
    private fun gotoCommonLogin() {
        if (!calledIntent) {
            calledIntent = true
            val intent = Intent(this, CommonLoginActivity::class.java)
            startActivity(intent)
            this@OtherScreensActivity.finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
        }
    }

    /**
     * navigate to waiting screen
     */
    private fun gotoWaitingScreen(source: String) {
        if (!calledIntent) {
            calledIntent = true
            val intent = Intent(this, WaitingActivity::class.java)
            if (source.equals(REVIEW_SUCCESS))
                intent.putExtra(VIA, REVIEW_SUCCESS)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            this@OtherScreensActivity.finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
        }
    }

    /**
     * just finish
     */
    private fun justFinish() {
        this@OtherScreensActivity.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
    }
}
