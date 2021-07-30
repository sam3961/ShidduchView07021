package shiddush.view.com.mmvsd.tutorials.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import shiddush.view.com.mmvsd.tutorials.onboarding.*
import shiddush.view.com.mmvsd.tutorials.reviews.*

class ReviewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> ReviewStep1Fragment()
            1 -> ReviewStep2Fragment()
            2 -> ReviewStep3Fragment()
            3 -> ReviewStep4Fragment()
            4 -> ReviewStep5Fragment()
            5 -> ReviewStep6Fragment()
            else -> ReviewStep1Fragment()
        }

    }

    override fun getCount(): Int {
        return 6
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when (position) {
            0 -> title = ""
            1 -> title = ""
            2 -> title = ""
            3 -> title = ""
            4 -> title = ""
            5 -> title = ""
        }
        return title
    }

}