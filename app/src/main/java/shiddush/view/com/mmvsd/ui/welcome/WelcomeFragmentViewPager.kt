package shiddush.view.com.mmvsd.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import shiddush.view.com.mmvsd.R

class WelcomeFragmentViewPager : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_welcome_viewpager, container, false)
        val textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)
        val textViewSubTitle = view.findViewById<TextView>(R.id.textViewSubTitle)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        textViewTitle.text = arguments!!.getString("title")
        textViewSubTitle.text = arguments!!.getString("subtitle")
        imageView.setImageResource(arguments!!.getInt("image"))
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String?, desc: String?, image: Int): Fragment {
            val fragmentViewPager = WelcomeFragmentViewPager()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("subtitle", desc)
            bundle.putInt("image", image)
            fragmentViewPager.arguments = bundle
            return fragmentViewPager
        }
    }
}