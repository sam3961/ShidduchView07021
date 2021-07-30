package shiddush.view.com.mmvsd.tutorials.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_on_boarding.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.tutorials.NextClickListener
import shiddush.view.com.mmvsd.tutorials.adapters.OnBoardingPagerAdapter
import shiddush.view.com.mmvsd.ui.subscription.SubscriptionActivity
import shiddush.view.com.mmvsd.utill.AppInstance
import shiddush.view.com.mmvsd.utill.TUTORIAL_SCREEN
import shiddush.view.com.mmvsd.utill.WAITING_SCREEN

class OnBoardingActivity : AppCompatActivity(), NextClickListener {

    private lateinit var onBoardingPagerAdapter: OnBoardingPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        onBoardingPagerAdapter = OnBoardingPagerAdapter(supportFragmentManager)
        vp_onBoard.adapter = onBoardingPagerAdapter
    }


    override fun onNextClick(next: Int) {
        if (next != 5)
            vp_onBoard.currentItem = next
        else {
          /*  if(!AppInstance.userObj!!.getSubscriptionDone()!! && !AppInstance.userObj!!.getSubscriptionFree()!! ) {
                val intent = Intent(this, SubscriptionActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }else {
                finish()
            }*/
            finish()
            println("Step for next highlight")
        }
    }

    override fun onResume() {
        super.onResume()
        if (SocketCommunication.isSocketConnected()) {
            SocketCommunication.emitInScreenActivity(TUTORIAL_SCREEN)
        }

    }
}
