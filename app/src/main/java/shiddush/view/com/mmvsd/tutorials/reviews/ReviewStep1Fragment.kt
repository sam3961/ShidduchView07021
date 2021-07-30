package shiddush.view.com.mmvsd.tutorials.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_timer.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.tutorials.NextClickListener


class ReviewStep1Fragment : Fragment() {
    lateinit var nextClickListener: NextClickListener
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_review_step_1, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        nextClickListener = activity as ReviewActivity
        inIt()
    }

    private fun inIt() {
        textView.setText(R.string.highlight_review_step_1)
        btn_next.setOnClickListener {
            nextClickListener.onNextClick(1)
        }
    }

}
