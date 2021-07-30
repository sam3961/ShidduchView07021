package shiddush.view.com.mmvsd.tutorials.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import shiddush.view.com.mmvsd.tutorials.match.MatchStep1Fragment
import shiddush.view.com.mmvsd.tutorials.match.MatchStep2Fragment
import shiddush.view.com.mmvsd.tutorials.match.MatchStep3Fragment
import shiddush.view.com.mmvsd.tutorials.match.MatchStep4Fragment
import shiddush.view.com.mmvsd.tutorials.onboarding.*

class MatchPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> MatchStep1Fragment()
            1 -> MatchStep2Fragment()
            2 -> MatchStep3Fragment()
            3 -> MatchStep4Fragment()
            else -> MatchStep1Fragment()
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