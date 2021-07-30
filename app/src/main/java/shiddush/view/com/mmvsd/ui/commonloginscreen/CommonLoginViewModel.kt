package shiddush.view.com.mmvsd.ui.commonloginscreen

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.facebook.FacebookCustomClass
import shiddush.view.com.mmvsd.ui.forgotpassword.ForgotPasswordActivity
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.ui.tnc.TermsAndConActivity
import shiddush.view.com.mmvsd.utill.*

/**
 * Created by Sumit Kumar.
 */
class CommonLoginViewModel : ViewModel() {
    var isAccepted: Boolean = false

    //facebook
    lateinit var mFacebookCustomClass: FacebookCustomClass
    var facebookData = MutableLiveData<FacebookCustomClass>()

    var email = ObservableField<String>()
    var password = ObservableField<String>()

    init {
        email.set("")
        password.set("")
    }



    fun onFBClick(view: View) {
        if (isNetworkAvailable(view.context)) {
            mFacebookCustomClass = FacebookCustomClass(view.context)
            facebookData.value = mFacebookCustomClass
            mFacebookCustomClass.facebookLogin()
        } else {
            showDialogNoInternet(view.context, view.context.getString(R.string.ooops), view.context.getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
        }
    }

    fun onTermsAndConditionClick(view: View) {
        if (isAccepted) {
            isAccepted = false
           // view.iv_check.setImageResource(R.drawable.ic_check_box)
        } else {
            isAccepted = true
            //view.iv_check.setImageResource(R.drawable.ic_check_box_selected)
        }
    }

    fun onSignInClick(view: View) {
        //forceCrash(view)
        view.isEnabled = false
        val intent = Intent(view.context, CommonLoginActivity::class.java)
        view.context.startActivity(intent)
        (view.context as Activity).finish()
        (view.context as Activity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    fun onSignUpClick(view: View) {
        view.isEnabled = false
        addData(view.context, 0, "", "", "", "", 0, "", "", false, false, false, "", "", "", false, false, false, false,"")
   //     addData(view.context, 0, "", "", "", "", 0, "", "", true, true, true, "", "", "", true, true, true, true)
        val intent = Intent(view.context, TermsAndConActivity::class.java)
        intent.putExtra("SOURCE", "COMMON_LOGIN")
        intent.putExtra("VIA", "normal")
        view.context.startActivity(intent)
        (view.context as Activity).finish()
        (view.context as Activity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    fun onContactUsClick(view: View) {
        openOtherScreen(view, "CONTACT_US")
    }

    fun onPartnershipClick(view: View) {
        openOtherScreen(view, "PARTNERSHIP")
    }

    fun onEndorsementsClick(view: View) {
        openOtherScreen(view, "ENDORSEMENT")
    }

    /**
     * navigate to other screen
     */
    fun openOtherScreen(view: View, name: String) {
        val intent = Intent(view.context, OtherScreensActivity::class.java)
        intent.putExtra("SOURCE", name)
        view.context.startActivity(intent)
        (view.context as Activity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * navigate to forgot password
     */
    fun onForgotPasswordClick(view: View){
        view.isEnabled = false
        val intent = Intent(view.context, ForgotPasswordActivity::class.java)
        view.context.startActivity(intent)
        (view.context as Activity).finish()
        (view.context as Activity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

}