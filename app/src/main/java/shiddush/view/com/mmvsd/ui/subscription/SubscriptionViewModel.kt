package shiddush.view.com.mmvsd.ui.subscription

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.utill.SIGNUP_SUCCESS
import shiddush.view.com.mmvsd.utill.SOURCE

/**
 * Created by Sumit Kumar.
 */
class SubscriptionViewModel : ViewModel() {

    fun openOtherScreen(activity: Activity) {
        val intent = Intent(activity, OtherScreensActivity::class.java)
        intent.putExtra(SOURCE, SIGNUP_SUCCESS)
        activity.startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }
}
