package shiddush.view.com.mmvsd.ui.otherscreens


import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment


import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.FragmentWelcomeScreenBinding
import shiddush.view.com.mmvsd.tutorials.onboarding.OnBoardingActivity
import shiddush.view.com.mmvsd.ui.waitingscreen.WaitingActivity
import shiddush.view.com.mmvsd.utill.*

/**
 * Created by Sumit Kumar.
 * This is Welcome Screen after successfull Signup and passed Quiz
 */
class WelcomeFragment : Fragment() {

    lateinit var binding: FragmentWelcomeScreenBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the fragment_intro_notes for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome_screen, container, false)
        setTextSizes()
        onCLickListeners()
        return binding.root
    }

    private fun setTextSizes() {
        val size50 = getFontSize(activity!!, 50)
        val size25 = getFontSize(activity!!, 25)
        val size18 = getFontSize(activity!!, 18)
        val size13 = getFontSize(activity!!, 13)

        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        binding.btnLetsGo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvHowItWorks.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvTimerText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvPlusFiveText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.tvOne.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvPointOne.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.tvTwo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvPointTwo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.tvThree.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvPointThree.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        try {
            val btnHeight = dpToPxs(size50.toInt())
            binding.tvOne.layoutParams.height = btnHeight
            binding.tvOne.layoutParams.width = btnHeight
            binding.tvTwo.layoutParams.height = btnHeight
            binding.tvTwo.layoutParams.width = btnHeight
            binding.tvThree.layoutParams.height = btnHeight
            binding.tvThree.layoutParams.width = btnHeight
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val bg60Image = getBackImage60Size(activity!!)
            val bg40Image = getBackImage40Size(activity!!)
            binding.firstCardCon.background = ContextCompat.getDrawable(activity!!, bg60Image)
            binding.secondInnerCardCon.background = ContextCompat.getDrawable(activity!!, bg40Image)
            binding.thirdInnerCardCon.background = ContextCompat.getDrawable(activity!!, bg40Image)
            binding.forthInnerCardCon.background = ContextCompat.getDrawable(activity!!, bg40Image)
            binding.fifthCardCon.background = ContextCompat.getDrawable(activity!!, bg60Image)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun onCLickListeners() {

        binding.btnLetsGo.setOnClickListener {
            binding.btnLetsGo.isEnabled = false
            goToHome()
        }
    }

    private fun goToHome() {
        val intent = Intent(activity!!, WaitingActivity::class.java)
        intent.putExtra(SHOW_TUTORIALS,true)
        startActivity(intent)
        activity!!.finish()
        activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

}
