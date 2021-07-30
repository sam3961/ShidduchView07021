package shiddush.view.com.mmvsd.ui.introvideo

import android.animation.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.*
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.analytics.FirebaseAnalytics
import com.jaychang.st.SimpleText
import com.smartlook.sdk.smartlook.Smartlook

import kotlinx.android.synthetic.main.dialog_reject_call.cancel_icon_click
import kotlinx.android.synthetic.main.dialog_reject_call.negativeBtn
import kotlinx.android.synthetic.main.dialog_reject_call.positiveBtn
import kotlinx.android.synthetic.main.dialog_reject_user.*
import kotlinx.android.synthetic.main.dialog_waiting_before_date.*
import kotlinx.android.synthetic.main.report_and_end_call_layout.*
import org.json.JSONObject
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityUserInfoBeforeCallBinding
import shiddush.view.com.mmvsd.model.chat.ReceivedMessageResponse
import shiddush.view.com.mmvsd.model.onlineOffline.OnlineOfflineResponse
import shiddush.view.com.mmvsd.model.videocall.MatchedUserDetailsRequest
import shiddush.view.com.mmvsd.model.videocall.ReGenerateTokenResponse
import shiddush.view.com.mmvsd.model.videocall.SocketCallResponse
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.socket.SocketCallBackListeners
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.ui.services.NetworkSchedulerService
import shiddush.view.com.mmvsd.ui.videocall.OpenTokConfig
import shiddush.view.com.mmvsd.ui.videocall.VideoCallActivity
import shiddush.view.com.mmvsd.ui.waitingscreen.WaitingActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.widget.loader.UtilLoader
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon
import java.io.IOException
import java.util.concurrent.TimeUnit

class UserInfoBeforeCallActivity : AppCompatActivity(), SocketCallBackListeners,
    NetworkSchedulerService.onNetworkConnectionListener {

    private var swingAnimation: Animation? = null
    private lateinit var shrinkGrowAnimation: ObjectAnimator
    lateinit var binding: ActivityUserInfoBeforeCallBinding
    private lateinit var viewModel: UserInfoBeforeCallViewModel
    private lateinit var mUtilLoader: UtilLoader
    private var screenLock: PowerManager.WakeLock? = null

    //list
    private lateinit var matchedAdapter: MatchedAnswersAdapter
    private var isMatchAcceptedTheCall: Boolean = false
    private var gotResponseFromSocket: Boolean = false
    private var isActionPerformed: Boolean = false
    private var isDialogShow = false
    private var buttonEnabled = false
    private var reasonRejectCall = ""

    //socket communication
    lateinit var mSocketCallBackListeners: SocketCallBackListeners

    //opentok
    private var TOKBOX_API_S = ""
    private var FROMID_S = ""
    private var TOID_S = ""
    private var COUNT_S = 0
    private var FROMUSER_SOCKET_ID_S = ""
    private var TOUSER_SOCKET_ID_S = ""
    private var FROMUSER_SESSION_ID_S = ""
    private var TOUSER_SESSION_ID_S = ""
    private var CALLDURATION_S: Long = 0  //600000
    private var TOKBOX_TOKEN_S = ""
    private var USER_CLICK_STATUS = ""
    private var isTimeout = false
    private var isAcceptEmit = false

    //timer
    private var timer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var timerWait: CountDownTimer? = null
    private var timeDuration: Long = 60000L //60 seconds
    lateinit var dialog: Dialog
    private var rejectUserDialog: Dialog? = null
    private var rejectCallReasonDialog: Dialog? = null
    lateinit var rejectDialog: Dialog
    var waitingDialog: Dialog? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private val TAG = UserInfoBeforeCallActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
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
        binding = DataBindingUtil.setContentView(this@UserInfoBeforeCallActivity, R.layout.activity_user_info_before_call)
        viewModel = UserInfoBeforeCallViewModel()
        binding.viewModel = viewModel
        mUtilLoader = UtilLoader(this)
        NetworkSchedulerService.registerForNetworkService(this,this)
        changeNavBack()
        setTextSizes()
        setMatchedAnswersList()
        onClickListener()
        setOPentokData()
        startCounter()
        socketCommunication()
        // setOnSocketCallBackListener(this)
        //checkAndCallGetOpenTokDataApi()
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
                window.navigationBarColor = ContextCompat.getColor(this@UserInfoBeforeCallActivity, R.color.colorPrimary)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to set all text sizes
     */
    @SuppressLint("SetTextI18n")
    private fun setTextSizes() {
        try {
            val size25 = getFontSize(this, 25)
            val size18 = getFontSize(this, 18)
            val size16 = getFontSize(this, 16)
            val size14 = getFontSize(this, 14)

            binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
            binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
            binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

            binding.tvTimerEnds.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
            binding.tvMatchedWith.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
            binding.tvUsername.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
            binding.tvCompatibilityRating.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size16)
            binding.tvRating.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
            binding.tvCommonAnswer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
            binding.tvMarriagePurpose.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size14)
            binding.tvEULAText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size14)
            binding.tvReject.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
            binding.tvAccept.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //set different colors
        try {
            val ppText = SimpleText.from(getString(R.string.agree_eula))
                    .first(getString(R.string.eula))
                    .bold()
                    .textColor(R.color.hebrew_purple)
                    .onClick(binding.tvEULAText) { text, range, tag ->
                        if (!isDialogShow) {
                            isDialogShow = true
                            showEULADialog()
                        }
                    }
            binding.tvEULAText.text = ppText
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //set all user data
        setImageSizes()
        setAllData()
    }

    /**
     * to set all image sizes
     */
    private fun setImageSizes() {
        try {
            //cardHeight
            val cardHeight = getPercentHeightOfDevice(this, 0.22F)
            val userImage = getPercentHeightOfDevice(this, 0.16F)
            val img25Height = getPercentHeightOfDevice(this, 0.03F)
            val img22Height = getPercentHeightOfDevice(this, 0.029F)
            //binding.secondCardCon.layoutParams.height = cardHeight
            //  binding.ivUserImage.layoutParams.height = userImage
            // binding.ivUserImage.layoutParams.width = userImage

            binding.ivGenderSign.layoutParams.height = img25Height
            binding.ivGenderSign.layoutParams.width = img25Height
            set5Rating(img22Height) //all stars with 22 dp unfilled

            if (OpenTokConfig.MATCHED_USER_RATING > 5) {
                set5Rating(img25Height) //all stars with 22 dp filled
            } else {
                when (OpenTokConfig.MATCHED_USER_RATING) {
                    1 -> {
                        binding.ivRatingOne.layoutParams.height = img25Height
                        binding.ivRatingOne.layoutParams.width = img25Height
                    }
                    2 -> {
                        binding.ivRatingOne.layoutParams.height = img25Height
                        binding.ivRatingOne.layoutParams.width = img25Height
                        binding.ivRatingTwo.layoutParams.height = img25Height
                        binding.ivRatingTwo.layoutParams.width = img25Height
                    }
                    3 -> {
                        binding.ivRatingOne.layoutParams.height = img25Height
                        binding.ivRatingOne.layoutParams.width = img25Height
                        binding.ivRatingTwo.layoutParams.height = img25Height
                        binding.ivRatingTwo.layoutParams.width = img25Height
                        binding.ivRatingThree.layoutParams.height = img25Height
                        binding.ivRatingThree.layoutParams.width = img25Height
                    }
                    4 -> {
                        binding.ivRatingOne.layoutParams.height = img25Height
                        binding.ivRatingOne.layoutParams.width = img25Height
                        binding.ivRatingTwo.layoutParams.height = img25Height
                        binding.ivRatingTwo.layoutParams.width = img25Height
                        binding.ivRatingThree.layoutParams.height = img25Height
                        binding.ivRatingThree.layoutParams.width = img25Height
                        binding.ivRatingFour.layoutParams.height = img25Height
                        binding.ivRatingFour.layoutParams.width = img25Height
                    }
                    5 -> {
                        set5Rating(img25Height) //all stars with 25 dp filled
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to set rating 5 image sizes
     */
    private fun set5Rating(size: Int) {
        try {
            binding.ivRatingOne.layoutParams.height = size
            binding.ivRatingOne.layoutParams.width = size
            binding.ivRatingTwo.layoutParams.height = size
            binding.ivRatingTwo.layoutParams.width = size
            binding.ivRatingThree.layoutParams.height = size
            binding.ivRatingThree.layoutParams.width = size
            binding.ivRatingFour.layoutParams.height = size
            binding.ivRatingFour.layoutParams.width = size
            binding.ivRatingFive.layoutParams.height = size
            binding.ivRatingFive.layoutParams.width = size
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to set all data
     */
    private fun setAllData() {
        //set data
        try {
            if (AppInstance.userObj?.getGender()!! == 1) { //Female
                binding.ivUserImage.setImageResource(R.drawable.ic_male)
                binding.ivGenderSign.setImageResource(R.drawable.ic_male_sign)
            } else {
                binding.ivUserImage.setImageResource(R.drawable.ic_female)
                binding.ivGenderSign.setImageResource(R.drawable.ic_female_sign)
            }

            val userName = if (OpenTokConfig.MATCHED_USER_AGE.trim().isNotEmpty()) {
                OpenTokConfig.MATCHED_USER_NAME
            } else {
                OpenTokConfig.MATCHED_USER_NAME
            }
            binding.tvUsername.text = userName

            if (OpenTokConfig.MATCHED_USER_RATING > 5) {
                set5RatingIcon(R.drawable.ic_fill_star)
            } else {
                binding.tvRating.text = "${OpenTokConfig.MATCHED_USER_RATING} / 5"
                set5RatingIcon(R.drawable.ic_unfil_star)
                when (OpenTokConfig.MATCHED_USER_RATING) {
                    1 -> binding.ivRatingOne.setImageResource(R.drawable.ic_fill_star)
                    2 -> {
                        binding.ivRatingOne.setImageResource(R.drawable.ic_fill_star)
                        binding.ivRatingTwo.setImageResource(R.drawable.ic_fill_star)
                    }
                    3 -> {
                        binding.ivRatingOne.setImageResource(R.drawable.ic_fill_star)
                        binding.ivRatingTwo.setImageResource(R.drawable.ic_fill_star)
                        binding.ivRatingThree.setImageResource(R.drawable.ic_fill_star)
                    }
                    4 -> {
                        binding.ivRatingOne.setImageResource(R.drawable.ic_fill_star)
                        binding.ivRatingTwo.setImageResource(R.drawable.ic_fill_star)
                        binding.ivRatingThree.setImageResource(R.drawable.ic_fill_star)
                        binding.ivRatingFour.setImageResource(R.drawable.ic_fill_star)
                    }
                    5 -> {
                        set5RatingIcon(R.drawable.ic_fill_star)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to set rating 5 image sizes
     */
    private fun set5RatingIcon(image: Int) {
        try {
            binding.ivRatingOne.setImageResource(image)
            binding.ivRatingTwo.setImageResource(image)
            binding.ivRatingThree.setImageResource(image)
            binding.ivRatingFour.setImageResource(image)
            binding.ivRatingFive.setImageResource(image)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * set list
     */
    @SuppressLint("SetTextI18n")
    private fun setMatchedAnswersList() {
        //To change navigation bar color
        try {
            binding.tvCommonAnswer.text = " ${OpenTokConfig.MATCH_USER_ANSWER_COUNT} " + getString(R.string.answer_in_common)
            val mLayoutManager = LinearLayoutManager(this@UserInfoBeforeCallActivity)
            binding.rvMatchedList.layoutManager = mLayoutManager
            matchedAdapter = MatchedAnswersAdapter(OpenTokConfig.MATCHED_USER_DATA, this@UserInfoBeforeCallActivity)
            binding.rvMatchedList.adapter = matchedAdapter
            matchedAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * all click listeners
     */
    private fun onClickListener() {
        binding.tvReject.alpha = 0.5F
        binding.tvAccept.alpha = 0.5F
        Handler().postDelayed({
            buttonEnabled = true
            binding.tvReject.alpha = 1F
            binding.tvAccept.alpha = 1F
        }, 2000)

        binding.tvReject.setOnClickListener {
            if (buttonEnabled) {
                //showRejectDialog()
                var fromId = intent.extras!!.getString(FROMID, "")
                var toId = intent.extras!!.getString(TOID, "")
                showRejectUserDialog(fromId, toId)
            }
        }

        binding.tvAccept.setOnClickListener {
            if (buttonEnabled) {
                binding.tvAccept.isEnabled = false
                binding.tvReject.isEnabled = false
                clearTimer()
                userClickAction(STATUS_ACCEPT)

                if (SocketCommunication.isSocketConnected()) {
                    try {
                        val fromId = intent.extras!!.getString(FROMID, "")
                        val toId = intent.extras!!.getString(TOID, "")
                        SocketCommunication.emitMobileLog(
                                "ACCEPT_REJECT_SCREEN_ACCEPT",
                                "User Accept the call",
                                "info",
                                "fromId:" + fromId + " toId: " + toId);
                    } catch (e: Exception) {
                        Log.e("EXPCETION_SOCKET", "Error Message: " + e.message);
                    }
                }
            }
        }
    }

    /**
     * to start counter for 60 seconds to wait other users response
     */
    private fun startWaitingCounter() {
        var name = "You Partner";
        try {
            timerWait = object : CountDownTimer(timeDuration, 1000) {
                override fun onFinish() {
                    clearTimer()
                    clearWaitingTimer()
                    userClickAction(STATUS_NORESPOND);
                    showLoadingDialog(false)
                    if (!OpenTokConfig.MATCHED_USER_NAME.isEmpty())
                        name = OpenTokConfig.MATCHED_USER_NAME

                    val message = name + " " + getString(R.string.did_not_accept_the_call)
                    showTimerEndDialog(getString(R.string.ooops), message)
                }

                override fun onTick(durationSeconds: Long) {
                    if (gotResponseFromSocket && isMatchAcceptedTheCall) {
                        if (!isActionPerformed) {
                            isActionPerformed = true
                            clearTimer()
                            clearWaitingTimer()
                            showLoadingDialog(false)
                            navigateForCall()
                        }
                    } else if (gotResponseFromSocket && !isMatchAcceptedTheCall) {
                        if (!isActionPerformed) {
                            isActionPerformed = true
                            clearTimer()
                            clearWaitingTimer()
                            showLoadingDialog(false)
                            if (!OpenTokConfig.MATCHED_USER_NAME.isEmpty())
                                name = OpenTokConfig.MATCHED_USER_NAME
                            val message = name + " " + getString(R.string.did_not_accept_the_call)
                            showTimerEndDialog(getString(R.string.ooops), message)
                        }
                    }
                }
            }
            timerWait!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to start counter for 60 seconds
     */
    private fun startCounter() {
        try {
            timer = object : CountDownTimer(timeDuration, 1) {
                override fun onFinish() {
                    updateText("00")
                    isTimeout = true
                    userClickAction(STATUS_TIMEOUT)
                    if (SocketCommunication.isSocketConnected()) {
                        try {
                            SocketCommunication.emitMobileLog(
                                    "ACCEPT_REJECT_SCREEN_TIMER",
                                    "Timer_Reach_at_zero",
                                    "info",
                                    "user not accept call within 60 second and timeout happen");
                        } catch (e: Exception) {
                            Log.e("EXPCETION_SOCKET", "Error Message: " + e.message);
                        }
                    }
                }

                override fun onTick(durationSeconds: Long) {
                    try {
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                                .toMinutes(durationSeconds))
                        val hms: String = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(durationSeconds),
                                TimeUnit.MILLISECONDS.toMinutes(durationSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(durationSeconds)),
                                TimeUnit.MILLISECONDS.toSeconds(durationSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationSeconds)));
                        updateText(hms.split(":")[2])
                        if (hms.split(":")[2].equals("30")) {
                            playRing()
                            showTimerDialog()
                        }
                        binding.textViewTimer?.text = hms.split(":")[2]

                        if (hms.split(":")[2].equals("45")) {
                            animateButtonAccept()
                            rightClickAnimation()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            timer!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun rightClickAnimation() {
        binding.imageViewRightClick.visibility = View.VISIBLE
        swingAnimation = AnimationUtils.loadAnimation(this, R.anim.swinging)
        binding.imageViewRightClick.startAnimation(swingAnimation)
    }

    private fun animateButtonAccept() {
        shrinkGrowAnimation = ObjectAnimator.ofPropertyValuesHolder(
                binding.tvAccept,
                PropertyValuesHolder.ofFloat("scaleX", 0.8f),
                PropertyValuesHolder.ofFloat("scaleY", 0.8f)
        )
        shrinkGrowAnimation.duration = 2000
        shrinkGrowAnimation.repeatMode = ValueAnimator.REVERSE
        shrinkGrowAnimation.repeatCount = ValueAnimator.INFINITE
        shrinkGrowAnimation.start()
    }

    private fun playRing() {
        try {
            stopMediaPlayer()
            mediaPlayer = MediaPlayer.create(this, R.raw.alert_accept_call);
            mediaPlayer?.start()
            mediaPlayer?.isLooping = true
        } catch (e: IOException) {
            Log.e("SWAPLOGRP", "initMediaPlayer() failed")
        }

    }

    /**
     * to update text with timer
     * @param time : time is seconds
     */
    private fun updateText(time: String) {
        try {
            val text = getString(R.string.have_accept_the_call).replace("$", time)
            val failText = SimpleText.from(text)
                    .first(getString(R.string.you_have))
                    .textColor(R.color.colorAccent)
                    .first(getString(R.string.seconds_to_accept_call))
                    .textColor(R.color.colorAccent)
            binding.tvTimerEnds.text = failText
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * ++ communicate with socket
     */
    private fun socketCommunication() {
        try {
            //creating socket callback
            setOnSocketCallBackListener(this)
            //updating callback of socket connection
            SocketCommunication.setCallBack(this@UserInfoBeforeCallActivity, mSocketCallBackListeners)
            SocketCommunication.isSocketConnected()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setOnSocketCallBackListener(mSocketCallBackListeners: SocketCallBackListeners) {
        this.mSocketCallBackListeners = mSocketCallBackListeners
    }

    //callback from socket
    override fun onMatchedResponse(data: SocketCallResponse) {
        Log.e("SWAPSOC", "onMatchedResponse = UIB4C")
    }

    override fun onUserInfoResponse(data: SocketCallResponse) {
        Log.e("SWAPSOC", "onUserInfoResponse = UIB4C")
        try {
            when (data.code) {
                ResponseCodes.Success -> try {
                    val matchFound = data.matchFound!!
                    if (matchFound) {
                        TOKBOX_API_S = if (data.tokboxApi!!.trim().isNotEmpty()) {
                            data.tokboxApi!!.trim()
                        } else {
                            TOKBOX_API_S
                        }
                        COUNT_S = if (data.count!! != 0) {
                            data.count!!
                        } else {
                            COUNT_S
                        }
                        CALLDURATION_S = if (data.time!! != 0L) {
                            data.time!!
                        } else {
                            CALLDURATION_S
                        }
                        TOKBOX_TOKEN_S = if (data.tokboxToken!!.trim().isNotEmpty()) {
                            data.tokboxToken!!.trim()
                        } else {
                            TOKBOX_TOKEN_S
                        }
                        if (data.fromId!! == AppInstance.userObj!!.getId()!!) {
                            FROMID_S = data.fromId!!
                            TOID_S = data.toId!!
                            FROMUSER_SESSION_ID_S = data.fromUserSessionId!!
                            TOUSER_SESSION_ID_S = data.toUserSessionId!!
                        } else {
                            FROMID_S = AppInstance.userObj!!.getId()!!
                            TOID_S = data.fromId!!
                            FROMUSER_SESSION_ID_S = data.toUserSessionId!!
                            TOUSER_SESSION_ID_S = data.fromUserSessionId!!
                        }
                        //set it to OpenTok Config
                        if (TOKBOX_API_S.trim().isNotEmpty()) {
                            OpenTokConfig.API_KEY = TOKBOX_API_S
                            OpenTokConfig.SESSION_ID = FROMUSER_SESSION_ID_S
                            OpenTokConfig.TOKEN = TOKBOX_TOKEN_S
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                ResponseCodes.ACCESS_TOKEN_EXPIRED -> {
                    //go for sorry no match found
                }
                else -> {
                    //failure response
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //failure response
        }
    }

    override fun onRefreshBestMatch(status: String) {

    }

    override fun onSocketConnected() {
        Log.e("SWAPSOC", "onSocketConnected = UIB4C")
        if (!isAcceptEmit && USER_CLICK_STATUS.equals(STATUS_ACCEPT)) {
            var fromId = intent.extras!!.getString(FROMID, "")
            var toId = intent.extras!!.getString(TOID, "")
            if (fromId!! != AppInstance.userObj!!.getId()!!) {
                toId = fromId
                fromId = AppInstance.userObj!!.getId()!!
            }
            SocketCommunication.acceptRejectCall(fromId, toId, STATUS_ACCEPT, reasonRejectCall)
        }

    }

    override fun onSocketDisconnected() {
        Log.e("SWAPSOC", "onSocketDisconnected = UIB4C")
    }

    override fun callAccepted(status: Boolean, tokboxToken: String, time: Long, matchFound: Boolean, count: Int,
                              fromsocketid: String, toSocketId: String, toUserSessionId: String, fromId: String,
                              toId: String, fromUserSessionId: String, tokboxApi: String, rejoin: Boolean) {
        Log.e("SWAPSOC", "callAccepted")
    }

    override fun callReconnectIsUserOnline(status: Boolean) {
    }

    override fun callDropWantToConnect(userId: String, friendId: String, firstName: String, friendFirstName: String, type: String) {
    }

    override fun connectedToCall(status: Boolean, tokboxToken: String, time: Long, matchFound: Boolean, count: Int,
                                 fromsocketid: String, toSocketId: String, toUserSessionId: String,
                                 fromId: String, toId: String, fromUserSessionId: String, tokboxApi: String, rejoin: Boolean) {
    }

    override fun friendOnline(arrayListUsers: MutableList<OnlineOfflineResponse>) {

    }

    override fun friendOffline(arrayListUsers: MutableList<OnlineOfflineResponse>) {
    }

    override fun bestMatches(arrayListUsers: MutableList<OnlineOfflineResponse>) {

    }

    override fun onNotifyEndCall(data: JSONObject) {

    }

    override fun onChatMessageReceive(data: ReceivedMessageResponse) {

    }


    //callback from socket for accept reject
    override fun onAcceptRejectResponse(data: SocketCallResponse) {
        try {
            Log.e("SWAPSOC", "onAcceptRejectResponse = UIB4C" + data.status)
            if (!gotResponseFromSocket) {
                gotResponseFromSocket = true
                when (data.status) {
                    STATUS_ACCEPT -> {
                        isMatchAcceptedTheCall = true
                        if (SocketCommunication.isSocketConnected()) {
                            try {
                                SocketCommunication.emitMobileLog(
                                        "ACCEPT_REJECT_SCREEN_ACCEPTED",
                                        "Other_User_Accept_Call",
                                        "info",
                                        "");
                            } catch (e: Exception) {
                                Log.e("EXPCETION_SOCKET", e.message!!);
                            }
                        }
                    }
                    STATUS_REJECT -> {
                        isMatchAcceptedTheCall = false
                        var name = ""
                        if (SocketCommunication.isSocketConnected()) {
                            try {
                                SocketCommunication.emitMobileLog(
                                        "ACCEPT_REJECT_SCREEN_REJECTED",
                                        "Other_User_Reject_Call",
                                        "info",
                                        "");
                            } catch (e: Exception) {
                                Log.e("EXPCETION_SOCKET", e.message!!);
                            }
                        }
                        if (!OpenTokConfig.MATCHED_USER_NAME.isEmpty())
                            name = OpenTokConfig.MATCHED_USER_NAME
                        val message = name + " " + getString(R.string.did_not_accept_call)
                        showTimerEndDialog(getString(R.string.ooops), message)
                        clearTimer()
                    }
                    STATUS_TIMEOUT -> {
                        isMatchAcceptedTheCall = false
                        if (SocketCommunication.isSocketConnected()) {
                            try {
                                SocketCommunication.emitMobileLog(
                                        "ACCEPT_REJECT_SCREEN_TIMEOUT",
                                        "Other_user_side_Timeout_the_Call",
                                        "info",
                                        "");
                            } catch (e: Exception) {
                                Log.e("EXPCETION_SOCKET", e.message!!);
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("onAcceptRejectResponse", "Error Message: " + e.message)
        }
    }

    //perform socket callback and click related tasks
    private fun userClickAction(status: String) {
        USER_CLICK_STATUS = status
        try {
            try {
                if (dialog != null) {
                    dialog.dismiss()
                    isDialogShow = false
                }
            } catch (e: Exception) {
            }
            var fromId = intent.extras!!.getString(FROMID, "")
            var toId = intent.extras!!.getString(TOID, "")
            var name = "You Partner";
            if (fromId!! != AppInstance.userObj!!.getId()!!) {
                toId = fromId
                fromId = AppInstance.userObj!!.getId()!!
            }
            when (status) {
                STATUS_ACCEPT -> {
                    if (SocketCommunication.isSocketConnected()) {
                        isAcceptEmit = true
                        SocketCommunication.acceptRejectCall(fromId, toId, status, reasonRejectCall)
                    }
                    if (gotResponseFromSocket && isMatchAcceptedTheCall) {
                        navigateForCall()
                    } else if (gotResponseFromSocket && !isMatchAcceptedTheCall) {
                        if (!OpenTokConfig.MATCHED_USER_NAME.isEmpty())
                            name = OpenTokConfig.MATCHED_USER_NAME
                        val message = name + " " + getString(R.string.did_not_accept_the_call)
                        showTimerEndDialog(getString(R.string.ooops), message)
                    } else {
                        //show loader to wait for response (60sec)
                        showLoadingDialog(true)
                        showWaitingDialog()
                        startWaitingCounter()
                    }
                }
                STATUS_REJECT -> {
                    //gotoWaitingScreen()
                    // rejectCallReasonDialog(getString(R.string.why_do_you_want_to_reject_this_call), fromId, toId, STATUS_REJECT)
                    showRejectUserDialog(fromId, toId)
                }
                STATUS_TIMEOUT -> {
                    //   showTimerEndDialog(getString(R.string.ooops), getString(R.string.you_missed_the_call))
                    if ((rejectUserDialog != null && rejectUserDialog?.isShowing!!) ||
                            (rejectCallReasonDialog != null && rejectCallReasonDialog?.isShowing!!))
                        Log.d("doNothing", "statuss" + status)
                    else
                        rejectCallReasonDialog(getString(R.string.why_do_you_missed_this_call), fromId, toId, status)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents(status, "Error Message: " + e.message)
        }
    }

    private fun rejectCallReasonDialog(message: String, fromId: String, toId: String, status: String) {
        try {
            rejectCallReasonDialog = Dialog(this, R.style.myDialog)
            rejectCallReasonDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                rejectCallReasonDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                rejectCallReasonDialog?.window!!.setLayout(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            rejectCallReasonDialog?.setCancelable(false)
            rejectCallReasonDialog?.setContentView(R.layout.report_and_end_call_layout)

            //getting resources
            val textViewTitle = rejectCallReasonDialog?.findViewById<View>(R.id.textViewtitle) as TextView
            val cancel_icon_click = rejectCallReasonDialog?.findViewById<View>(R.id.imageViewCancel) as ImageView
            val editTextMessage = rejectCallReasonDialog?.findViewById<View>(R.id.editTextmessage) as EditText

            //setTexts
            textViewTitle.text = message
            cancel_icon_click.visibility = View.GONE

            rejectCallReasonDialog?.textViewPositiveBtn?.setOnClickListener {
                reasonRejectCall = editTextMessage.text.toString()
                if (reasonRejectCall.isEmpty()) {
                    showDialogNoInternet(this, getString(R.string.please_enter_reason), "", R.drawable.ic_alert_icon)
                } else {
                    SocketCommunication.acceptRejectCall(fromId, toId, status, reasonRejectCall)
                    rejectCallReasonDialog?.dismiss()
                    hideSoftKeyboard(this, editTextMessage)
                    gotoWaitingScreen()
                }
            }

            //dialog show
            rejectCallReasonDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showRejectUserDialog(fromId: String, toId: String) {
        try {
            var status = STATUS_REJECT
            rejectUserDialog = Dialog(this, R.style.myDialog)
            rejectUserDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                rejectUserDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                rejectUserDialog?.window!!.setLayout(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            rejectUserDialog?.setCancelable(false)
            rejectUserDialog?.setContentView(R.layout.dialog_reject_user)


            rejectUserDialog?.textViewRejectOneTime?.setOnClickListener {
                rejectUserDialog?.textViewRejectOneTime?.background = ContextCompat.getDrawable(this, R.drawable.curved_shape_orange_filled)
                rejectUserDialog?.textViewRejectOneTime?.setTextColor(ContextCompat.getColor(this, R.color.white))

                rejectUserDialog?.textViewRejectPermanent?.background = ContextCompat.getDrawable(this, R.drawable.curved_shape_orange_border)
                rejectUserDialog?.textViewRejectPermanent?.setTextColor(ContextCompat.getColor(this, R.color.black))

                rejectUserDialog?.tvRejectMessage?.text = getString(R.string.not_receive_another_match_for_today)
                status = STATUS_REJECT
            }

            rejectUserDialog?.textViewRejectPermanent?.setOnClickListener {

                rejectUserDialog?.textViewRejectPermanent?.background = ContextCompat.getDrawable(this, R.drawable.curved_shape_orange_filled)
                rejectUserDialog?.textViewRejectPermanent?.setTextColor(ContextCompat.getColor(this, R.color.white))

                rejectUserDialog?.textViewRejectOneTime?.background = ContextCompat.getDrawable(this, R.drawable.curved_shape_orange_border)
                rejectUserDialog?.textViewRejectOneTime?.setTextColor(ContextCompat.getColor(this, R.color.black))

                rejectUserDialog?.tvRejectMessage?.text = getString(R.string.please_note_you_will_never_be_matched)

                status = STATUS_PREJECT
            }


            rejectUserDialog?.textViewYes?.setOnClickListener(View.OnClickListener {
                rejectCallReasonDialog(getString(R.string.why_do_you_want_to_reject_this_call), fromId, toId, status)
                rejectUserDialog?.dismiss()
                // gotoWaitingScreen()
            })

            rejectUserDialog?.close?.setOnClickListener(View.OnClickListener {
                rejectUserDialog?.dismiss()
                if (isTimeout)
                    rejectCallReasonDialog(getString(R.string.why_do_you_missed_this_call), fromId, toId, STATUS_TIMEOUT)

            })

            rejectUserDialog?.textViewNo?.setOnClickListener(View.OnClickListener {
                rejectUserDialog?.dismiss()
                if (isTimeout)
                    rejectCallReasonDialog(getString(R.string.why_do_you_missed_this_call), fromId, toId, STATUS_TIMEOUT)
            })


            rejectUserDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showWaitingDialog() {
        try {
            waitingDialog = Dialog(this, R.style.myDialog)
            waitingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                waitingDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                waitingDialog?.window!!.setLayout(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            waitingDialog?.setCancelable(false)
            waitingDialog?.setContentView(R.layout.dialog_waiting_before_date)

            val oa1 = ObjectAnimator.ofFloat(waitingDialog?.imageViewLogo, "scaleX", 1f, 0f);
            val oa2 = ObjectAnimator.ofFloat(waitingDialog?.imageViewLogo, "scaleX", 0f, 1f);

            oa1.setDuration(1500)
            oa2.setDuration(1500)

            oa1.repeatMode = ValueAnimator.REVERSE
            oa2.repeatMode = ValueAnimator.REVERSE

            oa1.repeatCount = ValueAnimator.INFINITE
            oa2.repeatCount = ValueAnimator.INFINITE

            oa1.setInterpolator(DecelerateInterpolator())
            oa2.setInterpolator(AccelerateDecelerateInterpolator())
            oa1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    waitingDialog?.imageViewLogo?.setImageResource(R.drawable.group)
                    oa2.start()
                }
            })

            oa1.start()

            waitingDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showLoadingDialog(show: Boolean) {
        try {
            if (show) mUtilLoader.startLoader(this@UserInfoBeforeCallActivity) else mUtilLoader.stopLoader()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * -- communicate with socket
     */

    /******************************** Start ************************************************
     * show EULA Dialog
     */
    private fun showEULADialog() {
        try {
            dialog = Dialog(this, R.style.myDialog)
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
            dialog.setContentView(R.layout.eula_dialog)

            //getting resources
            val rlWebView = dialog.findViewById<View>(R.id.rlWebView) as RelativeLayout
            val tv_hebru_text_t = dialog.findViewById<View>(R.id.tv_hebru_text_t) as TextView
            val tv_hebru_quotes = dialog.findViewById<View>(R.id.tv_hebru_quotes) as TextView
            val tv_hebru_text_dt = dialog.findViewById<View>(R.id.tv_hebru_text_dt) as TextView
            val cancel_icon = dialog.findViewById<View>(R.id.imageViewCancel) as ImageView
            val cancel_icon_click = dialog.findViewById<View>(R.id.cancel_icon_click) as ImageView
            val wvEULA = dialog.findViewById<View>(R.id.wvEULA) as WebView
            val skAILoader = dialog.findViewById<View>(R.id.skAILoader) as SpinKitView
            val tvNoSiteFound = dialog.findViewById<View>(R.id.tvNoSiteFound) as TextView


            //text sizes
            try {
                val size30 = getFontSize(this, 30)
                val size13 = getFontSize(this, 13)

                tv_hebru_text_t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
                tv_hebru_quotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
                tv_hebru_text_dt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
                tvNoSiteFound.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

                try {
                    val crossWH = getPercentHeightOfDevice(this@UserInfoBeforeCallActivity, 0.025F)
                    val crossHWH = getPercentHeightOfDevice(this@UserInfoBeforeCallActivity, 0.06F)
                    val dialogHeight = getPercentHeightOfDevice(this@UserInfoBeforeCallActivity, 0.50F)
                    cancel_icon.layoutParams.height = crossWH
                    cancel_icon.layoutParams.width = crossWH
                    cancel_icon_click.layoutParams.height = crossHWH
                    cancel_icon_click.layoutParams.width = crossHWH
                    rlWebView.layoutParams.height = dialogHeight
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                skAILoader.visibility = View.VISIBLE
                wvEULA.settings.javaScriptEnabled = true
                wvEULA.settings.loadWithOverviewMode = true
                wvEULA.settings.useWideViewPort = true
                wvEULA.webViewClient = MyWebViewClient(this, tvNoSiteFound, skAILoader)
                wvEULA.loadUrl(WebConstants.EULA_URL)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            cancel_icon_click.setOnClickListener {
                isDialogShow = false
                dialog.dismiss()
            }

            //dialog show
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class MyWebViewClient internal constructor(private val activity: Activity, private val tvNoSiteFound: TextView, private val skAILoader: SpinKitView) :
            WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
        ): Boolean {
            val url: String = request?.url.toString()
            view?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
        ) {
            try {
                tvNoSiteFound.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            try {
                skAILoader.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * show EULA Dialog
     ******************************* End ************************************************/

    /**
     * to get instance and enable weak lock
     */
    private fun showTimerEndDialog(title: String, message: String) {

        try {
            SwapdroidAlertDialog.Builder(this@UserInfoBeforeCallActivity)
                    .setTitle(title)
                    .setMessage(message)
                    .isMessageVisible(true)
                    .setNegativeBtnText(getString(R.string.ok))
                    .isNegativeVisible(false)
                    .setPositiveBtnText(getString(R.string.ok))
                    .isPositiveVisible(true)
                    .setAnimation(SwapdroidAnimation.POP)
                    .isCancellable(false)
                    .showCancelIcon(true)
                    .setIcon(R.drawable.ic_error_icon, SwapdroidIcon.Visible)
                    .OnPositiveClicked {
                        gotoWaitingScreen()
                    }
                    .OnCancelClicked {
                        gotoWaitingScreen()
                    }
                    .build()
        } catch (e: Exception) {
            e.printStackTrace()
            gotoWaitingScreen()
        }
    }


    private fun showRejectDialog() {
        try {
            rejectDialog = Dialog(this, R.style.myDialog)
            rejectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                rejectDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                rejectDialog.window!!.setLayout(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            rejectDialog.setCancelable(false)
            rejectDialog.setContentView(R.layout.dialog_reject_call)

            rejectDialog.positiveBtn.setOnClickListener(View.OnClickListener {
                //  binding.tvReject.isEnabled = false
                // binding.tvAccept.isEnabled = false
                //clearTimer()
                userClickAction(STATUS_REJECT)
                if (SocketCommunication.isSocketConnected()) {
                    try {
                        var fromId = intent.extras!!.getString(FROMID, "")
                        var toId = intent.extras!!.getString(TOID, "")
                        SocketCommunication.emitMobileLog(
                                "ACCEPT_REJECT_SCREEN_REJECT",
                                "User Reject the call",
                                "info",
                                "fromId:" + fromId + " toId: " + toId);
                    } catch (e: Exception) {
                        Log.e("EXPCETION_SOCKET", e.message!!);
                    }
                }
                rejectDialog.dismiss()
            })
            rejectDialog.negativeBtn.setOnClickListener(View.OnClickListener {
                rejectDialog.dismiss()
            })
            rejectDialog.cancel_icon_click.setOnClickListener(View.OnClickListener {
                rejectDialog.dismiss()
            })

            rejectDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showTimerDialog() {
        binding.frameLayoutProgressBar.visibility = View.VISIBLE
    }

    /**
     * to get instance and enable weak lock
     */
    override fun onResume() {
        super.onResume()

        //to set app instance
        try {
            AppInstance.userObj = getUserObject(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (SocketCommunication.isSocketConnected())
            SocketCommunication.emitInScreenActivity(ACCEPT_REJECT_SCREEN)

        try {
            @Suppress("DEPRECATION")
            screenLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, IntroAlertActivity::class.java.simpleName)
            screenLock!!.acquire()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to destroy weak lock
     */
    override fun onDestroy() {
        super.onDestroy()
        try {
            screenLock!!.release()
            stopMediaPlayer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * navigate to waiting screen
     */
    private fun gotoWaitingScreen() {
        clearTimer()
        clearAnimation()
        clearWaitingTimer()
        dismissWaitingDialog()
        stopMediaPlayer()
        val intent = Intent(this@UserInfoBeforeCallActivity, WaitingActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        this@UserInfoBeforeCallActivity.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
    }

    private fun clearAnimation() {
        swingAnimation?.reset()
        swingAnimation?.cancel()
        binding.imageViewRightClick.clearAnimation()
    }

    private fun stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    private fun dismissWaitingDialog() {
        if (waitingDialog != null && waitingDialog?.isShowing!!)
            waitingDialog?.dismiss()
    }

    private fun clearTimer() {
        try {
            //timer
            if (timer != null) {
                timer!!.cancel()
                timer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearWaitingTimer() {
        try {
            //waiting timer
            if (timerWait != null) {
                timerWait!!.cancel()
                timerWait = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //call api to get opentok credentials if not get from socket
    private fun checkAndCallGetOpenTokDataApi() {
        try {
            //call webservice
            if (OpenTokConfig.API_KEY.trim().isEmpty()) {
                if (isNetworkAvailable(this)) {
                    val request = MatchedUserDetailsRequest()
                    request.fromId = AppInstance.userObj!!.getId()!!
                    request.toId = intent.extras!!.getString(TOID, "")

                    val token = AppInstance.userObj!!.getToken()!!
                    val manager = NetworkManager()
                    manager.createApiRequest(ApiUtilities.getAPIService().reGenerateToken(token, request), object : ServiceListener<ReGenerateTokenResponse> {
                        override fun getServerResponse(response: ReGenerateTokenResponse, requestcode: Int) {
                            try {
                                when (response.code) {
                                    ResponseCodes.Success -> {
                                        OpenTokConfig.API_KEY = response.data!!.tokboxApi!!
                                        OpenTokConfig.SESSION_ID = response.data!!.fromUserSessionId!!
                                        OpenTokConfig.TOKEN = response.data!!.tokboxToken!!
                                    }
                                    ResponseCodes.ACCESS_TOKEN_EXPIRED -> //go for review then there from user will logout
                                        expireAccessToken(this@UserInfoBeforeCallActivity)
                                    else -> {

                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                logFirebaseEvents("checkAndCallGetOpenTokDataApi", "Error Message: " + e.message)
                            }
                        }

                        override fun getError(error: ErrorModel, requestcode: Int) {
                            logFirebaseEvents("checkAndCallGetOpenTokDataApi", "Error Message: " + error.error_message)

                        }
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * use to set opentok data
     */
    private fun setOPentokData() {
        try {
            FROMID_S = intent.extras!!.getString(FROMID, "")
            TOID_S = intent.extras!!.getString(TOID, "")
            FROMUSER_SOCKET_ID_S = intent.extras!!.getString(FROMUSER_SOCKET_ID, "")
            TOUSER_SOCKET_ID_S = intent.extras!!.getString(TOUSER_SOCKET_ID, "")
            COUNT_S = intent.extras!!.getInt(COUNT, 0)
            CALLDURATION_S = intent.extras!!.getLong(CALLDURATION, 0L)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to navigate for video call
     */
    private fun navigateForCall() {
        var fromId = ""
        var toId = ""
        var fromSocketId = ""
        var toSocketId = ""
        try {
            Smartlook.stopRecording()
            clearTimer()
            clearWaitingTimer()
            clearAnimation()
            dismissWaitingDialog()
            stopMediaPlayer()
            val from = FROMID_S
            val to = TOID_S
            val fromSocket = FROMUSER_SOCKET_ID_S
            val toSocket = TOUSER_SOCKET_ID_S
            if (from != AppInstance.userObj!!.getId()!!) {
                fromId = to
                toId = from
                fromSocketId = toSocket
                toSocketId = fromSocket
            } else {
                fromId = from
                toId = to
                fromSocketId = fromSocket
                toSocketId = toSocket
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            Log.e("SWAPTOVD", "UB4CA : TOID = $toId" + "  fromSocket  " + fromSocketId + " tosocket  " + toSocketId)
            val mIntent = Intent(this@UserInfoBeforeCallActivity, VideoCallActivity::class.java)
            mIntent.putExtra(FROMID, fromId)
            mIntent.putExtra(TOID, toId)
            mIntent.putExtra(COUNT, COUNT_S)
            mIntent.putExtra(CALLDURATION, CALLDURATION_S)
            mIntent.putExtra(FROMUSER_SOCKET_ID, fromSocketId)
            mIntent.putExtra(TOUSER_SOCKET_ID, toSocketId)
            mIntent.putExtra(REJOIN, intent.extras!!.getBoolean(REJOIN, false))
            startActivity(mIntent)
            this@UserInfoBeforeCallActivity.finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
        } catch (e: Exception) {
            e.printStackTrace()
            val intent = Intent(this@UserInfoBeforeCallActivity, OtherScreensActivity::class.java)
            intent.putExtra(SOURCE, NO_MATCH_FOUND)
            startActivity(intent)
            this@UserInfoBeforeCallActivity.finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
        }
    }

    override fun onBackPressed() {
        //nothing to do not permitted to go back
    }

    fun logFirebaseEvents(key: String, value: String?) {
        val params = Bundle()
        params.putString(key, value)
        firebaseAnalytics?.logEvent(TAG, params)
    }

    override fun onNetworkListener(isConnected: Boolean) {

    }

}
