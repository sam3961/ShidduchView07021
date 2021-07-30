package shiddush.view.com.mmvsd.ui.waitingscreen

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.ui.introvideo.UserInfoBeforeCallActivity
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.utill.*

/**
 * Created by Sumit Kumar.
 */
class WaitingViewModel : ViewModel() {

    var pauseForNextScreen = ObservableField<Boolean>()

    init {
        pauseForNextScreen.set(false)
    }

    fun setPauseCommunicator(status: Boolean) {
        pauseForNextScreen.set(status)
    }

    fun getPauseCommunicator(): Boolean {
        return pauseForNextScreen.get()!!
    }

    fun onContactUsClick(view: View) {
        openOtherScreen(view, CONTACT_US)
    }

    fun onPartnershipClick(view: View) {
        openOtherScreen(view, PARTNERSHIP)
    }

    fun onEndorsementsClick(view: View) {
        openOtherScreen(view, ENDORSEMENT)
    }

    fun openOtherScreen(view: View, name: String) {
        pauseForNextScreen.set(true)
        val intent = Intent(view.context, OtherScreensActivity::class.java)
       // val intent = Intent(view.context, UserInfoBeforeCallActivity::class.java)
        intent.putExtra(SOURCE, name)
        intent.putExtra(SOURCE_WAITING, true)
        view.context.startActivity(intent)
        //(view.context as Activity).finish()
        (view.context as Activity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }


}
