package shiddush.view.com.mmvsd.ui.otherscreens


import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

import com.jaychang.st.SimpleText

import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.FragmentReviewFailureBinding
import shiddush.view.com.mmvsd.utill.dpToPxs
import shiddush.view.com.mmvsd.utill.getFontSize
import shiddush.view.com.mmvsd.utill.getPercentHeightOfDevice

/**
 * Created by Sumit Kumar.
 * This screen shows after Negative review
 */
class ReviewFailureFragment : Fragment() {

    lateinit var binding: FragmentReviewFailureBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the fragment_intro_notes for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review_failure, container, false)
        val myView: View = binding.root
        setTextSizes()
        onCLickListeners()
        return myView
    }

    private fun setTextSizes() {
        val size25 = getFontSize(activity!!, 25)
        val size18 = getFontSize(activity!!, 18)
        val size13 = getFontSize(activity!!, 13)

        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        binding.trouble.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.instruction.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.returnToHomeBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)

        try {
            val size20 = getFontSize(activity!!, 20)
            val ivHeightWidth = dpToPxs(size20.toInt())
            binding.backArrow.layoutParams.height = ivHeightWidth
            binding.backArrow.layoutParams.width = ivHeightWidth

            val imgHeight = getPercentHeightOfDevice(activity!!, 0.08F)
            binding.returnToHome.layoutParams.height = imgHeight
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try{
            val failText = SimpleText.from(getString(R.string.negative_review))
                    .first(getString(R.string.next_time))
                    .textColor(R.color.colorDarkOrange)
                    .first(getString(R.string.train_the_ai_ques))
                    .bold()
                    .textColor(R.color.colorDarkOrange)
            binding.instruction.text = failText
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun onCLickListeners() {
        binding.backView.setOnClickListener {
            activity!!.onBackPressed()
        }

        binding.returnToHomeBtn.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

}
