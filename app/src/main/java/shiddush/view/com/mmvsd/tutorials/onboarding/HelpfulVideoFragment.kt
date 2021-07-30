package shiddush.view.com.mmvsd.tutorials.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_helpful_video.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.tutorials.NextClickListener


class HelpfulVideoFragment : Fragment() {
    lateinit var nextClickListener: NextClickListener
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the fragment_intro_notes for this fragment
        return inflater.inflate(R.layout.fragment_helpful_video, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        nextClickListener = activity as OnBoardingActivity
        inIt()
    }

    private fun inIt() {
        textView.setText(R.string.highlight_3)
        btn_next.setOnClickListener {
            nextClickListener.onNextClick(3)
        }
    }

}
