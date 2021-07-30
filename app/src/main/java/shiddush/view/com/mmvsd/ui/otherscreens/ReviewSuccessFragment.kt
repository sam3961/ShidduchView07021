package shiddush.view.com.mmvsd.ui.otherscreens


import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

import com.jaychang.st.SimpleText

import kotlinx.android.synthetic.main.dialog_rating.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.FragmentReviewSuccessBinding
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.utill.*


/**
 * Created by Sumit Kumar.
 * This screen shows after positive review
 */
class ReviewSuccessFragment : Fragment() {

    lateinit var binding: FragmentReviewSuccessBinding
    var browser: WebView? = null
    var dialog: Dialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the fragment_intro_notes for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_review_success, container, false)
        val myView: View = binding.root
        setTextSizes()
        onCLickListeners()
        showSubscriptionDialog()


        dialog = Dialog(activity!!, android.R.style.Theme_Translucent_NoTitleBar)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.activity_html_review)
        val window = dialog!!.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        dialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        //dialog!!.show()

        if (getPrefString(requireActivity(), PreferenceConnector.REVIEW_YES_NO) == "Yes") {
            showDialogContactingUser()
        }


        var email_address = ""
        var phone = ""
        var fromid = ""
        var user_name = ""
        var session_id = ""
        var toid = ""
        var count = 0
        try {
            if (AppInstance.userObj!!.getEmail() != null)
                email_address = AppInstance.userObj!!.getEmail()!!
            if (AppInstance.userObj!!.getPhone() != null)
                phone = AppInstance.userObj!!.getPhone()!!
            if (AppInstance.userObj!!.getId() != null)
                fromid = AppInstance.userObj!!.getId()!!
            if (AppInstance.userObj!!.getFirstName() != null)
                user_name = AppInstance.userObj!!.getFirstName()!!
            session_id = getPrefString(activity!!, PreferenceConnector.SESSION_ID)
            toid = getPrefString(activity!!, PreferenceConnector.MATCH_USER_ID)
            count = getPrefInt(activity!!, PreferenceConnector.VIDEO_CALL_COUNT)
        } catch (e: Exception) {
            Log.e("DataNotFound", e.message!!);
        }

        if (SocketCommunication.isSocketConnected()) {
            try {
                SocketCommunication.emitMobileLog(
                    "SCHEDULE_FORM",
                    "",
                    "success",
                    "email_address:" + email_address + ", session_id:" + session_id + ", toid:" + toid
                            + " , fromid:" + fromid + " ,count:" + count + ", user_name:" + user_name + " ,phone:" + phone
                )
            } catch (e: Exception) {
                Log.e("EXPCETION_SOCKET", e.message!!);
            }
        }


        browser = dialog!!.findViewById<View>(R.id.webChart) as WebView
        browser!!.clearCache(true)
        browser!!.settings.setDomStorageEnabled(true)

        browser!!.loadUrl("https://review.shidduchview.com/chatzapier/index.php?os=android&user_id=" + fromid + "&session_id=" + session_id)
        browser!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val js =
                    "javascript:loadData('$email_address','$session_id','$toid','$fromid','$count','$user_name','$phone')"
                // browser!!.loadUrl("javascript:document.getElementById('email_address').value = '"+email_address+"';document.getElementById('$session_id').value='"+session_id+"';document.getElementById('$toid').value='"+toid+"';document.getElementById('$fromid').value='"+fromid+"';document.getElementById('$count').value='"+count+"';document.getElementById('$user_name').value='"+user_name+"';document.getElementById('$phone').value='"+phone+"';");

                println(js)
                browser!!.loadUrl(js)
            }
        }
        browser!!.settings.javaScriptEnabled = true
        browser!!.addJavascriptInterface(WebViewJavaScriptInterface(activity!!), "Android")

        return myView
    }

    inner class WebViewJavaScriptInterface /*
         * Need a reference to the context in order to sent a post message
         */(private val context: Context) {
        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        fun loadResult(result: Boolean) {
            println("loader worked")
            dialog!!.dismiss()
        }

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

        try {
            val failText = SimpleText.from(getString(R.string.positive_review))
                .first(getString(R.string.if_person_say))
                .textColor(R.color.colorDarkOrange)
                .first(getString(R.string.person_yes))
                .bold()
                .textColor(R.color.colorDarkOrange)
                .first(getString(R.string.next_time))
                .textColor(R.color.colorDarkOrange)
                .first(getString(R.string.additional_five_min))
                .bold()
                .textColor(R.color.colorDarkOrange)
            binding.instruction.text = failText
        } catch (e: Exception) {
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

    private fun showSubscriptionDialog() {
        try {
            val dialog = Dialog(activity!!, R.style.myDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                dialog.window!!.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_rating)

            dialog.textViewRateNow.setOnClickListener(View.OnClickListener {
                try {
                    val uri: Uri =
                        Uri.parse("market://details?id=" + activity!!.packageName.toString() + "")
                    val goMarket = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(goMarket)
                } catch (e: ActivityNotFoundException) {
                    val uri: Uri =
                        Uri.parse("https://play.google.com/store/apps/details?id=" + activity!!.packageName.toString() + "")
                    val goMarket = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(goMarket)
                }
                dialog.dismiss()
            })

            dialog.textViewNoThanks.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
            })

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        browser!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        browser!!.onResume()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (SocketCommunication.isSocketConnected()) {
            SocketCommunication.emitInScreenActivity(REVIEW_SCREEN)
        }

    }

    private fun showDialogContactingUser() {
        try {
            val donationDialog = Dialog(requireActivity(), R.style.myDialog)
            donationDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                donationDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                donationDialog!!.window!!.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            donationDialog!!.setCancelable(false)
            donationDialog!!.setContentView(R.layout.dialog_donation)

            //getting resources
            val textViewAnotherTime =
                donationDialog!!.findViewById<View>(R.id.textViewAnotherTime) as TextView
            val textViewDonate =
                donationDialog!!.findViewById<View>(R.id.textViewDonate) as TextView
            val cancel_icon_click =
                donationDialog!!.findViewById<View>(R.id.closeCallDialog) as ImageView


            cancel_icon_click.setOnClickListener {
                donationDialog!!.dismiss()
            }
            textViewAnotherTime.setOnClickListener {
                donationDialog!!.dismiss()
            }
            textViewDonate.setOnClickListener {
                openBrowser(DONATION_URL,requireActivity())
                donationDialog!!.dismiss()
            }

            //dialog show
            donationDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
