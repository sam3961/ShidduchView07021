package shiddush.view.com.mmvsd.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlinx.android.synthetic.main.activity_on_boarding.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.ui.subscription.SubscriptionActivity
import shiddush.view.com.mmvsd.ui.waitingscreen.WaitingActivity
import shiddush.view.com.mmvsd.utill.*

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {

    private var viewPager: ViewPager? = null
    private var textViewContinue: TextView? = null
    private var dotsIndicator: WormDotsIndicator? = null
    private var source = "";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        source = intent.getStringExtra(SOURCE)!!

        initViews()
        clickListener()
        setPagerAdapter()
        setViewPagerListener()
    }

    private fun setViewPagerListener() {
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 7)
                    textViewContinue?.setText("Get Started!")
                else
                    textViewContinue?.setText("Continue")
            }

        })
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        textViewContinue = findViewById(R.id.textViewContinue)
        dotsIndicator = findViewById(R.id.dots_indicator)
    }

    private fun clickListener() {
        textViewContinue!!.setOnClickListener(this)
    }

    private fun setPagerAdapter() {
        viewPager!!.adapter = WelcomePagerAdapter(this, supportFragmentManager)
        dotsIndicator!!.setViewPager(viewPager!!)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.textViewContinue -> {
                if (viewPager?.currentItem == 7) {
                    if (source == SIGNUP_SUCCESS)
                        checkAndNavigate()
                    else
                        finish()
                } else
                    viewPager?.currentItem = viewPager?.currentItem!! + 1
            }
        }
    }

    //commenting subscription code
    private fun checkAndNavigate() {
//        if (AppInstance.userObj?.getSubscriptionDone()!!)
//            goToHome();
//        else
//            gotoSubscriptionActivity()

        goToHome();
    }

    private fun goToHome() {
        val intent = Intent(this, WaitingActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * navigate to SubscriptionActivity
     */
    private fun gotoSubscriptionActivity() {
        val intent = Intent(this, SubscriptionActivity::class.java)
        intent.putExtra(SOURCE, LOGIN)
        intent.putExtra(VIA, NORMAL)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    override fun onResume() {
        super.onResume()
        if (source == SHOW_TUTORIALS) {
            if (SocketCommunication.isSocketConnected()) {
                SocketCommunication.emitInScreenActivity(TUTORIAL_SCREEN)
            }
        }
    }
}