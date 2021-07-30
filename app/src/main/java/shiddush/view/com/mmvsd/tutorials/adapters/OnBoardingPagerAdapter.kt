package shiddush.view.com.mmvsd.tutorials.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import shiddush.view.com.mmvsd.tutorials.onboarding.*

class OnBoardingPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> TimerFragment()
            1 -> QuestionFragment()
            2 -> HelpfulVideoFragment()
            3 -> SuggestionFragment()
            else -> TimerFragment()
        }

    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when (position) {
            0 -> title = ""
            1 -> title = ""
            2 -> title = ""
            3 -> title = ""
        }
        return title
    }

}