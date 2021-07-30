package shiddush.view.com.mmvsd.ui.signin

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.ui.forgotpassword.ForgotPasswordActivity
import shiddush.view.com.mmvsd.ui.tnc.TermsAndConActivity
import shiddush.view.com.mmvsd.utill.*

/**
 * Created by Sumit Kumar.
 */

class SignInViewModel : ViewModel() {

    var email = ObservableField<String>()
    var password = ObservableField<String>()

    init {
        email.set("")
        password.set("")
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

    /**
     * navigate to sign up
     */
    fun onSignUpClick(view: View){
        view.isEnabled = false
        val email = email.get()!!.trim()
        addData(view.context, 0, "", "", "", email, 0, "", "", false, false, false, "", "","", false, false, false,false,"")
        val intent = Intent(view.context, TermsAndConActivity::class.java)
        intent.putExtra(SOURCE,LOGIN)
        intent.putExtra(VIA, NORMAL)
        view.context.startActivity(intent)
        (view.context as Activity).finish()
        (view.context as Activity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

}
