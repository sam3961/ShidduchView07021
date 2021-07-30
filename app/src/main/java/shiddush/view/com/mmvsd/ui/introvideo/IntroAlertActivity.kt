package shiddush.view.com.mmvsd.ui.introvideo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.util.Log
import android.util.TypedValue
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson

import shiddush.view.com.mmvsd.BuildConfig
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityIntroAlertBinding
import shiddush.view.com.mmvsd.model.subscription.CheckSubscriptionResponse
import shiddush.view.com.mmvsd.model.videocall.MatchedUserDetailsRequest
import shiddush.view.com.mmvsd.model.videocall.ReGenerateTokenResponse
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.rest.RequestParams
import shiddush.view.com.mmvsd.rest.RestClient
import shiddush.view.com.mmvsd.rest.RestMethod
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.ui.videocall.OpenTokConfig
import shiddush.view.com.mmvsd.utill.*

class IntroAlertActivity : AppCompatActivity(),RestClient.OnAsyncRequestComplete, RestClient.OnAsyncRequestError {

    lateinit var binding: ActivityIntroAlertBinding
    private var screenLock: PowerManager.WakeLock? = null
    private var isClickable = false
    private var firebaseAnalytics: FirebaseAnalytics?=null
    private val TAG = IntroAlertActivity::class.java.simpleName


    //timer
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this)
            firebaseAnalytics!!.setUserId(getUserObject(this).getEmail())

        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro_alert)
        changeNavBack()
        setTextSizes()
        if (OpenTokConfig.API_KEY.trim().isEmpty()){
            checkAndCallGetOpenTokDataApi()
        }else{
            startCounter(5000)
        }
    }

    //call api to get opentok credentials if not get from socket
    private fun checkAndCallGetOpenTokDataApi() {
        try {
            //call webservice
            if (OpenTokConfig.API_KEY.trim().isEmpty()) {
                if (isNetworkAvailable(this)) {
                    val token = AppInstance.userObj!!.getToken()!!
                    val p = RequestParams()
                    p.add("fromId", AppInstance.userObj!!.getId()!!)
                    p.add("toId",intent.extras!!.getString(TOID, ""))
                    val rest = RestClient(this, RestMethod.POST, p)
                    rest.setToken(token)
                    rest.execute(WebConstants.RE_GENERATE_TOKEN_WS);
                    if(SocketCommunication.isSocketConnected()) {
                        SocketCommunication.emitMobileLog(
                                "INTRO_SCREEN_REGENRATE_TOKEN",
                                "Regenrate token method call",
                                "info",
                                "method call due to tokboxApi is getting blank"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            logFirebaseEvents("checkAndCallGetOpenTokDataApi","Error Message: "+e.message)
            e.printStackTrace()
        }
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
        val size80 = getFontSize(this, 80)
        val size25 = getFontSize(this, 25)
        val size18 = getFontSize(this, 18)
        val size13 = getFontSize(this, 13)

        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        binding.trouble.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.instruction.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.instruction2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        try {
            //cardHeight
            val card80Height = dpToPxs(size80.toInt())
            binding.ivBackImage.layoutParams.height = card80Height
            binding.ivBackImage.layoutParams.width = card80Height
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to start counter for 5 seconds
     * @param timeDuration : time is milliseconds
     */
    fun startCounter(timeDuration: Long) {
        try {
            timer = object : CountDownTimer(timeDuration, 1) {
                override fun onFinish() {
                    navigateForCall()
                }

                override fun onTick(durationSeconds: Long) {

                }
            }
            timer!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

        if (SocketCommunication.isSocketConnected()) {
            SocketCommunication.emitInScreenActivity(INTRO_SCREEN)
        }

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
        } catch (e: Exception) {
            e.printStackTrace()
        }
        clearTimer()
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
     * to navigate for video call
     */
    private fun navigateForCall() {
        if (!isClickable) {
            isClickable = true
            try {
                Log.e("SWAPTOID", "IAA : TOID = ${intent.extras!!.getString(TOID, "")}")
                val mIntent = Intent(this@IntroAlertActivity, UserInfoBeforeCallActivity::class.java)
                mIntent.putExtra(FROMID, intent.extras!!.getString(FROMID, ""))
                mIntent.putExtra(TOID, intent.extras!!.getString(TOID, ""))
                mIntent.putExtra(COUNT, intent.extras!!.getInt(COUNT, 0))
                mIntent.putExtra(CALLDURATION, intent.extras!!.getLong(CALLDURATION, 0L))
                mIntent.putExtra(FROMUSER_SOCKET_ID, intent.extras!!.getString(FROMUSER_SOCKET_ID, ""))
                mIntent.putExtra(TOUSER_SOCKET_ID, intent.extras!!.getString(TOUSER_SOCKET_ID, ""))
                mIntent.putExtra(REJOIN, intent.extras!!.getBoolean(REJOIN, false))
                startActivity(mIntent)
                this@IntroAlertActivity.finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
            } catch (e: Exception) {
                e.printStackTrace()
                val intent = Intent(this@IntroAlertActivity, OtherScreensActivity::class.java)
                intent.putExtra(SOURCE, NO_MATCH_FOUND)
                startActivity(intent)
                this@IntroAlertActivity.finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
            }
        }
    }

    override fun onBackPressed() {
        //nothing to do not permitted to go back
    }

    fun logFirebaseEvents(key: String, value: String?){
        val params = Bundle()
        params.putString(key,value)
        firebaseAnalytics?.logEvent(TAG, params)
    }

    override fun asyncResponse(responseData: String?, label: String?, `object`: Any?) {
        try {
            val gson = Gson();
            val response = gson.fromJson(responseData, ReGenerateTokenResponse::class.java);
            when (response.code) {
                ResponseCodes.Success -> {
                    OpenTokConfig.API_KEY = response.data!!.tokboxApi!!
                    OpenTokConfig.SESSION_ID = response.data!!.fromUserSessionId!!
                    OpenTokConfig.TOKEN = response.data!!.tokboxToken!!

                    if(SocketCommunication.isSocketConnected()) {
                        try {
                            SocketCommunication.emitMobileLog(
                                    "INTRO_SCREEN_REGENRATE_TOKEN",
                                    "Regenrate token method response get",
                                    "info",
                                    "fromSocketID:" + response.data!!.fromSocketID +", toSocketID: "+response.data!!.toSocketID +" ToSessionId: "+response.data!!.toUserSessionId
                            );
                        }catch(e: Exception){
                            Log.e("EXPCETION_SOCKET", e.message!!)
                        }
                    }
                    navigateForCall()
                }
                ResponseCodes.ACCESS_TOKEN_EXPIRED -> //go for review then there from user will logout
                    expireAccessToken(this@IntroAlertActivity)
                else -> {

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("apiResponse","Error Message: "+e.message!!)
        }

    }

    override fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?) {
    }

}
