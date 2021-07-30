package shiddush.view.com.mmvsd.ui.welcome

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.ui.welcome.WelcomeFragmentViewPager.Companion.newInstance

class WelcomePagerAdapter(private val activity: Activity, fm: FragmentManager) : FragmentStatePagerAdapter(fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> {
                val title1 = "While you wait for the countdown, answer as many of the “Introduce yourself” questions as possible:"
                val subTitle1 = "The more questions you answer:\n1. The more accurate your match will be.\n2. The more likely you'll have a date."
                newInstance(title1, subTitle1, R.drawable.welcome_2)
            }
            2 -> {
                val title2 = "When the countdown reaches 00:00, you will be entering a matching algorithm designed to match you to your best match."
                val subTitle2 = "Do not leave the app during this time."
                newInstance(title2, subTitle2, R.drawable.welcome_3)
            }
            3 -> {
                val title3 = "Once Matched..."
                val subTitle3 = "You will have 60 seconds to accept the call. Dating Coaches Available."
                newInstance(title3, subTitle3, R.drawable.welcome_4)
            }
            4 -> {
                val title4 = "Respect your match & Complete the FULL date time"
                val subTitle4 = ""
                newInstance(title4, subTitle4, R.drawable.welcome_5)
            }
            5 -> {
                val title5 = "Review the date.\nIf it's a mutual Yes,\nWe'll notify you and help arrange the next date."
                val subTitle5 = ""
                newInstance(title5, subTitle5, R.drawable.welcome_6)
            }
            6 -> {
                val title6 = "Awesome! it's a mutual Yes";
                val subTitle6 = "Let's schedule the next date...2nd dates are 10 min., 3rd dates are 15 min. and so on...."
                newInstance(title6, subTitle6, R.drawable.welcome_7)
            }
            7 -> {
                val title7 = "Disconnected?\nDon’t worry! Use the rejoin button when you are both on the app at the same time."
                val subTitle7 = ""
                newInstance(title7, subTitle7, R.drawable.welcome_8)
            }
            0 -> {
                val title = "To go on a date - be online as the countdown ends."
                val subTitle = ""
                newInstance(title,
                        subTitle, R.drawable.welcome_1)
            }
            else -> {
                val title = "To go on a date - be online as the countdown ends."
                val subTitle = ""
                newInstance(title,
                        subTitle, R.drawable.welcome_1)
            }
        }
    }

    override fun getCount(): Int {
        return 8
    }

}