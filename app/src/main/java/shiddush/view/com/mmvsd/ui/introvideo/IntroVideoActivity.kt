package shiddush.view.com.mmvsd.ui.introvideo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_intro_video.*
import org.json.JSONObject
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityIntroVideoBinding
import shiddush.view.com.mmvsd.model.chat.ReceivedMessageResponse
import shiddush.view.com.mmvsd.model.onlineOffline.OnlineOfflineResponse
import shiddush.view.com.mmvsd.model.videocall.MatchedAnswersData
import shiddush.view.com.mmvsd.model.videocall.MatchedUserDetailsRequest
import shiddush.view.com.mmvsd.model.videocall.MatchedUserDetailsResponse
import shiddush.view.com.mmvsd.model.videocall.SocketCallResponse
import shiddush.view.com.mmvsd.radioplayer.RadioPlayer
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.rest.RestClient
import shiddush.view.com.mmvsd.socket.SocketCallBackListeners
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.ui.introvideo.adapter.PageAdapter
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.ui.services.NetworkSchedulerService
import shiddush.view.com.mmvsd.ui.videocall.OpenTokConfig
import shiddush.view.com.mmvsd.utill.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class IntroVideoActivity : AppCompatActivity(), SocketCallBackListeners, RestClient.OnAsyncRequestComplete,
        RestClient.OnAsyncRequestError, NetworkSchedulerService.onNetworkConnectionListener {

    lateinit var binding: ActivityIntroVideoBinding
    lateinit var mediaController: MediaController
    private var screenLock: PowerManager.WakeLock? = null
    private var matchFound: Boolean = false
    private var TOKBOX_API_S = ""
    private var FROMID_S = ""
    private var TOID_S = ""
    private var COUNT_S = 0
    private var FROMUSER_SOCKET_ID_S = ""
    private var TOUSER_SOCKET_ID_S = ""
    private var FROMUSER_SESSION_ID_S = ""
    private var TOUSER_SESSION_ID_S = ""
    private var CALLDURATION_S: Long = 0  //300000
    private var CurrentDuration: Long = 0  //300000
    private var TotalDuration: Long = 0  //300000
    private var TOKBOX_TOKEN_S = ""
    private var isTimerStart: Boolean = false
    private var beforePause = false
    private var isClickable = false
    private var isSessionExpire = false
    private var gotDataFromSocket = false
    private var isEmitted = false
    private var rejoin = false
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private val TAG = IntroVideoActivity::class.java.simpleName
    private var timerSeconds=0
    private var viewPagerPosition=0

    //timer
    private var timer: CountDownTimer? = null

    //socket communication
    lateinit var mSocketCallBackListeners: SocketCallBackListeners

    override fun onCreate(savedInstanceState: Bundle?) {
        // strictModeEnable()
        super.onCreate(savedInstanceState)
        try {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this)
            firebaseAnalytics!!.setUserId(getUserObject(this).getEmail());

        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro_video)
        NetworkSchedulerService.registerForNetworkService(this,this)
        changeNavBack()
        setTextSizes()
        setTimerVal(0L)
        setVideoView()
        socketCommunication()
        animateSandClock()
        setPagerAdapter()
    }

    private fun setPagerAdapter() {
        binding.pager.setPagingEnabled(false)
        binding.pager.adapter = PageAdapter(supportFragmentManager)
        binding.pager.setPageTransformer(true, TRANSFORM_CLASSES[0].clazz.newInstance())

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
                window.navigationBarColor = ContextCompat.getColor(this, shiddush.view.com.mmvsd.R.color.black)
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
        val size22 = getFontSize(this, 22)
        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvTimer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size22)
    }

    /**
     * to set up video view
     */
    fun setVideoView() {
        binding.tvTimer.text = ""
        try {
            val packageName = application.packageName
            mediaController = MediaController(this)
            mediaController.setAnchorView(binding.videoView)
            //Location of Media File
            val uri = Uri.parse("android.resource://" + packageName + "/" + shiddush.view.com.mmvsd.R.raw.intro_video)
            //Starting VideoView By Setting MediaController and URI
            mediaController.visibility = View.GONE
            binding.videoView.setMediaController(mediaController)
            binding.videoView.setVideoURI(uri)
            binding.videoView.requestFocus()

            binding.videoView.setOnPreparedListener {
                Log.e("SWAPLOG", "Duration = " + binding.videoView.duration)
                if (!isTimerStart) {
                    isTimerStart = true
                    TotalDuration = binding.videoView.duration.toLong()
                    startCounter(TotalDuration)
                }
            }

            binding.videoView.setOnCompletionListener {
                Log.e("SWAPLOG", "setOnCompletionListener")
            }

            binding.videoView.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to start counter as per video time
     * @param timeDuration : this comes from video duration
     */
    fun startCounter(timeDuration: Long) {
        try {
            timer = object : CountDownTimer(timeDuration, 1000) {
                override fun onFinish() {
                    binding.tvTimer.text = "00:00"
                    navigateForCall()
                }

                override fun onTick(durationSeconds: Long) {
                    CurrentDuration = durationSeconds
                    setTimerVal(durationSeconds)

                    if (timerSeconds==10){
                        viewPagerPosition = viewPagerPosition+1
                       binding.pager.setCurrentItem(viewPagerPosition,true)
                        timerSeconds=0;
                    }
                    timerSeconds++

                }
            }
            timer!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to show timer to text view
     */
    private fun setTimerVal(timeDuration: Long) {
        try {
            if (timeDuration != 0L) {
                val hms: String = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeDuration),
                        TimeUnit.MILLISECONDS.toMinutes(timeDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDuration)),
                        TimeUnit.MILLISECONDS.toSeconds(timeDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDuration)))
                binding.tvTimer.text = hms.split(":")[1] + ":" + hms.split(":")[2]


            } else {
                binding.tvTimer.text = ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //pick matched user for video call api call
    private fun pickMatchUserForVideoCall(fromId: String, toId: String) {
        try {
            if (isNetworkAvailable(this)) {
                try {
                    val request = MatchedUserDetailsRequest()
                    request.fromId = fromId
                    request.toId = toId
                    val token = AppInstance.userObj!!.getToken()!!
                    val manager = NetworkManager()
                    SocketCommunication.emitMobileLog(
                            "INCOMING_CALL_INFO_API_CALL",
                            "Method call to get other user detail",
                            "info",
                            "")
                    manager.createApiRequest(ApiUtilities.getAPIService().getMatchUserDetails(token, request), object : ServiceListener<MatchedUserDetailsResponse> {
                        override fun getServerResponse(response: MatchedUserDetailsResponse, requestcode: Int) {
                            when (response.code) {
                                ResponseCodes.Success -> try {
                                    var name = response.data?.name!!
                                    var totalAnswers = 0;
                                    try {
                                        totalAnswers = response.data?.count!!;
                                    } catch (e: Exception) {

                                    }
                                    if (name.contains(" ")) {
                                        name = name.split(" ")[0]
                                        name = name.substring(0, 1).toUpperCase() + name.substring(1)
                                    } else {
                                        name = name.substring(0, 1).toUpperCase() + name.substring(1)
                                    }
                                    val data: ArrayList<MatchedAnswersData> = ArrayList<MatchedAnswersData>()
                                    OpenTokConfig.MATCHED_USER_NAME = name
                                    OpenTokConfig.MATCHED_USER_AGE = response.data?.age!!
                                    OpenTokConfig.MATCHED_USER_RATING = response.data?.compatibilityrating!!.toFloat().roundToInt()
                                    OpenTokConfig.MATCHED_USER_DATA = data
                                    OpenTokConfig.MATCH_USER_ANSWER_COUNT = totalAnswers;
                                    SocketCommunication.emitMobileLog(
                                            "INCOMING_CALL_INFO_API_CALL_RESPONSE",
                                            "Method response get",
                                            "info",
                                            "Name: " + name + " compatibilityrating:" + OpenTokConfig.MATCHED_USER_RATING + " commonQuetionCount:" + totalAnswers
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    logFirebaseEvents("pickMatchUserForVideoCallAPI", "Error Message: " + e.message)
                                }
                                ResponseCodes.ACCESS_TOKEN_EXPIRED -> {
                                    logFirebaseEvents("pickMatchUserForVideoCallAPI", "Session Expire")
                                    //go for sorry no match found
                                    isSessionExpire = true
                                }
                                else -> {

                                    //failure response
                                }
                            }
                        }

                        override fun getError(error: ErrorModel, requestcode: Int) {
                            //failure response
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                // no internet
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun animateSandClock() {
        val rotate180 = RotateAnimation(0f, 180f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate180.setDuration(900)


        val rotate0 = RotateAnimation(180f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate0.setDuration(900)

        imageViewClock.startAnimation(rotate180)

        rotate180.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                imageViewClock.startAnimation(rotate0)
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        rotate0.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                imageViewClock.startAnimation(rotate180)
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
    }


    override fun onResume() {
        super.onResume()
        RadioPlayer.pauseRadio()

        //to set app instance
        try {
            AppInstance.userObj = getUserObject(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (beforePause) {
                val duration = TotalDuration - CurrentDuration
                Log.e("SWAPLOG", "duration = $duration, total = $TotalDuration, CurrentDuration = $CurrentDuration")

                binding.videoView.start()
                binding.videoView.seekTo(duration.toInt())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            @Suppress("DEPRECATION")
            screenLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, IntroVideoActivity::class.java.simpleName)
            screenLock!!.acquire()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            screenLock!!.release()
            imageViewClock.clearAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        clearTimer()
    }

    override fun onPause() {
        super.onPause()
        try {
            binding.videoView.pause()
            beforePause = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    /**
     * ++ communicate with socket
     */
    private fun socketCommunication() {
        try {
            //creating socket callback
            setOnSocketCallBackListener(this)
            //creating socket connection
            SocketCommunication.setCallBack(this@IntroVideoActivity, mSocketCallBackListeners)
            if (SocketCommunication.isSocketConnected()) {
                if (!isEmitted) {
                    isEmitted = true
                    SocketCommunication.emitInScreenActivity(INTRO_VIDEO_SCREEN)
                    SocketCommunication.emitCallMatching()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setOnSocketCallBackListener(mSocketCallBackListeners: SocketCallBackListeners) {
        this.mSocketCallBackListeners = mSocketCallBackListeners
    }

    //callback from socket
    override fun onMatchedResponse(data: SocketCallResponse) {
        Log.e("SWAPSOC", "onMatchedResponse = IVA")
        if (!gotDataFromSocket) {
            gotDataFromSocket = true

            try {
                when (data.code) {
                    ResponseCodes.Success -> try {
                        matchFound = data.matchFound!!
                        if (matchFound) {
                            //setData(data, false)
                            setData(data, true)
                            //call api for getting other user details
                            var fromID = FROMID_S
                            var toID = TOID_S
                            if (fromID != AppInstance.userObj!!.getId()!!) {
                                fromID = AppInstance.userObj!!.getId()!!
                                toID = data.fromId!!
                            }

                            //below code is for initiation like to decide who is publisher
                            if (FROMID_S == AppInstance.userObj!!.getId()!!) {
                                //SocketCommunication.emitGenerateToken(FROMID_S, TOID_S, FROMUSER_SOCKET_ID_S, TOUSER_SOCKET_ID_S, COUNT_S)
                            }
                            rejoin = data.rejoin!!
                            //name, gender, matched ques count, rating and matched questions list
                            pickMatchUserForVideoCall(fromID, toID)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        logFirebaseEvents("onMatchedResponse", "Error Message: " + e.message)
                    }
                    ResponseCodes.ACCESS_TOKEN_EXPIRED -> {
                        //go for sorry no match found
                        isSessionExpire = true

                    }
                    else -> {
                        //failure response
                        matchFound = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                //failure response
                matchFound = false
                logFirebaseEvents("onMatchedResponse", "Error Message: " + e.message)
            }
        }
    }

    override fun onUserInfoResponse(data: SocketCallResponse) {
        Log.e("SWAPSOC", "onUserInfoResponse = IVA")
        try {
            when (data.code) {
                ResponseCodes.Success -> try {
                    matchFound = data.matchFound!!
                    if (matchFound) {
                        setData(data, true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logFirebaseEvents("onUserInfoResponse", "Error Message: " + e.message)
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
            logFirebaseEvents("onUserInfoResponse", "Error Message: " + e.message)
            //failure response
        }
    }

    //set all received data and update values
    private fun setData(data: SocketCallResponse, makeAlternative: Boolean) { //false means set as it is data
        try {
            TOKBOX_API_S = data.tokboxApi!!
            COUNT_S = data.count!!
            CALLDURATION_S = data.time!!
            TOKBOX_TOKEN_S = data.tokboxToken!!
            println("RESPONSE_TOKEN");
            println(data);
            if (!makeAlternative || (data.fromId!! == AppInstance.userObj!!.getId()!!)) {
                //if (data.fromId!! == AppInstance.userObj!!.getId()!!) {
                FROMID_S = data.fromId!!
                TOID_S = data.toId!!
                FROMUSER_SESSION_ID_S = data.fromUserSessionId!!
                TOUSER_SESSION_ID_S = data.toUserSessionId!!
                //  Toast.makeText(this,"414",Toast.LENGTH_LONG).show()
            } else {
                FROMID_S = AppInstance.userObj!!.getId()!!
                TOID_S = data.fromId!!
                FROMUSER_SESSION_ID_S = data.toUserSessionId!!
                TOUSER_SESSION_ID_S = data.fromUserSessionId!!
                //  Toast.makeText(this,"420",Toast.LENGTH_LONG).show()
            }
            //set it to OpenTok Config
            OpenTokConfig.API_KEY = TOKBOX_API_S
            OpenTokConfig.SESSION_ID = FROMUSER_SESSION_ID_S
            OpenTokConfig.TOKEN = TOKBOX_TOKEN_S

            SocketCommunication.emitMobileLog(
                    "CALL_MATCHING_LISTNER_RESPONSE",
                    "emitdatatoparticluaruser data recived",
                    "info",
                    "fromSocketID: " + data.fromSocketID + ", toSocketID: " + data.toSocketID + " ToSessionId: " + data.toUserSessionId
            );
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("setData", "Error Message: " + e.message)
        }
    }

    //callback from socket for accept reject
    override fun onAcceptRejectResponse(data: SocketCallResponse) {
        Log.e("SWAPSOC", "onAcceptRejectResponse = IVA")
    }

    override fun onSocketConnected() {
        Log.e("SWAPSOC", "onSocketConnected = IVA")
        if (!isEmitted) {
            isEmitted = true
            SocketCommunication.emitCallMatching()
        }
    }
    override fun onRefreshBestMatch(status: String) {

    }

    override fun onSocketDisconnected() {
        Log.e("SWAPSOC", "onSocketDisconnected = IVA")
        isEmitted = false
    }

    override fun callAccepted(status: Boolean, tokboxToken: String, time: Long, matchFound: Boolean, count: Int,
                              fromsocketid: String, toSocketId: String, toUserSessionId: String, fromId: String,
                              toId: String, fromUserSessionId: String, tokboxApi: String, rejoin: Boolean) {

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

    }

    override fun onChatMessageReceive(data: ReceivedMessageResponse) {

    }

    override fun callReconnectIsUserOnline(boolean: Boolean) {
    }


    /**
     * -- communicate with socket
     */

    /**
     * navigate to 5 sec intro alert screen
     * if match not found then navigate to no match screen
     */
    private fun navigateForCall() {
        try {
            Log.e("SWAPTOID", "IVA : TOID = $TOID_S")
            if (!isClickable) {
                isClickable = true
                if (matchFound) {
                    val mIntent = Intent(this@IntroVideoActivity, IntroAlertActivity::class.java)
                    mIntent.putExtra(FROMID, FROMID_S)
                    mIntent.putExtra(TOID, TOID_S)
                    mIntent.putExtra(COUNT, COUNT_S)
                    mIntent.putExtra(CALLDURATION, CALLDURATION_S)
                    mIntent.putExtra(FROMUSER_SOCKET_ID, FROMUSER_SOCKET_ID_S)
                    mIntent.putExtra(TOUSER_SOCKET_ID, TOUSER_SOCKET_ID_S)
                    mIntent.putExtra(REJOIN, rejoin)
                    startActivity(mIntent)
                    this@IntroVideoActivity.finish()
                    overridePendingTransition(shiddush.view.com.mmvsd.R.anim.slide_in_right, shiddush.view.com.mmvsd.R.anim.slide_out_left)  // for open
                } else {
                    navigateForNoMatch()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            navigateForNoMatch()
        }
    }

    private fun navigateForNoMatch() {
        if (isSessionExpire) {
            expireAccessToken(this@IntroVideoActivity)
        } else {
            val intent = Intent(this@IntroVideoActivity, OtherScreensActivity::class.java)
            intent.putExtra(SOURCE, NO_MATCH_FOUND)
            startActivity(intent)
            this@IntroVideoActivity.finish()
            overridePendingTransition(shiddush.view.com.mmvsd.R.anim.slide_in_right, shiddush.view.com.mmvsd.R.anim.slide_out_left)  // for open
        }
    }

    /******************************** Start ************************************************
     * show reject Call  dialog
     */

    override fun onBackPressed() {
        //nothing to do not permitted to go back
    }

    fun logFirebaseEvents(key: String, value: String?) {
        val params = Bundle()
        params.putString(key, value)
        firebaseAnalytics?.logEvent(TAG, params)
    }

    override fun asyncResponse(responseData: String?, label: String?, `object`: Any?) {
        try {
            val gson = Gson();
            val response = gson.fromJson(responseData, MatchedUserDetailsResponse::class.java);
            when (response.code) {
                ResponseCodes.Success -> try {
                    var name = response.data?.name!!
                    var totalAnswers = 0;
                    try {
                        totalAnswers = response.data?.count!!;
                    } catch (e: Exception) {

                    }
                    if (name.contains(" ")) {
                        name = name.split(" ")[0]
                        name = name.substring(0, 1).toUpperCase() + name.substring(1)
                    } else {
                        name = name.substring(0, 1).toUpperCase() + name.substring(1)
                    }
                    val data: ArrayList<MatchedAnswersData> = ArrayList<MatchedAnswersData>()
                    OpenTokConfig.MATCHED_USER_NAME = name
                    OpenTokConfig.MATCHED_USER_AGE = response.data?.age!!
                    OpenTokConfig.MATCHED_USER_RATING = response.data?.compatibilityrating!!.toFloat().roundToInt()
                    OpenTokConfig.MATCHED_USER_DATA = data
                    OpenTokConfig.MATCH_USER_ANSWER_COUNT = totalAnswers;

                    SocketCommunication.emitMobileLog(
                            "INCOMING_CALL_INFO_API_CALL_RESPONSE",
                            "Method response get",
                            "info",
                            "Name: " + name + " compatibilityrating:" + OpenTokConfig.MATCHED_USER_RATING + " commonQuetionCount:" + totalAnswers
                    );

                } catch (e: Exception) {
                    e.printStackTrace()
                    logFirebaseEvents("pickMatchUserForVideoCallAPI", "Error Message: " + e.message)
                }
                ResponseCodes.ACCESS_TOKEN_EXPIRED -> {
                    logFirebaseEvents("pickMatchUserForVideoCallAPI", "Session Expire")
                    //go for sorry no match found
                    isSessionExpire = true
                }
                else -> {

                    //failure response
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("pickMatchUserForVideoCallAPI", "Error Message: " + e.message)
            //failure response

        }

    }

    override fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?) {
        logFirebaseEvents("pickMatchUserForVideoCallAPI", "Error Message: " + responseData!!.error_message)

    }

    private val TRANSFORM_CLASSES: List<TransformerItem> = listOf(
            TransformerItem(CubeOutTransformer::class.java))

    class TransformerItem(
            val clazz: Class<out ViewPager.PageTransformer>,
            val title: String = clazz.simpleName
    ) {
        override fun toString(): String = title
    }

    override fun onNetworkListener(isConnected: Boolean) {

    }
}
