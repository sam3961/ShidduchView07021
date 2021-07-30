package shiddush.view.com.mmvsd.ui.videocall

import android.Manifest
import android.actionsheet.demo.com.khoiron.actionsheetiosforandroid.ActionSheet
import android.actionsheet.demo.com.khoiron.actionsheetiosforandroid.Interface.ActionSheetCallBack
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.GLSurfaceView
import android.os.*
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.opentok.android.*
import com.smartlook.sdk.smartlook.Smartlook

import org.json.JSONObject
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityVideoCallBinding
import shiddush.view.com.mmvsd.model.MainModel
import shiddush.view.com.mmvsd.model.chat.ReceivedMessageResponse
import shiddush.view.com.mmvsd.model.onlineOffline.OnlineOfflineResponse
import shiddush.view.com.mmvsd.model.review.AddReviewResponse
import shiddush.view.com.mmvsd.model.videocall.*
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.rest.RequestParams
import shiddush.view.com.mmvsd.rest.RestClient
import shiddush.view.com.mmvsd.rest.RestMethod
import shiddush.view.com.mmvsd.socket.SocketCallBackListeners
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.ui.review.ReviewActivity
import shiddush.view.com.mmvsd.ui.services.NetworkSchedulerService
import shiddush.view.com.mmvsd.ui.waitingscreen.WaitingActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.utill.PreferenceConnector.Companion.REVIEW_TO_USER_ID
import shiddush.view.com.mmvsd.utill.networkTest.TrafficStatusService
import shiddush.view.com.mmvsd.widget.loader.UtilLoader
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidColorConstants
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon
import java.util.concurrent.TimeUnit;

/**
 * Created by Sumit Kumar.
 * this class is use to connect and disconnect video call
 * https://github.com/opentok/opentok-android-sdk-samples/tree/master/Basic-Video-Chat
 * mute /unmute : http://iamvijayakumar.blogspot.com/2012/07/android-mute-and-unmute-sound.html
 */

class VideoCallActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
        WebServiceCoordinator.Listener, Session.SessionListener, SubscriberKit.StreamListener, SubscriberKit.VideoListener,
        PublisherKit.PublisherListener, RestClient.OnAsyncRequestComplete, RestClient.OnAsyncRequestError, SocketCallBackListeners, SubscriberKit.SubscriberListener,
    NetworkSchedulerService.onNetworkConnectionListener {


    //
    private var isPause = false
    lateinit var binding: ActivityVideoCallBinding
    private var screenLock: PowerManager.WakeLock? = null
    private lateinit var mUtilLoader: UtilLoader

    // Suppressing this warning. mWebServiceCoordinator will get GarbageCollected if it is local.
    private var mWebServiceCoordinator: WebServiceCoordinator? = null

    private var mSession: Session? = null
    private var mPublisher: Publisher? = null
    private var mSubscriber: Subscriber? = null

    //timer
    private var timer: CountDownTimer? = null
    private var isTimerEnd: Boolean = false
    private var isAlertShown: Boolean = false
    private var isSessionDisconnected: Boolean = false
    private var isCallDrop: Boolean = false
    private var isTimerStart: Boolean = false
    private var isClickable: Boolean = false
    private var isNavigatingToReview = false

    //for menu options
    private val menuList by lazy { ArrayList<String>() }
    private var isEndCall: Boolean = false;
    private var isReportCall: Boolean = false;

    companion object {
        private val LOG_TAG = VideoCallActivity::class.java.simpleName
        private val RC_SETTINGS_SCREEN_PERM = 123
        private const val RC_VIDEO_APP_PERM = 124
    }

    //Video call response values from API
    private var FROMID_S = ""
    private var TOID_S = ""
    private var COUNT_S = 0
    private var CALLDURATION_S: Long = 0//300000
    private var CURRENT_DURATION: Long = 0//300000

    //waiting call timer
    private var waitingTimer: CountDownTimer? = null
    private var waitingTimeDuration: Long = 120000
    private var countStreamDestroyed = 0
    private var countStreamDropped = 0
    private var isVideoCallConnected: Boolean = false
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private val TAG = VideoCallActivity::class.java.simpleName
    private var matchedUserName = ""
    private var sessionId = ""
    private var toUserSokcetId = ""
    private var fromUserSocketId = ""
    private var apiKey = ""
    private var rejoin = false
    private var isCallSuccess = false
    private var isPublisherConnected = false
    private var isSessionConnected = false
    private var isSubscriberConnected = false
    private var rejoinYesOrNo = ""
    lateinit var mSocketCallBackListeners: SocketCallBackListeners
    private var callEndedDialog = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        //strictModeEnable()
        super.onCreate(savedInstanceState)
        socketCommunication();

        if (SocketCommunication.isSocketConnected()) {
            try {
                SocketCommunication.emitMobileLog(
                        "CALL_SCREEN_ONLOAD",
                        "User reach on video connect screen",
                        "info",
                        "");
            } catch (e: Exception) {
                Log.e("EXPCETION_SOCKET", e.message!!)
            }
        }
        try {

            firebaseAnalytics = FirebaseAnalytics.getInstance(this)
            firebaseAnalytics?.setUserId(getUserObject(this).getEmail())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_call)
        mUtilLoader = UtilLoader(this)
        NetworkSchedulerService.registerForNetworkService(this,this)
        OpenTokConfig.ISBLOCK = false
        changeNavBack()
        setTextSizes()
        waitingTimerView()
        setTimerVal(CALLDURATION_S)
        onCLickListeners()
        requestPermissions()
        openTokValues()

    }

    private fun openTokValues() {
        matchedUserName = OpenTokConfig.MATCHED_USER_NAME
        sessionId = OpenTokConfig.SESSION_ID
        apiKey = OpenTokConfig.API_KEY
        toUserSokcetId = intent.extras!!.getString(TOUSER_SOCKET_ID)!!
        fromUserSocketId = intent.extras!!.getString(FROMUSER_SOCKET_ID)!!
        rejoin = intent.extras!!.getBoolean(REJOIN)!!

        Log.d("OPENTOK_VAL", "API_KEY=" + apiKey + "  SESSION_ID=" + OpenTokConfig.SESSION_ID + "  TOKEN=" + OpenTokConfig.TOKEN)

        if (!rejoin)
            rejoinYesOrNo = "NO"
        else
            rejoinYesOrNo = "YES"

    }


    fun isLoading(res: Boolean) {
        binding.skLoader.visibility = if (res) View.VISIBLE else View.GONE;
    }


    /**
     * to change navigation background color
     */
    private fun changeNavBack() {
        //To change navigation bar color
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.navigationBarColor = ContextCompat.getColor(this, R.color.purple_call_back)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to set all text sizes
     */
    private fun setTextSizes() {
        val size30 = getFontSize(this, 30)
        binding.tvTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)

        menuList.add(getString(R.string.block))
        menuList.add(getString(R.string.report))
        menuList.add(getString(R.string.end_call))
    }

    //on click of hold button
    private fun onCLickListeners() {
        binding.ivHoldButton.setOnClickListener {
            showActionSheet()
        }
    }

    //show action sheet
    private fun showActionSheet() {
        ActionSheet(this@VideoCallActivity, menuList)
                .setTitle(getString(R.string.choose_an_option))
                .setCancelTitle(getString(R.string.cancel))
                .setColorTitleCancel(Color.parseColor("#f44336"))
                .setColorTitle(Color.parseColor("#B9B8B8"))
                .setColorData(Color.parseColor("#7802D2"))
                .create(object : ActionSheetCallBack {
                    override fun data(data: String, position: Int) {
                        when (data) {
                            getString(R.string.block) -> {
                                // Show block user view
                                blockUserAlarmDialog()
                            }
                            getString(R.string.report) -> {
                                // Show report user view
                                reportAndEndCallDialog(getString(R.string.why_do_you_want_to_report_this_user), false)

                            }
                            getString(R.string.end_call) -> {
                                // Show end call view
                                reportAndEndCallDialog(getString(R.string.why_do_you_want_to_end_this_call), true)
                            }
                        }
                    }
                })
    }

    /******************************** Start ************************************************
     * show confirmation dialog for report or end call
     */
    private fun reportAndEndCallDialog(titleText: String, forEndCall: Boolean) {
        try {
            val dialog = Dialog(this, R.style.myDialog)
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
            dialog.setContentView(R.layout.report_and_end_call_layout)

            //getting resources
            val rlBackLayout = dialog.findViewById<View>(R.id.rlBackLayout) as RelativeLayout
            val tv_hebru_text_t = dialog.findViewById<View>(R.id.tv_hebru_text_t) as TextView
            val tv_hebru_quotes = dialog.findViewById<View>(R.id.tv_hebru_quotes) as TextView
            val tv_hebru_text_dt = dialog.findViewById<View>(R.id.tv_hebru_text_dt) as TextView
            val title1 = dialog.findViewById<View>(R.id.textViewtitle) as TextView
            val message1 = dialog.findViewById<View>(R.id.editTextmessage) as EditText
            val cancel_icon = dialog.findViewById<View>(R.id.imageViewCancel) as ImageView
            val cancel_icon_click = dialog.findViewById<View>(R.id.cancel_icon_click) as ImageView
            val pBtn = dialog.findViewById<View>(R.id.textViewPositiveBtn) as TextView

            //setTexts
            title1.text = titleText

            //text sizes
            try {
                val size30 = getFontSize(this, 30)
                val size18 = getFontSize(this, 19)
                val size16 = getFontSize(this, 17)

                tv_hebru_text_t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
                tv_hebru_quotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
                tv_hebru_text_dt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
                title1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
                message1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size16)
                pBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size16)

                try {
                    val btnHeight = getPercentHeightOfDevice(this@VideoCallActivity, 0.045F)
                    val btnWidth = getPercentHeightOfDevice(this@VideoCallActivity, 0.16F)
                    val crossWH = getPercentHeightOfDevice(this@VideoCallActivity, 0.025F)
                    val crossHWH = getPercentHeightOfDevice(this@VideoCallActivity, 0.06F)
                    val msgHeight = getPercentHeightOfDevice(this@VideoCallActivity, 0.16F)

                    pBtn.layoutParams.height = btnHeight
                    pBtn.layoutParams.width = btnWidth

                    cancel_icon.layoutParams.height = crossWH
                    cancel_icon.layoutParams.width = crossWH

                    cancel_icon_click.layoutParams.height = crossHWH
                    cancel_icon_click.layoutParams.width = crossHWH

                    message1.layoutParams.height = msgHeight

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            rlBackLayout.setOnClickListener {
                hideSoftKeyboard(this@VideoCallActivity, message1)
            }

            cancel_icon_click.setOnClickListener {
                hideSoftKeyboard(this@VideoCallActivity, message1)
                dialog.dismiss()
            }

            pBtn.setOnClickListener {
                hideSoftKeyboard(this@VideoCallActivity, message1)
                if (message1.text.trim().isEmpty()) {
                    message1.setText("")
                    message1.hint = getString(R.string.enter_reason_here)
                    message1.setHintTextColor(ContextCompat.getColor(this@VideoCallActivity, R.color.colorRed))
                } else {
                    dialog.dismiss()
                    //stopNetworkStatusService()
                    endCallEmit()

                    isCallDrop = false
                    if (forEndCall) {
                        isEndCall = true;
                        isCallSuccess = true;
                        endCallApiCall(message1.text.toString())
                    } else {
                        isReportCall = true
                        reportUserApiCall(message1.text.toString())
                    }
                }
            }

            //dialog show
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * show confirmation dialog for report or end call
     ******************************* End ************************************************/

    //this function use to show alert dialog for confirmation of block user
    private fun blockUserAlarmDialog() {
        try {
            SwapdroidAlertDialog.Builder(this)
                    .setTitle(getString(R.string.are_you_sure_to_block))
                    .isMessageVisible(false)
                    .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                    .setNegativeBtnText(getString(R.string.no))
                    .isNegativeVisible(true)
                    .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                    .setPositiveBtnText(getString(R.string.yes))
                    .isPositiveVisible(true)
                    .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                    .setAnimation(SwapdroidAnimation.POP)
                    .isCancellable(false)
                    .showCancelIcon(false)
                    .setIcon(R.drawable.ic_alert_icon, SwapdroidIcon.Visible)
                    .OnNegativeClicked {
                        //do nothing
                    }
                    .OnPositiveClicked {
                        Log.e(LOG_TAG, "blockUserAlarmDialog OnPositiveClicked")
                        blockUserApiCall()
                    }
                    .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //this function returns value when call ends before 5 seconds
    private fun checkLessTime(): Boolean {
        var isFinished = false
        try {
            if (CURRENT_DURATION <= 5000) {
                isFinished = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isFinished
    }

    //if other person ends the call
    private fun endCallDialog(title: String, message: String) {
        callEndedDialog = true
        clearAllRecords()
        binding.tvTime.text = ""
        SwapdroidAlertDialog.Builder(this)
                .setTitle(title)
                .isMessageVisible(true)
                .setMessage(message)
                .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                .setNegativeBtnText(getString(R.string.ok))
                .isNegativeVisible(false)
                .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                .setPositiveBtnText(getString(R.string.ok))
                .isPositiveVisible(true)
                .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                .setAnimation(SwapdroidAnimation.POP)
                .isCancellable(false)
                .showCancelIcon(true)
                .setIcon(R.drawable.ic_error_icon, SwapdroidIcon.Visible)
                .OnPositiveClicked {
                    isAlertShown = false
                    Log.e(LOG_TAG, "timer endCallDialog")
                    Handler().postDelayed({
                        // gotoReview()
                        endCallApiCall(title)
                    }, 300)
                }
                .OnCancelClicked {
                    isAlertShown = false
                    Log.e(LOG_TAG, "timer endCallDialog")
                    Handler().postDelayed({
                        // gotoReview()
                        endCallApiCall(title)
                    }, 300)
                }
                .build()
    }

    private fun dropCallDialog(title: String, message: String) {
        callEndedDialog = true
        clearAllRecords()
        binding.tvTime.text = ""
        SwapdroidAlertDialog.Builder(this)
                .setTitle(title)
                .isMessageVisible(true)
                .setMessage(message)
                .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                .setNegativeBtnText(getString(R.string.ok))
                .isNegativeVisible(false)
                .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                .setPositiveBtnText(getString(R.string.ok))
                .isPositiveVisible(true)
                .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                .setAnimation(SwapdroidAnimation.POP)
                .isCancellable(false)
                .showCancelIcon(true)
                .setIcon(R.drawable.call_disconnected, SwapdroidIcon.Visible)
                .OnPositiveClicked {
                    isAlertShown = false
                    Log.e(LOG_TAG, "timer endCallDialog")
                    Handler().postDelayed({
                        // gotoReview()
                        endCallApiCall(title)
                    }, 300)
                }
                .OnCancelClicked {
                    isAlertShown = false
                    Log.e(LOG_TAG, "timer endCallDialog")
                    Handler().postDelayed({
                        // gotoReview()
                        endCallApiCall(title)
                    }, 300)
                }
                .build()
    }

    //show dialog after two minutes extended
    private fun showExtendedTwoMinDialog(message: String) {
        SwapdroidAlertDialog.Builder(this)
                .setTitle(getString(R.string.ooops))
                .isMessageVisible(true)
                .setMessage(message)
                .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                .setNegativeBtnText(getString(R.string.ok))
                .isNegativeVisible(false)
                .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                .setPositiveBtnText(getString(R.string.ok))
                .isPositiveVisible(true)
                .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                .setAnimation(SwapdroidAnimation.POP)
                .isCancellable(false)
                .setIcon(R.drawable.ic_error_icon, SwapdroidIcon.Visible)
                .OnPositiveClicked {
                    isAlertShown = false
                    navigateToWaiting()
                }
                .OnCancelClicked {
                    isAlertShown = false
                    navigateToWaiting()
                }
                .build()
    }

    /**
     * to start count down timer for provided duration
     * @param timeDuration : duration will be change according to conditions
     */
    fun startCounter(timeDuration: Long) {
        try {
            Log.e(LOG_TAG, "startCounter = $timeDuration")
            if (!isTimerStart) {
                isTimerStart = true
                timer = object : CountDownTimer(timeDuration, 1) {
                    override fun onFinish() {
                        Log.e(LOG_TAG, "timer onFinish")
                        //end the call
                        binding.tvTime.text = "00:00"
                        isCallDrop = false
                        isCallSuccess = true
                        isEndCall = true
                        stopNetworkStatusService()
                        endCallDialog(getString(R.string.date_ended), getString(R.string.time_is_ended))
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onTick(durationSeconds: Long) {
                        CURRENT_DURATION = durationSeconds
                        //tvDurationDayEnd.text = getDate(durationSeconds, "yyyy-MM-dd HH:mm:ss").split(" ")[1]
                        setTimerVal(durationSeconds)
                    }
                }
                timer!!.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set time value to text view
    private fun setTimerVal(timeDuration: Long) {
        try {
            if (timeDuration != 0L) {
                val hms: String = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeDuration),
                        TimeUnit.MILLISECONDS.toMinutes(timeDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDuration)),
                        TimeUnit.MILLISECONDS.toSeconds(timeDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDuration)))
                binding.tvTime.text = hms.split(":")[1] + ":" + hms.split(":")[2]
                if (binding.tvTime.text.toString().equals("00:03")) {
                    binding.textViewEndTime.visibility = View.VISIBLE
                }
                binding.textViewEndTime.text = binding.tvTime.text.toString()
            } else {
                binding.tvTime.text = ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //function for waiting timer
    private fun waitingTimerView() {
        try {
            if (waitingTimeDuration != 0L) {
                waitingTimer = object : CountDownTimer(waitingTimeDuration, 1) {
                    override fun onFinish() {
                        //after ends call api and navigate to waiting screen
                        if (!isVideoCallConnected) {
                            //end the call
                            isSessionDisconnected = true
                            isCallDrop = false
                            callDropAfterTwoMin()

                        }
                    }

                    override fun onTick(durationSeconds: Long) {
                        //tvDurationDayEnd.text = getDate(durationSeconds, "yyyy-MM-dd HH:mm:ss").split(" ")[1]
                        if (isVideoCallConnected) {
                            if (waitingTimer != null) {
                                waitingTimer!!.cancel()
                                waitingTimer = null
                            }
                        }
                    }
                }
                waitingTimer!!.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /*****************************************************
     * Start Video Call TokBox
     **************************************************/
    override fun onPause() {
        super.onPause()
        Log.e(LOG_TAG, "onPause")
        //update Audio Functioning
        try {
            isPause = true
            if (mPublisher != null && mSubscriber != null) {
                mPublisher!!.publishAudio = false
                mSubscriber!!.subscribeToAudio = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e(LOG_TAG, "onResume")
        isPause = false
        try {
            if (!isSessionDisconnected) {
                if (mSession != null) {
                    mSession!!.onResume()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //to set app instance
        try {
            AppInstance.userObj = getUserObject(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (SocketCommunication.isSocketConnected()) {
            SocketCommunication.emitInScreenActivity(VIDEO_CALL_SCREEN)
        }


        //update Audio Functioning
        try {
            if (mPublisher != null && mSubscriber != null) {
                if (!mPublisher!!.publishAudio)
                    mPublisher!!.publishAudio = true
                if (!mSubscriber!!.subscribeToAudio)
                    mSubscriber!!.subscribeToAudio = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        try {
            @Suppress("DEPRECATION")
            screenLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, LOG_TAG)
            screenLock!!.acquire()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        //opentok session resume

        try {
            FROMID_S = intent.extras!!.getString(FROMID, "")
            TOID_S = intent.extras!!.getString(TOID, "")
            OpenTokConfig.MATCH_USER_ID = TOID_S
            COUNT_S = intent.extras!!.getInt(COUNT, 0)
            CALLDURATION_S = intent.extras!!.getLong(CALLDURATION, 0L)
            savePrefString(this, REVIEW_TO_USER_ID, TOID_S)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!isCallDrop)
            isCallDrop = true

    }

    override fun onStop() {
        super.onStop()
        //opentok session pause
        try {
            if (!isSessionDisconnected) {
                if (mSession != null) {
                    mSession!!.onPause()
                }
            }
            if (!isNavigatingToReview)
                isCallDrop = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            screenLock?.release()
            clearAllRecords()
            NotificationManagerCompat.from(this).cancel(TrafficStatusService.NOTIFICATION_ID)
            stopNetworkStatusService()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopNetworkStatusService() {
         val intentService = Intent(this, TrafficStatusService::class.java)
         stopService(intentService)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.e(LOG_TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.e(LOG_TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size)
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show()
        }
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private fun requestPermissions() {
        try {
            val perms = arrayOf(Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            if (EasyPermissions.hasPermissions(this, *perms)) {
                if (SocketCommunication.isSocketConnected()) {
                    try {
                        SocketCommunication.emitMobileLog(
                                "CALL_SCREEN_CAMERA_PERMISSION",
                                "authorized by user",
                                "info",
                                "");
                        SocketCommunication.emitMobileLog(
                                "CALL_SCREEN_MICROPHONE_PERMISSION",
                                "authorized by user",
                                "info",
                                "");
                    } catch (e: Exception) {
                        Log.e("EXPCETION_SOCKET", e.message!!);
                    }
                }

                initializeSession(OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID, OpenTokConfig.TOKEN)
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, *perms)
                if (SocketCommunication.isSocketConnected()) {
                    try {
                        SocketCommunication.emitMobileLog(
                                "CALL_SCREEN_CAMERA_PERMISSION",
                                "denied by user",
                                "info",
                                "");
                        SocketCommunication.emitMobileLog(
                                "CALL_SCREEN_MICROPHONE_PERMISSION",
                                "denied by user",
                                "info",
                                "");
                    } catch (e: Exception) {
                        Log.e("EXPCETION_SOCKET", e.message!!);
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initializeSession(apiKey: String, sessionId: String, token: String) {
        try {
            Log.e("SWAPLOGCALL", "ApiKey: $apiKey SessionId: $sessionId Token: $token");
            println("ApiKey: $apiKey SessionId: $sessionId Token: $token");
            if (mSession == null) {
                mSession = Session.Builder(this, apiKey, sessionId).build()
                mSession!!.setSessionListener(this)
                mSession!!.connect(token)
                if (SocketCommunication.isSocketConnected()) {
                    try {
                        SocketCommunication.emitMobileLog(
                                "CALL_SCREEN_CONNECT_TO_OPENTOK",
                                "Connect to opentok success",
                                "info",
                                "fromSocketID: $fromUserSocketId, toSocketID: $toUserSokcetId ToSessionId: $sessionId token : $token");
                    } catch (e: Exception) {
                        Log.e("EXPCETION_SOCKET", e.message!!);
                    }
                }
            }
        } catch (e: Exception) {
            if (SocketCommunication.isSocketConnected()) {
                try {
                    SocketCommunication.emitMobileLog(
                            "CALL_SCREEN_CONNECT_TO_OPENTOK",
                            "Error happen for connect made to opentok session",
                            "info",
                            e.message!!);
                } catch (e: Exception) {
                    Log.e("EXPCETION_SOCKET", "Error Message: " + e.message);
                }
            }
            e.printStackTrace()
        }
    }

    /* Web Service Coordinator delegate methods */

    override fun onSessionConnectionDataReady(apiKey: String, sessionId: String, token: String) {
        try {
            Log.e(LOG_TAG, "ApiKey: $apiKey SessionId: $sessionId Token: $token")
            initializeSession(apiKey, sessionId, token)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onWebServiceCoordinatorError(error: Exception) {
        try {
            Log.e(LOG_TAG, "Web Service error: " + error.message)
            showToast(this, "Web Service error: " + error.message, Toast.LENGTH_SHORT)
            //finish();
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* Session Listener methods */

    //visible to himself
    //sessionlISTENER
    override fun onConnected(session: Session) {
        isSessionConnected = true
        try {
            Log.e(LOG_TAG, "onConnected: Connected to session: " + session.sessionId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (SocketCommunication.isSocketConnected()) {
                try {
                    SocketCommunication.emitMobileLog(
                            "CALL_SCREEN_SESSION_DID_CONNECT",
                            "Session_sessionDidConnect delegate called",
                            "info",
                            "");
                } catch (e: Exception) {
                    Log.e("EXPCETION_SOCKET", e.message!!);
                }
            }
            // initialize Publisher and set this object to listen to Publisher events
            if (mPublisher == null) {
                createPublisher();
                //my Session connected
                isSessionDisconnected = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (SocketCommunication.isSocketConnected()) {
                try {
                    SocketCommunication.emitMobileLog(
                            "CALL_SCREEN_SESSION_DID_CONNECT_PUBLISH_ERROR",
                            "Session_sessionDidConnect when try to publish getting error",
                            "info",
                            e.message!!);
                } catch (e: Exception) {
                    Log.e("EXPCETION_SOCKET", "Error Message: " + e.message);
                }
            }
        }
    }

    private fun createPublisher() {
        mPublisher = Publisher.Builder(this).build()
        mPublisher!!.setPublisherListener(this)
        mPublisher!!.publishAudio = true
        mPublisher!!.publishVideo = true
        mSession!!.publish(mPublisher);
        mPublisher!!.renderer.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL)
        binding.publisherContainer!!.addView(mPublisher!!.view)
        if (mPublisher!!.view is GLSurfaceView) {
            (mPublisher!!.view as GLSurfaceView).setZOrderOnTop(true)
        }
        if (SocketCommunication.isSocketConnected()) {
            try {
                SocketCommunication.emitMobileLog(
                        "CALL_SCREEN_SESSION_DID_CONNECT_PUBLISHER_CREATE",
                        "Publisher created",
                        "info",
                        "");
            } catch (e: Exception) {
                Log.e("EXPCETION_SOCKET", e.message!!);
            }
        }

    }

    //SESSIONlISTENER
    override fun onDisconnected(session: Session) {
        try {
            if (SocketCommunication.isSocketConnected()) {
                try {
                    SocketCommunication.emitMobileLog(
                            "CALL_SCREEN_SESSION_DID_DISCONNECT",
                            "Session_sessionDidConnect when try to publish getting error",
                            "info",
                            "");
                } catch (e: Exception) {
                    Log.e("EXPCETION_SOCKET", e.message!!);
                }
            }
            Log.e(LOG_TAG, "onDisconnected: Disconnected from session: " + session.sessionId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //SESSION LISTENER
    //opposite person visible
    override fun onStreamReceived(session: Session, stream: Stream) {
        try {
            Log.e(LOG_TAG, "onStreamReceived: New Stream Received " + stream.streamId + " in session: " + session.sessionId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (SocketCommunication.isSocketConnected()) {
                try {
                    SocketCommunication.emitMobileLog(
                            "CALL_SCREEN_SESSION_STREAM_CREATED",
                            "Session_streamCreated",
                            "info",
                            "streamId: " + stream.streamId + "streamName: " + stream.name);
                } catch (e: Exception) {
                    Log.e("EXPCETION_SOCKET", e.message!!);
                }
            }
            //reset count
            countStreamDestroyed = 0
            countStreamDropped = 0
            if (mSubscriber == null) {
                mSubscriber = Subscriber.Builder(this, stream).build();
                mSubscriber!!.renderer.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL)
                mSubscriber?.setVideoListener(this)
                mSubscriber?.setStreamListener(this)
                mSubscriber?.setSubscriberListener(this)
                mSession!!.subscribe(mSubscriber);
                binding.subscriberContainer!!.addView(mSubscriber!!.view);
                isLoading(false);
                startCounter(CALLDURATION_S)


                //set video connected and waiting timer make stop
                try {
                    isVideoCallConnected = true
                    if (waitingTimer != null) {
                        waitingTimer!!.cancel()
                        waitingTimer = null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (SocketCommunication.isSocketConnected()) {
                    try {
                        SocketCommunication.emitMobileLog(
                                "CALL_SCREEN_SESSION_STREAM_CREATED_SUBSCRIBER_CREATE",
                                "Session_streamCreated",
                                "info",
                                "streamId: " + stream.streamId + "streamName: " + stream.name);
                    } catch (e: Exception) {
                        Log.e("EXPCETION_SOCKET", e.message!!);
                    }
                }

                videoCallArchiving()
                //checkForSessionError()
                  //startTrafficStatusService()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun startTrafficStatusService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.channel_name)
                val description = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(getString(R.string.channel_id), name, importance)
                channel.description = description
                channel.setSound(null, null)

                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
            val intentService = Intent(this, TrafficStatusService::class.java)
            intentService.setPackage(this.packageName)
            startService(intentService)
            TrafficStatusService.registerForTrafficService(this)
        }
    }


    //onError: SessionErrorDomain : SessionPublisherNotFound - (null description)
    // in session: 1_MX40NjQwMDY5Mn5-MTU4NzAyMDk0NDAzNH4zdkllWlg1Y1hSMGtSc2lhSW1HY0RiVXB-fg
    //if i cant see the opposite person my screen white and opposite person have also slow internet
    //this is called after onStreamDestroyed

    //but sometimes cant see opposite person becuase onsTREAMrECEIVED NEVER called and disconnected after 2 min without any error
    //and log
    //SESSION LISTENER
    override fun onError(session: Session, opentokError: OpentokError) {
        isSessionConnected = false
        try {
            if (SocketCommunication.isSocketConnected()) {
                try {
                    SocketCommunication.emitMobileLog(
                            "CALL_SCREEN_SESSION_ERROR",
                            "Session_didFailWithError",
                            "info",
                            opentokError.message);
                } catch (e: Exception) {
                    Log.e("EXPCETION_SOCKET", e.message!!);
                }
            }
            Log.e(LOG_TAG, "onSessionError: " + opentokError.errorDomain + " : " + opentokError.errorCode + " - " + opentokError.message + " in session: " + session.sessionId)

            retryConnectionMethod()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* Publisher Listener methods */

    override fun onStreamCreated(publisherKit: PublisherKit, stream: Stream) {
        try {
            isPublisherConnected = true
            Log.e(LOG_TAG, "onStreamCreated: Publisher Stream Created. Own stream " + stream.streamId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //StreamListener
    override fun onReconnected(p0: SubscriberKit?) {
        Log.d(LOG_TAG, "onReconnected: Stream onReconnected: ")
        println("### stream started") //To change body of created functions use File | Settings | File Templates.
    }


    //StreamListener
    override fun onDisconnected(p0: SubscriberKit?) {
        Log.d(LOG_TAG, "onDisconnected: Stream onDisconnected: ")
        println("#### stream stopped") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnected(p0: SubscriberKit?) {
        isSubscriberConnected = true
    }

    override fun onError(p0: SubscriberKit?, p1: OpentokError?) {
        isSubscriberConnected = false
        Log.d(LOG_TAG, "onErrorSubscriber:  " + p1?.message)
        retryConnectionMethod()
    }

    //SESSION LISTENER
    //opposite person dropped or opposite person end call
    //make it false
    override fun onStreamDropped(session: Session, stream: Stream) {
        try {
            Log.d(LOG_TAG, "onStreamDropped: Stream Dropped: " + stream.streamId + " in session: " + session.sessionId)
            if (SocketCommunication.isSocketConnected()) {
                try {
                    SocketCommunication.emitMobileLog(
                            "CALL_SCREEN_SESSION_STREAM_DESTROYED",
                            "Session_streamDestroyed",
                            "info",
                            "streamId: " + stream.streamId + "streamName: " + stream.name);
                } catch (e: Exception) {
                    Log.e("EXPCETION_SOCKET", e.message!!);
                }
            }
            try {
                countStreamDropped++
                if (mSubscriber != null) {
                    mSubscriber = null
                    isCallDrop = false
                    binding.subscriberContainer?.removeAllViews();
                    mSession!!.unpublish(mPublisher);
                    isSessionDisconnected = true
                    stopNetworkStatusService()
                    if (checkLessTime()) {
                        endCallDialog(getString(R.string.date_ended), getString(R.string.time_is_ended))
                    } else {
                        showDropCallDialog()
                    }
                } else {
                    //initializeSession(OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID, OpenTokConfig.TOKEN)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (checkLessTime()) {
                    endCallDialog(getString(R.string.date_ended), getString(R.string.time_is_ended))
                } else {
                   showDropCallDialog()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDropCallDialog() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isEndCall)
                dropCallDialog(getString(R.string.date_disconnected),
                        getString(R.string.your_match_drop_call))
        }, 3000)
    }

    //PUBLISHER LISTENER
    override fun onStreamDestroyed(publisherKit: PublisherKit, stream: Stream) {
        try {
            Log.d(LOG_TAG, "onStreamDestroyed: Publisher Stream Destroyed. Own stream " + stream.streamId)
            //show the message as your stream dropped
            if (SocketCommunication.isSocketConnected()) {
                try {
                    SocketCommunication.emitMobileLog(
                            "CALL_SCREEN_PUBLISHER_DESTROYED",
                            "Publisher_streamDestroyed",
                            "info",
                            "streamId: " + stream.streamId + " streamId:" + stream.name);
                } catch (e: Exception) {
                    Log.e("EXPCETION_SOCKET", e.message!!);
                }
            }
            countStreamDestroyed++
            //   if (!isEndCall && countStreamDestroyed == 3) {
            if (!isEndCall && !callEndedDialog) {
                isCallDrop = true
                stopNetworkStatusService()
                //endCallNetwork("Date Ended", "You are dropped from the call")
            } else if (isEndCall || isReportCall) {
                Log.d("onStreamDestroyed", "CALL ENDED BY USER")
            } else {
                //regenerate token
                // initializeSession(OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID, OpenTokConfig.TOKEN)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //pUBLISHER lISTENER
    override fun onError(publisherKit: PublisherKit, opentokError: OpentokError) {
        isPublisherConnected = false
        try {
            if (SocketCommunication.isSocketConnected()) {
                try {
                    SocketCommunication.emitMobileLog(
                            "CALL_SCREEN_PUBLISHER_ERROR",
                            "Publisher_didFailWithError",
                            "info",
                            opentokError.message)
                } catch (e: Exception) {
                    Log.e("EXPCETION_SOCKET", e.message!!);
                }
            }
            retryConnectionMethod()
            Log.e(LOG_TAG, "onError: " + opentokError.errorDomain + " : " + opentokError.errorCode + " - " + opentokError.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun retryConnectionMethod() {
        if (!isPublisherConnected) {
            createPublisher()
        } else if (!isSessionConnected) {
            initializeSession(OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID, OpenTokConfig.TOKEN)
        } else if (!isSubscriberConnected) {

        }
    }

    /***************************  End Video Call TokBox*******************************/
    private fun endCallNetwork(title: String, message: String) {
        clearAllRecords()
        binding.tvTime.text = ""
        SwapdroidAlertDialog.Builder(this)
                .setTitle(title)
                .isMessageVisible(true)
                .setMessage(message)
                .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                .setNegativeBtnText(getString(R.string.ok))
                .isNegativeVisible(false)
                .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                .setPositiveBtnText(getString(R.string.ok))
                .isPositiveVisible(true)
                .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                .setAnimation(SwapdroidAnimation.POP)
                .isCancellable(false)
                .showCancelIcon(true)
                .setIcon(R.drawable.ic_error_icon, SwapdroidIcon.Visible)
                .OnPositiveClicked {
                    isAlertShown = false
                    Log.e(LOG_TAG, "timer end call network")
                    Handler().postDelayed({
                        //endCallApiCall(message);
                        isCallDrop = true
                        navigateToWaiting();
                    }, 300)
                }
                .OnCancelClicked {
                    isAlertShown = false
                    Log.e(LOG_TAG, "timer end call net work")
                    Handler().postDelayed({
                        endCallApiCall(message)
                    }, 300)
                }
                .build()
    }


    //call api after 2 min if user not available
    //MAKE VALUE FALSE AND CALL ISS 00 -> FALSE , IF BUTTON PRESS FROM BOTTOM FALSE
    private fun callDropAfterTwoMin() {
        try {
            clearAllRecords()

            //call webservice
            if (isNetworkAvailable(this)) {
                showLoadingDialog(true)
                val token = AppInstance.userObj!!.getToken()!!

                val p = RequestParams();
                p.add("fromId", AppInstance.userObj!!.getId()!!)
                p.add("toId", intent.extras!!.getString(TOID, ""))
                p.add("fromUserSessionId", OpenTokConfig.SESSION_ID)
                p.add("toUserSessionId", OpenTokConfig.SESSION_ID)
                p.add("tokboxApi", OpenTokConfig.API_KEY)

                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.CALL_DROP_AFTER_TWO_MIN_WS);
                if (SocketCommunication.isSocketConnected()) {
                    try {
                        SocketCommunication.emitMobileLog(
                                "CALL_SCREEN_DROP_CALL",
                                "User wait for 2 min and response not comes",
                                "info",
                                "timer reach to 0 by waiting 2 min");
                    } catch (e: Exception) {
                        Log.e("EXPCETION_SOCKET", e.message!!);
                    }
                }
            } else {
                showDialogNoInternet(this@VideoCallActivity, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showLoadingDialog(false)
        }
    }


    //video call archiving web service
    private fun videoCallArchiving() {
        try {
            //call webservice
            if (isNetworkAvailable(this)) {
                val token = AppInstance.userObj!!.getToken()!!

                val p = RequestParams();
                p.add("fromId", AppInstance.userObj!!.getId()!!)
                p.add("toId", TOID_S)
                p.add("sessionId", OpenTokConfig.SESSION_ID)
                p.add("count", COUNT_S)
                p.add("rejoin", rejoinYesOrNo)

                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.ARCHIVING_VIDEO_CALL_WS);
                if (SocketCommunication.isSocketConnected()) {
                    try {
                        SocketCommunication.emitMobileLog(
                                "CALL_SCREEN_ARCHIVE_API",
                                "archive api called",
                                "info",
                                "sessionId: " + sessionId);
                    } catch (e: Exception) {
                        Log.e("EXPCETION_SOCKET", e.message!!);
                    }
                }
            } else {
                //no internet connection
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showLoadingDialog(false)
            if (SocketCommunication.isSocketConnected()) {
                try {
                    SocketCommunication.emitMobileLog(
                            "CALL_SCREEN_ARCHIVE_API_ERROR",
                            "archive api call error",
                            "info",
                            "sessionId: " + OpenTokConfig.SESSION_ID);
                } catch (e: Exception) {
                    Log.e("EXPCETION_SOCKET", e.message!!);
                }
            }

        }
    }

    //call api to get opentok credentials if not get from socket
    private fun checkAndCallGetOpenTokDataApi() {
        try {
            //call webservice
            if (isNetworkAvailable(this)) {
                showLoadingDialog(true)
                val token = AppInstance.userObj!!.getToken()!!

                val p = RequestParams();
                p.add("fromId", intent.extras!!.getString(TOID, ""))
                p.add("toId", TOID_S)

                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.RE_GENERATE_TOKEN_WS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //block user web service
    private fun blockUserApiCall() {
        try {
            //call webservice
            if (isNetworkAvailable(this)) {
                clearAllRecords()
                showLoadingDialog(true)

                val token = AppInstance.userObj!!.getToken()!!
                val p = RequestParams();
                p.add("blocked_by", AppInstance.userObj!!.getId()!!)
                p.add("blocked_to", intent.extras!!.getString(TOID, ""))
                p.add("openTok_sessionId", OpenTokConfig.SESSION_ID)

                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.BLOCKED_USER_WS);
            } else {
                showDialogNoInternet(this@VideoCallActivity, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showLoadingDialog(false)

        }
    }

    //report user web service
    private fun reportUserApiCall(note: String) {
        try {
            //call webservice
            if (isNetworkAvailable(this)) {
                clearAllRecords()
                showLoadingDialog(true)
                val token = AppInstance.userObj!!.getToken()!!

                val p = RequestParams();
                p.add("reported_by", AppInstance.userObj!!.getId()!!)
                p.add("reported_for", intent.extras!!.getString(TOID, ""))
                p.add("openTok_sessionId", OpenTokConfig.SESSION_ID)
                p.add("note", note)

                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.REPORT_USER_WS);
            } else {
                showDialogNoInternet(this@VideoCallActivity, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showLoadingDialog(false)

        }
    }

    //end call web service
    private fun endCallApiCall(note: String) {
        try {
            var isValidCall = "NO"

            if (isCallSuccess)
                isValidCall = "YES"


            //call webservice
            if (isNetworkAvailable(this)) {
                clearAllRecords()
                showLoadingDialog(true)
                val token = AppInstance.userObj!!.getToken()!!

                val p = RequestParams();
                p.add("endcall_by", AppInstance.userObj!!.getId()!!)
                p.add("endcall_for", intent.extras!!.getString(TOID, ""))
                p.add("openTok_sessionId", OpenTokConfig.SESSION_ID)
                p.add("note", note)
                p.add("valid_call", isValidCall)

                println("endCallRequest ***" +
                        "endcall_by=" + AppInstance.userObj!!.getId()!! +
                        " endcall_for= " + intent.extras!!.getString(TOID, "") +
                        " openTok_sessionId=" + OpenTokConfig.SESSION_ID + " note=" + note + "  valid_call=" + isValidCall)
                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.END_CALL_WS);
            } else {
                showDialogNoInternet(this@VideoCallActivity, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showLoadingDialog(false)

        }
    }

    private fun showLoadingDialog(show: Boolean) {
        try {
            if (show) mUtilLoader.startLoader(this@VideoCallActivity) else mUtilLoader.stopLoader()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * clear all records before navigating anywhere
     */
    private fun clearAllRecords() {
        try {
            if (timer != null) {
                timer!!.cancel()
                timer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (waitingTimer != null) {
                waitingTimer!!.cancel()
                waitingTimer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (mPublisher != null)
                mPublisher!!.publishAudio = false
            if (mSubscriber != null)
                mSubscriber!!.subscribeToAudio = false
            mSession?.unpublish(mPublisher);
            mSession?.disconnect();
            mPublisher = null
            mSession = null
            mSubscriber = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Navigations
     */
    private fun navigateToReview() {
        try {
            if (!isTimerEnd) {
                Log.e(LOG_TAG, "navigateToReview")
                isTimerEnd = true
                clearAllRecords()
                Handler().postDelayed({
                    if (isEndCall || isReportCall)
                        gotoReview()
                    else
                        autoReviewApiCall()
                }, 300)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            gotoReview()
        }
    }

    private fun autoReviewApiCall() {
        try {
            if (isNetworkAvailable(this)) {
                mUtilLoader.startLoader(this)

                val token = AppInstance.userObj!!.getToken()!!

                val p = RequestParams();
                p.add("review_from", AppInstance.userObj!!.getId()!!)
                p.add("review_for", intent.extras!!.getString(TOID, ""))
                p.add("isInterested", true)
                p.add("pleasant", "3")
                p.add("attractive", "3")
                p.add("religious", "3")
                p.add("notes", "")
                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.ADD_REVIEW_WS);
            } else {
                mUtilLoader.stopLoader()
                showDialogNoInternet(this@VideoCallActivity, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mUtilLoader.stopLoader()
            Log.e("SWAPLOG", e.message!!)
            showDialogNoInternet(this@VideoCallActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
        }
    }

    private fun navigateToWaiting() {
        try {
            Log.e(LOG_TAG, "navigateToWaiting")
            isTimerEnd = true

            Handler().postDelayed({
                gotoWaiting()
            }, 300)
        } catch (e: Exception) {
            e.printStackTrace()
            gotoWaiting()
        }
    }

    /**
     * go for review screen
     */
    private fun gotoReview() {
        Smartlook.setupAndStartRecording(resources.getString(R.string.smart_look_key));
        isNavigatingToReview = true
        isCallDrop = false;
        saveCallPref()
        Log.e(LOG_TAG, "gotoReview")
        if (!isClickable) {
            isClickable = true
            Log.e("SWAPTOID", "VCA : TOID = ${intent.extras!!.getString(TOID, "")}")
            val inte = Intent(this, ReviewActivity::class.java)
            try {
                inte.putExtra(TOID, intent.extras!!.getString(TOID, ""))
                inte.putExtra(SOURCE, VIDEO_CALL_SCREEN)
            } catch (e: Exception) {
                e.printStackTrace()
                inte.putExtra(TOID, "")
            }
            startActivity(inte)
            this@VideoCallActivity.finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
        }
    }

    private fun saveCallPref() {
        savePrefString(this, PreferenceConnector.SESSION_ID, OpenTokConfig.SESSION_ID)
        savePrefString(this, PreferenceConnector.MATCH_USER_ID, OpenTokConfig.MATCH_USER_ID)
        savePrefString(this, PreferenceConnector.MATCH_USER_NAME, OpenTokConfig.MATCHED_USER_NAME)
        savePrefInt(this, PreferenceConnector.VIDEO_CALL_COUNT, COUNT_S)
        savePrefString(this, REVIEW_TO_USER_ID, "")
    }

    /**
     * go for Waiting screen
     */
    private fun gotoWaiting() {
        Log.e(LOG_TAG, "gotoWaiting")
        Smartlook.setupAndStartRecording(resources.getString(R.string.smart_look_key));
        if (!isClickable) {
            isClickable = true
            val intent = Intent(this, WaitingActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            this.startActivity(intent)
            this.finish()
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
        }
    }

    override fun onBackPressed() {
        //do nothing here not permitted to go back
    }


    override fun asyncResponse(responseData: String?, label: String?, `object`: Any?) {
        val gson = Gson();
        println(responseData);
        when (label) {
            WebConstants.ADD_REVIEW_WS -> {
                showLoadingDialog(false)
                savePrefString(this, REVIEW_TO_USER_ID, "")
                val response = gson.fromJson(responseData, AddReviewResponse::class.java);
                when (response.code) {
                    ResponseCodes.Success -> {
                        Log.e("SWAPLOG", response.message!!)
                        navigateToWaiting()
                    }
                    ResponseCodes.ACCESS_TOKEN_EXPIRED -> expireAccessToken(this)
                    else -> {
                        Log.e("SWAPLOG", response.message!!)
                        showDialogNoInternet(this, response.message!!, "", R.drawable.ic_alert_icon)
                    }
                }
            }

            WebConstants.CALL_DROP_AFTER_TWO_MIN_WS -> {
                try {
                    val response = gson.fromJson(responseData, MainModel::class.java);
                    when (response.getCode()) {
                        ResponseCodes.Success -> {
                            showLoadingDialog(false)
                            showExtendedTwoMinDialog(response.getMessage()!!)
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> //go for review then there from user will logout
                            expireAccessToken(this@VideoCallActivity)
                        else -> {
                            showLoadingDialog(false)
                            showExtendedTwoMinDialog(response.getMessage()!!)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showLoadingDialog(false)
                    showExtendedTwoMinDialog(getString(R.string.failure_response))
                }

            }
            WebConstants.ARCHIVING_VIDEO_CALL_WS -> {
                val response = gson.fromJson(responseData, MainModel::class.java);
                try {
                    when (response.getCode()) {
                        ResponseCodes.Success -> {
                            //Success
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> //go for review then there from user will logout
                        {
                            //session expire
                        }
                        else -> {
                            //error
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    //Exception

                }

            }
            WebConstants.RE_GENERATE_TOKEN_WS -> {
                try {
                    val response = gson.fromJson(responseData, ReGenerateTokenResponse::class.java);
                    showLoadingDialog(false)
                    when (response.code) {
                        ResponseCodes.Success -> {
                            OpenTokConfig.API_KEY = response.data!!.tokboxApi!!
                            OpenTokConfig.SESSION_ID = response.data!!.fromUserSessionId!!
                            OpenTokConfig.TOKEN = response.data!!.tokboxToken!!
                            initializeSession(OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID, OpenTokConfig.TOKEN)
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> //go for review then there from user will logout
                            expireAccessToken(this@VideoCallActivity)
                        else -> {
                            //nothing to do
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            WebConstants.BLOCKED_USER_WS -> {
                try {
                    val response = gson.fromJson(responseData, MainModel::class.java);
                    when (response.getCode()) {
                        ResponseCodes.Success -> {
                            OpenTokConfig.ISBLOCK = true
                            showLoadingDialog(false)
                            navigateToReview()
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> //go for review then there from user will logout
                            navigateToReview()
                        else -> {
                            showLoadingDialog(false)
                            showDialogNoInternet(this@VideoCallActivity, response.getMessage()!!, "", R.drawable.ic_alert_icon)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showLoadingDialog(false)
                    showDialogNoInternet(this@VideoCallActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }

            }
            WebConstants.REPORT_USER_WS -> {
                try {
                    //emit another user that user ended call
                    endCallEmit()
                    val response = gson.fromJson(responseData, MainModel::class.java);
                    when (response.getCode()) {
                        ResponseCodes.Success -> {
                            showLoadingDialog(false)
                            navigateToReview()
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> //go for review then there from user will logout
                            navigateToReview()
                        else -> {
                            showLoadingDialog(false)
                            showDialogNoInternet(this@VideoCallActivity, response.getMessage()!!, "", R.drawable.ic_alert_icon)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showLoadingDialog(false)
                    showDialogNoInternet(this@VideoCallActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }

            }
            WebConstants.END_CALL_WS -> {
                try {
                    println("endCallResponse ***" + responseData);
                    val response = gson.fromJson(responseData, MainModel::class.java);
                    when (response.getCode()) {
                        ResponseCodes.Success -> {
                            showLoadingDialog(false)
                            navigateToReview()
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> //go for review then there from user will logout
                            navigateToReview()
                        else -> {
                            showLoadingDialog(false)
                            showDialogNoInternet(this@VideoCallActivity, response.getMessage()!!, "", R.drawable.ic_alert_icon)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showLoadingDialog(false)
                    showDialogNoInternet(this@VideoCallActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }
            }
        }

    }

    private fun endCallEmit() {
        if (SocketCommunication.isSocketConnected()) {
            SocketCommunication.emitInScreenActivity(INTRO_VIDEO_SCREEN)
            SocketCommunication.emitVideoCallEnd(OpenTokConfig.MATCH_USER_ID)
        }
    }

    override fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?) {
        when (label) {
            WebConstants.ADD_REVIEW_WS -> {
                showLoadingDialog(false)
                savePrefString(this, REVIEW_TO_USER_ID, "")
                Log.e("SWAPLOG", responseData!!.error_message)
                showDialogNoInternet(this, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
            }

            WebConstants.CALL_DROP_AFTER_TWO_MIN_WS -> {
                showLoadingDialog(false)
                showExtendedTwoMinDialog(getString(R.string.failure_response))
            }
            WebConstants.ARCHIVING_VIDEO_CALL_WS -> {

            }
            WebConstants.RE_GENERATE_TOKEN_WS -> {
                showLoadingDialog(false)
            }
            WebConstants.BLOCKED_USER_WS -> {
                showLoadingDialog(false)
                showDialogNoInternet(this@VideoCallActivity, responseData!!.error_message, "", R.drawable.ic_alert_icon)
            }
            WebConstants.REPORT_USER_WS -> {
                showLoadingDialog(false)
                showDialogNoInternet(this@VideoCallActivity, responseData!!.error_message, "", R.drawable.ic_alert_icon)
            }
            WebConstants.END_CALL_WS -> {
                showLoadingDialog(false)
                showDialogNoInternet(this@VideoCallActivity, responseData!!.error_message, "", R.drawable.ic_alert_icon)
            }
        }
    }

    private fun socketCommunication() {
        try {
            //creating socket callback
            setOnSocketCallBackListener(this)
            //creating socket connection
            SocketCommunication.connectSocket(this, mSocketCallBackListeners)
            if (SocketCommunication.isSocketConnected()) {
                SocketCommunication.emitOnlineActivity()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setOnSocketCallBackListener(mSocketCallBackListeners: SocketCallBackListeners) {
        this.mSocketCallBackListeners = mSocketCallBackListeners
    }

    override fun onMatchedResponse(data: SocketCallResponse) {

    }

    override fun onUserInfoResponse(data: SocketCallResponse) {

    }

    override fun onAcceptRejectResponse(data: SocketCallResponse) {

    }

    override fun onSocketConnected() {

    }

    override fun onRefreshBestMatch(status: String) {

    }


    override fun onSocketDisconnected() {

    }


    override fun callReconnectIsUserOnline(status: Boolean) {

    }


    override fun callDropWantToConnect(userId: String, friendId: String, firstName: String, friendFirstName: String, type: String) {
    }


    override fun connectedToCall(status: Boolean, tokboxToken: String, time: Long, matchFound: Boolean, count: Int,
                                 fromsocketid: String, toSocketId: String, toUserSessionId: String, fromId: String,
                                 toId: String, fromUserSessionId: String, tokboxApi: String, rejoin: Boolean) {

    }

    override fun friendOnline(arrayListUsers: MutableList<OnlineOfflineResponse>) {

    }

    override fun friendOffline(arrayListUsers: MutableList<OnlineOfflineResponse>) {

    }

    override fun bestMatches(arrayListUsers: MutableList<OnlineOfflineResponse>) {

    }

    override fun onNotifyEndCall(data: JSONObject) {
        Log.d("usersssEndCalll", "endCall")
        runOnUiThread(Runnable {
            isEndCall = true
            endCallDialog(getString(R.string.date_ended), getString(R.string.your_match_left_the_call))
        })
    }

    override fun onChatMessageReceive(data: ReceivedMessageResponse) {

    }

    override fun callAccepted(status: Boolean, tokboxToken: String, time: Long, matchFound: Boolean, count: Int,
                              fromsocketid: String, toSocketId: String, toUserSessionId: String, fromId: String,
                              toId: String, fromUserSessionId: String, tokboxApi: String, rejoin: Boolean) {

    }


    override fun onVideoDataReceived(p0: SubscriberKit?) {
        Log.d(LOG_TAG, "onVideoDataReceived: onVideoDataReceived ")
    }

    override fun onVideoEnabled(p0: SubscriberKit?, p1: String?) {
        Log.d(LOG_TAG, "onVideoEnabled: onVideoEnabled ")
        if (mSession != null) {
            mSession!!.onResume()
        }
    }

    override fun onVideoDisabled(p0: SubscriberKit?, p1: String?) {
        Log.d(LOG_TAG, "onVideoDisabled: onVideoDisabled ")
        if (mSession != null && !isPause) {
            mSession!!.onResume()
        }

    }

    override fun onVideoDisableWarning(p0: SubscriberKit?) {
        Log.d(LOG_TAG, "onVideoDisableWarning: onVideoDisableWarning ")

    }

    override fun onVideoDisableWarningLifted(p0: SubscriberKit?) {
        Log.d(LOG_TAG, "onVideoDisableWarningLifted: onVideoDisableWarningLifted ")
    }

    override fun onNetworkListener(isConnected: Boolean) {

    }


}
