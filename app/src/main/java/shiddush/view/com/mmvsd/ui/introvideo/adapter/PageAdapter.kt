package shiddush.view.com.mmvsd.ui.introvideo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import shiddush.view.com.mmvsd.ui.introvideo.fragment.PlaceholderFragment

open class PageAdapter internal constructor(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment =
                PlaceholderFragment.newInstance(position)

        override fun getCount(): Int {
            return 6
        }

    }
