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
import shiddush.view.com.mmvsd.databinding.FragmentPartnershipBinding
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.utill.*

/**
 * Created by Sumit Kumar.
 */
class PartnershipFragment : Fragment() {

    lateinit var binding: FragmentPartnershipBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the fragment_intro_notes for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_partnership, container, false)
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

        binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        binding.trouble.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.instruction.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.supportEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size15)

        binding.tvContactus.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvEndorsements.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvPartnership.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        //set Background Image
        val bgImage = getBackImage80Size(activity!!)
        binding.partnershipLayout.background = ContextCompat.getDrawable(activity!!, bgImage)

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

        binding.backView.setOnClickListener {
            activity!!.onBackPressed()
        }

        binding.tvContactus.setOnClickListener {
            fragmentCall(ContactUsFragment(), "CONTACT_US_FRAGMENT")
        }

        binding.tvPartnership.setOnClickListener {
            //nothing to do
        }

        binding.tvEndorsements.setOnClickListener {
            fragmentCall(EndorsementFragment(), "ENDORSEMENT_FRAGMENT")
        }
    }

    fun fragmentCall(fragment: Fragment, tag: String) {
        val manager = fragmentManager
        val transaction = manager!!.beginTransaction()
        transaction.replace(R.id.other_screen_container, fragment, tag)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (SocketCommunication.isSocketConnected()) {
            SocketCommunication.emitInScreenActivity(PARTNERSHIPS_SCREEN)
        }

    }

}
