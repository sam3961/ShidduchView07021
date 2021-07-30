package shiddush.view.com.mmvsd.ui.review

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.util.TypedValue
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson

import org.json.JSONObject
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityReviewBinding
import shiddush.view.com.mmvsd.model.chat.ReceivedMessageResponse
import shiddush.view.com.mmvsd.model.onlineOffline.OnlineOfflineResponse
import shiddush.view.com.mmvsd.model.review.AddReviewResponse
import shiddush.view.com.mmvsd.model.videocall.SocketCallResponse
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.rest.RequestParams
import shiddush.view.com.mmvsd.rest.RestClient
import shiddush.view.com.mmvsd.rest.RestClientScheduler
import shiddush.view.com.mmvsd.rest.RestMethod
import shiddush.view.com.mmvsd.socket.SocketCallBackListeners
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.ui.videocall.OpenTokConfig
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.widget.loader.UtilLoader

class ReviewActivity : AppCompatActivity(), RestClient.OnAsyncRequestComplete,
        RestClient.OnAsyncRequestError, RestClientScheduler.OnAsyncRequestComplete,
        RestClientScheduler.OnAsyncRequestError, SocketCallBackListeners {

    lateinit var binding: ActivityReviewBinding
    private lateinit var mUtilLoader: UtilLoader
    private lateinit var viewModel: ReviewViewModel

    private var dateEnjoyed: String = ""
    private var pleasantRating: Int = 0
    private var attractiveRating: Int = 0
    private var religiousRating: Int = 0
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private val TAG = ReviewActivity::class.java.simpleName

    //socket communication
    lateinit var mSocketCallBackListeners: SocketCallBackListeners


    override fun onCreate(savedInstanceState: Bundle?) {
        //strictModeEnable()
        super.onCreate(savedInstanceState)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = ContextCompat.getColor(this, R.color.purple_bar)
            }
            firebaseAnalytics = FirebaseAnalytics.getInstance(this)
            firebaseAnalytics!!.setUserId(getUserObject(this).getEmail());
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review)
        viewModel = ReviewViewModel()
        binding.viewModel = viewModel
        mUtilLoader = UtilLoader(this)
        changeNavBack()
        //setTextSizes()
        onClickListeners()
        checkForMatchUserName()
    }

    private fun checkForMatchUserName() {
        var matchName = getPrefString(this, PreferenceConnector.MATCH_USER_NAME)

        if (matchName.contains(" "))
            matchName = matchName.split(" ")[0]

        if (matchName.isEmpty()) {
            if (AppInstance.userObj?.getGender()!! == 1) //Female
                matchName = "him"
            else
                matchName = "her"
        }

        binding.tvEnjoyQuestion.text = "Would you like to go out with \n" + matchName + " again?"

    }


    private fun strictModeEnable() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()   // detectDiskReads, detectDiskWrites, detectNetwork
                .penaltyLog()
                .build());

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }

    /**
     * to change navigation bar color
     */
    private fun changeNavBack() {
        //To change navigation bar color
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to set all text sizes
     */
    private fun setTextSizes() {
        val size25 = getFontSize(this, 25)
        val size18 = getFontSize(this, 18)
        val size20 = getFontSize(this, 20)
        val size16 = getFontSize(this, 16)
        val size14 = getFontSize(this, 14)
        val size13 = getFontSize(this, 13)
        val size15 = getFontSize(this, 15)

        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        binding.tvEnjoyQuestion.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
        binding.tvEnjoyYes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size15)
        binding.tvEnjoyNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size15)

        binding.tvRatingQuestion.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)

        binding.tvAddNote.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
        binding.etAddNotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size14)

        binding.btnSubmit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)

        binding.tvPleasant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size16)
        binding.tvAttractive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size16)
        binding.tvReligious.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size16)

        binding.tvContactus.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvEndorsements.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvPartnership.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)


    }

    //on click of submit button
    private fun onSubmit() {
        try {
            hideVirtualKeyboard(this)
            if (isValid()) {
                addReviewServiceCall()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to check validations
     */
    private fun isValid(): Boolean {
        var isValid = false
        when {
            dateEnjoyed.isEmpty() -> {
                isValid = false
                showDialogNoInternet(this@ReviewActivity, getString(R.string.please_select_review), "", R.drawable.ic_alert_icon)
            }
            pleasantRating == 0 -> {
                isValid = false
                showDialogNoInternet(this@ReviewActivity, getString(R.string.please_give_rating_for_pleasant), "", R.drawable.ic_alert_icon)
            }
            attractiveRating == 0 -> {
                isValid = false
                showDialogNoInternet(this@ReviewActivity, getString(R.string.please_give_rating_for_attractive), "", R.drawable.ic_alert_icon)
            }
            religiousRating == 0 -> {
                isValid = false
                showDialogNoInternet(this@ReviewActivity, getString(R.string.please_give_rating_for_religious), "", R.drawable.ic_alert_icon)
            }
            binding.etAddNotes.text!!.trim().isEmpty() -> {
                binding.etAddNotes.error = getString(R.string.please_enter_something)
                binding.etAddNotes.requestFocus()
                isValid = false
            }
            else -> isValid = true
        }
        return isValid
    }

    /**
     * all click listeners
     */
    private fun onClickListeners() {
        if (OpenTokConfig.ISBLOCK) {
            onClickDateAgainNo()
        }

        binding.tvEnjoyYes.setOnClickListener {
            if (!OpenTokConfig.ISBLOCK) {
                onClickDateAgainYes()
            }
        }

        binding.tvEnjoyNo.setOnClickListener {
            if (!OpenTokConfig.ISBLOCK) {
                onClickDateAgainNo()
            }
        }

        binding.btnSubmit.setOnClickListener {
            onSubmit()
        }

        //Pleasant clicks
        binding.ivPl1.setOnClickListener {
            onClickPl1()
        }

        binding.ivPl2.setOnClickListener {
            onClickPl2()
        }

        binding.ivPl3.setOnClickListener {
            onClickPl3()
        }

        binding.ivPl4.setOnClickListener {
            onClickPl4()
        }

        binding.ivPl5.setOnClickListener {
            onClickPl5()
        }

        //Attractive clicks
        binding.ivAt1.setOnClickListener {
            onClickAt1()
        }

        binding.ivAt2.setOnClickListener {
            onClickAt2()
        }

        binding.ivAt3.setOnClickListener {
            onClickAt3()
        }

        binding.ivAt4.setOnClickListener {
            onClickAt4()
        }

        binding.ivAt5.setOnClickListener {
            onClickAt5()
        }

        //Religious clicks
        binding.ivRe1.setOnClickListener {
            onClickRe1()
        }

        binding.ivRe2.setOnClickListener {
            onClickRe2()
        }

        binding.ivRe3.setOnClickListener {
            onClickRe3()
        }

        binding.ivRe4.setOnClickListener {
            onClickRe4()
        }

        binding.ivRe5.setOnClickListener {
            onClickRe5()
        }

        //bottom content
        binding.tvContactus.setOnClickListener {
            openOtherScreen(CONTACT_US)
        }

        binding.tvPartnership.setOnClickListener {
            openOtherScreen(PARTNERSHIP)
        }

        binding.tvEndorsements.setOnClickListener {
            openOtherScreen(ENDORSEMENT)
        }

    }

    private fun emailIntent() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, getUserObject(this).getEmail())
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.request_live_shadchan))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun onClickDateAgainYes() {
        binding.tvEnjoyYes.background = ContextCompat.getDrawable(this, R.drawable.curved_shape_orange_filled)
        binding.tvEnjoyNo.background = ContextCompat.getDrawable(this, R.drawable.curved_shape_orange_border)
        binding.tvEnjoyYes.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.tvEnjoyNo.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGrayText))
        dateEnjoyed = "Yes"
    }

    private fun onClickDateAgainNo() {
        binding.tvEnjoyYes.background = ContextCompat.getDrawable(this, R.drawable.curved_shape_orange_border)
        binding.tvEnjoyNo.background = ContextCompat.getDrawable(this, R.drawable.curved_shape_orange_filled)
        binding.tvEnjoyYes.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGrayText))
        binding.tvEnjoyNo.setTextColor(ContextCompat.getColor(this, R.color.white))
        dateEnjoyed = "No"
    }

    //Pleasant clicks
    private fun onClickPl1() {
        binding.ivPl1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl2.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivPl3.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivPl4.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivPl5.setImageResource(R.drawable.ic_star_unfilled_icon)
        pleasantRating = 1
    }

    private fun onClickPl2() {
        binding.ivPl1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl3.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivPl4.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivPl5.setImageResource(R.drawable.ic_star_unfilled_icon)
        pleasantRating = 2
    }

    private fun onClickPl3() {
        binding.ivPl1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl3.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl4.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivPl5.setImageResource(R.drawable.ic_star_unfilled_icon)
        pleasantRating = 3
    }

    private fun onClickPl4() {
        binding.ivPl1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl3.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl4.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl5.setImageResource(R.drawable.ic_star_unfilled_icon)
        pleasantRating = 4
    }

    private fun onClickPl5() {
        binding.ivPl1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl3.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl4.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivPl5.setImageResource(R.drawable.ic_star_filled_icon)
        pleasantRating = 5
    }

    //Attractive clicks
    private fun onClickAt1() {
        binding.ivAt1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt2.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivAt3.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivAt4.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivAt5.setImageResource(R.drawable.ic_star_unfilled_icon)
        attractiveRating = 1
    }

    private fun onClickAt2() {
        binding.ivAt1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt3.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivAt4.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivAt5.setImageResource(R.drawable.ic_star_unfilled_icon)
        attractiveRating = 2
    }

    private fun onClickAt3() {
        binding.ivAt1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt3.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt4.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivAt5.setImageResource(R.drawable.ic_star_unfilled_icon)
        attractiveRating = 3
    }

    private fun onClickAt4() {
        binding.ivAt1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt3.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt4.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt5.setImageResource(R.drawable.ic_star_unfilled_icon)
        attractiveRating = 4
    }

    private fun onClickAt5() {
        binding.ivAt1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt3.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt4.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivAt5.setImageResource(R.drawable.ic_star_filled_icon)
        attractiveRating = 5
    }

    //Religious clicks
    private fun onClickRe1() {
        binding.ivRe1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe2.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivRe3.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivRe4.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivRe5.setImageResource(R.drawable.ic_star_unfilled_icon)
        religiousRating = 1
    }

    private fun onClickRe2() {
        binding.ivRe1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe3.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivRe4.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivRe5.setImageResource(R.drawable.ic_star_unfilled_icon)
        religiousRating = 2
    }

    private fun onClickRe3() {
        binding.ivRe1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe3.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe4.setImageResource(R.drawable.ic_star_unfilled_icon)
        binding.ivRe5.setImageResource(R.drawable.ic_star_unfilled_icon)
        religiousRating = 3
    }

    private fun onClickRe4() {
        binding.ivRe1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe3.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe4.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe5.setImageResource(R.drawable.ic_star_unfilled_icon)
        religiousRating = 4
    }

    private fun onClickRe5() {
        binding.ivRe1.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe2.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe3.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe4.setImageResource(R.drawable.ic_star_filled_icon)
        binding.ivRe5.setImageResource(R.drawable.ic_star_filled_icon)
        religiousRating = 5
    }

    //++Add Review Webservice
    private fun addReviewServiceCall() {
        try {
            if (isNetworkAvailable(this)) {
                mUtilLoader.startLoader(this)

                val token = AppInstance.userObj!!.getToken()!!

                val p = RequestParams();
                p.add("review_from", AppInstance.userObj!!.getId()!!)
                p.add("review_for", intent.extras!!.getString(TOID, ""))
                p.add("isInterested", dateEnjoyed.equals("Yes"))
                p.add("pleasant", pleasantRating)
                p.add("attractive", attractiveRating)
                p.add("religious", religiousRating)
                p.add("notes", binding.etAddNotes.text.toString())
                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.ADD_REVIEW_WS);
            } else {
                mUtilLoader.stopLoader()
                showDialogNoInternet(this@ReviewActivity, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("addReviewServiceCall", "Error Message: " + e.message)
            mUtilLoader.stopLoader()
            Log.e("SWAPLOG", e.message!!)
            showDialogNoInternet(this@ReviewActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
        }
    }
    //--Add Review Webservice

    override fun onResume() {
        super.onResume()
        //to set app instance
        try {
            AppInstance.isCallCut = false
            AppInstance.userObj = getUserObject(this)
            if (SocketCommunication.isSocketConnected()) {
                SocketCommunication.emitInScreenActivity(SUBMIT_REVIEW_SCREEN)
            } else {
                setOnSocketCallBackListener(this)
                SocketCommunication.connectSocket(this, mSocketCallBackListeners)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setOnSocketCallBackListener(mSocketCallBackListeners: SocketCallBackListeners) {
        this.mSocketCallBackListeners = mSocketCallBackListeners
    }

    //other screen intents
    fun openOtherScreen(name: String) {
        savePrefString(this, PreferenceConnector.REVIEW_YES_NO, dateEnjoyed)

        val intent = Intent(this@ReviewActivity, OtherScreensActivity::class.java)
        intent.putExtra(SOURCE, name)
        startActivity(intent)
        if (name == REVIEW_SUCCESS || name == REVIEW_FAILURE) {
            this@ReviewActivity.finish()
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    override fun onBackPressed() {
        showDialogNoInternet(this@ReviewActivity, getString(R.string.please_review_the_date), "", R.drawable.ic_alert_icon)
    }

    fun logFirebaseEvents(key: String, value: String?) {
        val params = Bundle()
        params.putString(key, value)
        firebaseAnalytics?.logEvent(TAG, params)
    }

    override fun asyncResponse(responseData: String?, label: String?, `object`: Any?) {

        val gson = Gson();
        when (label) {
            WebConstants.ADD_REVIEW_WS -> {
                try {
                    mUtilLoader.stopLoader()
                    val response = gson.fromJson(responseData, AddReviewResponse::class.java);
                    when (response.code) {
                        ResponseCodes.Success -> {
                            Log.e("SWAPLOG", response.message!!)
                            if (dateEnjoyed.equals("Yes")) {
                                openOtherScreen(REVIEW_SUCCESS)
                            } else {
                                postUserSaidNoServiceCall()
                                // openOtherScreen(REVIEW_FAILURE)
                            }
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> expireAccessToken(this@ReviewActivity)
                        else -> {
                            Log.e("SWAPLOG", response.message!!)
                            showDialogNoInternet(this@ReviewActivity, response.message!!, "", R.drawable.ic_alert_icon)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logFirebaseEvents("addReviewServiceCall", "Error Message: " + e.message)
                    mUtilLoader.stopLoader()
                    showDialogNoInternet(this@ReviewActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }
            }
            WebConstants.USER_SAID_NO_REVIEW_WS -> {
                try {
                    Log.e("USER_SAID_NO_REVIEW_WS", responseData!!)
                    mUtilLoader.stopLoader()
                    openOtherScreen(REVIEW_FAILURE)
                } catch (e: Exception) {
                    e.printStackTrace()
                    logFirebaseEvents("USER_SAID_NO_REVIEW_WS", "Error Message: " + e.message)
                    mUtilLoader.stopLoader()
                    showDialogNoInternet(this@ReviewActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }
            }

        }
    }


    override fun asyncError(responseData: ErrorModel?
                            , label: String?
                            , `object`: Any?) {

        println("ASYNC ERROR")
        when (label) {
            WebConstants.ADD_REVIEW_WS -> {
                mUtilLoader.stopLoader()
                logFirebaseEvents("addReviewServiceCall", "Error Message: " + responseData!!.error_message)
                Log.e("SWAPLOG", responseData!!.error_message)
                showDialogNoInternet(this@ReviewActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
            }
            WebConstants.USER_SAID_NO_REVIEW_WS -> {
                mUtilLoader.stopLoader()
                openOtherScreen(REVIEW_FAILURE)
                logFirebaseEvents("USER_SAID_NO_REVIEW_WS", "Error Message: " + responseData!!.error_message)
                Log.e("SWAPLOG", responseData!!.error_message)
                showDialogNoInternet(this@ReviewActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
            }
        }
    }

    private fun postUserSaidNoServiceCall() {
        try {
            if (isNetworkAvailable(this)) {
                mUtilLoader.startLoader(this)
                val token = AppInstance.userObj!!.getToken()!!
                val p = RequestParams();
                p.add("email_address", AppInstance.userObj!!.getEmail()!!)
                p.add("toid", getPrefString(this, PreferenceConnector.MATCH_USER_ID))
                p.add("fromid", AppInstance.userObj!!.getId())
                p.add("phone", AppInstance.userObj!!.getPhone()!!)
                p.add("session_id", getPrefString(this, PreferenceConnector.SESSION_ID))
                p.add("count", getPrefInt(this, PreferenceConnector.VIDEO_CALL_COUNT))
                p.add("choice", "NO")
                p.add("user_name", AppInstance.userObj!!.getFirstName()!!)
                val rest = RestClientScheduler(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.USER_SAID_NO_REVIEW_WS);
            } else {
                mUtilLoader.stopLoader()
                showDialogNoInternet(this@ReviewActivity, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("postUserSaidNo", "Error Message: " + e.message)
            mUtilLoader.stopLoader()
            Log.e("SWAPLOG", e.message!!)
            showDialogNoInternet(this@ReviewActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
        }

    }


    override fun onMatchedResponse(data: SocketCallResponse) {

    }

    override fun onUserInfoResponse(data: SocketCallResponse) {
    }

    override fun onAcceptRejectResponse(data: SocketCallResponse) {
    }

    override fun onRefreshBestMatch(status: String) {

    }

    override fun onSocketConnected() {
        SocketCommunication.emitInScreenActivity(SUBMIT_REVIEW_SCREEN)
    }

    override fun onSocketDisconnected() {
    }

    override fun callAccepted(status: Boolean, tokboxToken: String, time: Long, matchFound: Boolean, count: Int, fromsocketid: String, toSocketId: String, toUserSessionId: String, fromId: String, toId: String, fromUserSessionId: String, tokboxApi: String, rejoin: Boolean) {
    }

    override fun callReconnectIsUserOnline(status: Boolean) {
    }

    override fun callDropWantToConnect(userId: String, friendId: String, firstName: String, friendFirstName: String, type: String) {
    }

    override fun connectedToCall(status: Boolean, tokboxToken: String, time: Long, matchFound: Boolean, count: Int, fromsocketid: String, toSocketId: String, toUserSessionId: String, fromId: String, toId: String, fromUserSessionId: String, tokboxApi: String, rejoin: Boolean) {
    }

    override fun friendOnline(arrayListUsers: MutableList<OnlineOfflineResponse>
    ) {
    }

    override fun friendOffline(arrayListUsers: MutableList<OnlineOfflineResponse>
    ) {
    }

    override fun bestMatches(arrayListUsers: MutableList<OnlineOfflineResponse>) {

    }

    override fun onNotifyEndCall(data: JSONObject) {

    }

    override fun onChatMessageReceive(data: ReceivedMessageResponse) {

    }

}
