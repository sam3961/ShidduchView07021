package shiddush.view.com.mmvsd.ui.otherscreens


import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment


import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.FragmentRegistrationFailedBinding
import shiddush.view.com.mmvsd.utill.addData
import shiddush.view.com.mmvsd.utill.dpToPxs
import shiddush.view.com.mmvsd.utill.getBackImage80Size
import shiddush.view.com.mmvsd.utill.getFontSize

/**
 * Created by Sumit Kumar.
 * This screen for Locked Account User
 */
class LockedAccountFragment : Fragment() {

    lateinit var binding: FragmentRegistrationFailedBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the fragment_intro_notes for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_registration_failed, container, false)
        val myView: View = binding.root
        setTextSizes()
        onCLickListeners()
        return myView
    }

    private fun setTextSizes() {
        val size25 = getFontSize(activity!!, 25)
        val size18 = getFontSize(activity!!, 18)
        val size15 = getFontSize(activity!!, 15)
        val size13 = getFontSize(activity!!, 13)

        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        binding.trouble.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.instruction.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.supportEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size15)

        //set Background Image
        val bgImage = getBackImage80Size(activity!!)
        binding.llLocked.background = ContextCompat.getDrawable(activity!!, bgImage)

        try {
            val size20 = getFontSize(activity!!, 20)
            val ivHeightWidth = dpToPxs(size20.toInt())
            binding.backArrow.layoutParams.height = ivHeightWidth
            binding.backArrow.layoutParams.width = ivHeightWidth
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onCLickListeners() {
        //set user default info
        addData(activity!!,0, "", "", "", "", 0, "", "", false, false, false, "", "", "", false, false, false, false,"")

        binding.backView.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

}