package shiddush.view.com.mmvsd.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.smartlook.sdk.smartlook.Smartlook
import com.tapadoo.alerter.Alerter
import io.branch.referral.Branch
import io.branch.referral.BranchError

import org.json.JSONObject
import shiddush.view.com.mmvsd.BuildConfig
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivitySplashBinding
import shiddush.view.com.mmvsd.model.ReferralData
import shiddush.view.com.mmvsd.model.radio.RadioListResponse
import shiddush.view.com.mmvsd.model.review.CheckReviewResponse
import shiddush.view.com.mmvsd.model.subscription.CheckSubscriptionResponse
import shiddush.view.com.mmvsd.model.subscriptionStatus.SubscriptionStatus
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.rest.RequestParams
import shiddush.view.com.mmvsd.rest.RestClient
import shiddush.view.com.mmvsd.rest.RestMethod
import shiddush.view.com.mmvsd.ui.commonloginscreen.CommonLoginActivity
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.ui.recievers.AlarmReciever
import shiddush.view.com.mmvsd.ui.recievers.BackgroundReceiver
import shiddush.view.com.mmvsd.ui.review.ReviewActivity
import shiddush.view.com.mmvsd.ui.services.NetworkSchedulerService
import shiddush.view.com.mmvsd.ui.subscription.SubscriptionActivity
import shiddush.view.com.mmvsd.ui.tnc.TermsAndConActivity
import shiddush.view.com.mmvsd.ui.waitingscreen.WaitingActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.utill.PreferenceConnector.Companion.REVIEW_TO_USER_ID
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidColorConstants
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import shiddush.view.com.mmvsd.model.review.AddReviewResponse as AddReviewResponse1


/**
 *
 * this is the splash screen
 * https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/kotlin/MyFirebaseMessagingService.kt
 * keyhash :  keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
 **/

@Suppress("DEPRECATION")
class Splash : AppCompatActivity(), RestClient.OnAsyncRequestComplete, RestClient.OnAsyncRequestError { //, PurchasesUpdatedListener

    var binding: ActivitySplashBinding? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val TAG = Splash::class.java.simpleName
    var userid: String = ""
    var to_userid: String = ""
    var isBlocked: Boolean = false
    var isNormalQuizComplete: Boolean = false
    var isBibleQuizComplete: Boolean = false
    var isSubscriptionFree: Boolean = false
    var isSubscriptionDone: Boolean = false
    lateinit var reviewResponse: CheckReviewResponse;

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        FirebaseApp.initializeApp(this)
        Smartlook.setupAndStartRecording(resources.getString(R.string.smart_look_key));
        scheduleBackgroundProcess(this@Splash)
        //setContentView(R.fragment_intro_notes.activity_alarm_display)
/*
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobInfo = JobInfo.Builder(0, ComponentName(this@Splash, AlarmService::class.java))
                // only add if network access is required
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(1 * 1000)// wait at least
                .setOverrideDeadline(3 * 1000) // maximum delay
                .build()
        jobScheduler.schedule(jobInfo)*/

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        changeNavBack()
        storeToken(this)
        generateKeyHash()
        setTextSizes()
        clearAllNotifications()
        //checkBilling()

        //check review submitted or not then proceed
        callReviewApi();
        //scheduleAlarms()

        //conevrtTime()
        scheduleNetworkJob()


    }

    private fun callReviewApi() {
        Handler().postDelayed({
            checkandNavigate()
        }, 500)
    }

    fun scheduleAlarms() {

        val localCalendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val currentDayOfWeek: Int = localCalendar.get(Calendar.DAY_OF_WEEK)

        if (currentDayOfWeek == 1) {
            setTimings(1, 0)
            setTimings(2, 1)
            setTimings(3, 2)
            setTimings(4, 3)
            setTimings(5, 4)
        } else if (currentDayOfWeek == 2) {
            setTimings(1, 6)
            setTimings(2, 0)
            setTimings(3, 1)
            setTimings(4, 2)
            setTimings(5, 3)
        } else if (currentDayOfWeek == 3) {
            setTimings(1, 5)
            setTimings(2, 6)
            setTimings(3, 0)
            setTimings(4, 1)
            setTimings(5, 2)
        } else if (currentDayOfWeek == 4) {
            setTimings(1, 4)
            setTimings(2, 5)
            setTimings(3, 6)
            setTimings(4, 0)
            setTimings(5, 1)
        } else if (currentDayOfWeek == 5) {
            setTimings(1, 3)
            setTimings(2, 4)
            setTimings(3, 5)
            setTimings(4, 6)
            setTimings(5, 0)
        }
    }

    fun setTimings(reqCode: Int, day: Int) {

        var hour = 2

        if (reqCode == 1) {
            hour = 2
        } else if (reqCode == 2 || reqCode == 5) {
            hour = 5
        } else if (reqCode == 3 || reqCode == 4) {
            hour = 9
        }

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 28)
        calendar.set(Calendar.SECOND, 0)
        val timestamp: Long = calendar.timeInMillis
        setAlarm(reqCode, timestamp)

        /*calendar.add(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, 5)
        calendar.set(Calendar.MINUTE, 28)
        calendar.set(Calendar.SECOND, 0)
        monday = calendar.timeInMillis
        setAlarm(2, monday)

        calendar.add(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 28)
        calendar.set(Calendar.SECOND, 0)
        tuesday = calendar.timeInMillis
        setAlarm(3, tuesday)

        calendar.add(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 28)
        calendar.set(Calendar.SECOND, 0)
        wednesday = calendar.timeInMillis
        setAlarm(4, wednesday)

        calendar.add(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, 5)
        calendar.set(Calendar.MINUTE, 28)
        calendar.set(Calendar.SECOND, 0)
        thursday = calendar.timeInMillis
        setAlarm(5, thursday)*/
    }


    @SuppressLint("NewApi")
    fun conevrtTime() {

        val calendar = Calendar.getInstance()
        Log.e("hour==", "after ====== " + calendar.get(Calendar.HOUR_OF_DAY) + " " + calendar.get(Calendar.MINUTE))
        calendar.timeZone = TimeZone.getTimeZone("America/New_York")
        calendar.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 28)
        calendar.set(Calendar.SECOND, 0)

        val istDf: SimpleDateFormat = SimpleDateFormat("MM dd yyyy HH:mm");
        val etDf: SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy 'at' hh:mma 'ET'");

        Log.e("mytimezone", "===== " + TimeZone.getDefault().id)
        istDf.setTimeZone(TimeZone.getDefault())

        val etTimeZone: TimeZone = TimeZone.getTimeZone("America/New_York")
        etDf.setTimeZone(etTimeZone)

        System.out.println("india  " + istDf.format(calendar.getTimeInMillis()));

        //In ET Time
        System.out.println("newyork" + etDf.format(calendar.getTimeInMillis()));
    }

    fun getDate(milliSeconds: Long): String {
        // Create a DateFormatter object for displaying date in specified
        // format.
        val formatter: SimpleDateFormat = SimpleDateFormat("HH:mm")
        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun setAlarm(reqCode: Int, time: Long) {

        Log.e("time", "==== time " + time + "  " + AlarmManager.INTERVAL_DAY)
        val alarmIntent = Intent(this,
                AlarmReciever::class.java)
        val pi = PendingIntent.getBroadcast(this, reqCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent.putExtra("time", time)
        alarmIntent.putExtra("requestCode", reqCode)

        // mgr.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pi);
        mgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, time,
                86400000 * 7, pi)
    }

    /**
     *  check user login and navigate
     */
    private fun checkandNavigate() {
        try {
            AppInstance.userObj = getUserObject(this)
            userid = AppInstance.userObj!!.getId()!!
            isBlocked = AppInstance.userObj!!.getIsBlocked()!!
            isNormalQuizComplete = AppInstance.userObj!!.getIsNormalQuizComplete()!!
            isBibleQuizComplete = AppInstance.userObj!!.getIsBibleQuizComplete()!!
            Log.e("SWAPLOG", "userid = $userid")
            Log.e("SWAPLOG", "DEVICE_TOKEN = " + getPrefString(this, PreferenceConnector.DEVICE_TOKEN))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /**
         *  check user did review or not
         */
        if (userid.length > 1) {
            if (isNetworkAvailable(this)) {

                val token = AppInstance.userObj!!.getToken()!!
                val p = RequestParams();
                p.add("userid", AppInstance.userObj!!.getId()!!)
                p.add("device_token", getPrefString(this, PreferenceConnector.DEVICE_TOKEN))
                p.add("deviceInfo", getDeviceInfo())
                p.add("appversion", BuildConfig.VERSION_NAME)
                p.add("lat", "" + getPrefString(this, PreferenceConnector.LAT))
                p.add("lng", "" + getPrefString(this, PreferenceConnector.LNG))
                p.add("city", "" + getPrefString(this, PreferenceConnector.CITY))
                p.add("country", "" + getPrefString(this, PreferenceConnector.COUNTRY))
                p.add("countryCode", "" + getPrefString(this, PreferenceConnector.COUNTRYCODE))
                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.CHECK_FOR_REVIEW_WS);
            } else {
                showDialogNoInternet()
            }
        } else {
            Handler().postDelayed({
                //call radio webservice
                navigateToCommonLogin()
            }, 500)
        }
    }

    /**
     *  check app subscription details
     */
    fun checkAppSubscriptionDetail(userid: String, isBlocked: Boolean, isNormalQuizComplete: Boolean, isBibleQuizComplete: Boolean, isSubscriptionFree: Boolean, isSubscriptionDone: Boolean) {
        try {
            if (isNetworkAvailable(this)) {
                this.isSubscriptionFree = isSubscriptionFree
                this.isSubscriptionDone = isSubscriptionDone
                val token = AppInstance.userObj!!.getToken()!!

                val p = RequestParams();
                p.add("userid", AppInstance.userObj!!.getId()!!)
                p.add("packageName", BuildConfig.APPLICATION_ID)
                p.add("device_type", DEVICE_TYPE)
                p.add("productId", ITEM_SKU)

                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.APP_SUBSCRIPTION_DETAIL_WS);
            } else {
                showDialogNoInternet()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, isSubscriptionFree, isSubscriptionDone)
            logFirebaseEvents("checkAppSubscriptionDetail", "Error Message: " + e.printStackTrace())
        }
    }

    /**
     *  change navigation bottom bar color
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
     *  set all text sizes
     */
    private fun setTextSizes() {
        val size25 = getFontSize(this, 25)
        val size20 = getFontSize(this, 20)
        binding!!.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding!!.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding!!.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding!!.tvLoading.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
    }

    /**
     *  generate Key Hash
     */
    @SuppressLint("WrongConstant")
    private fun generateKeyHash() {
        // Add code to print out the key hash
        try {
            var hashCode = ""
            val packageName = application.packageName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val info = packageManager.getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES)
                if (info.signatures != null) {
                    for (signature in info.signatures) {
                        val md = MessageDigest.getInstance("SHA")
                        md.update(signature.toByteArray())
                        Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                        hashCode = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                        Log.e("KeyHash: hashCode", hashCode)
                    }
                }
            } else {
                val info = packageManager.getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNATURES)
                if (info.signatures != null) {
                    for (signature in info.signatures) {
                        val md = MessageDigest.getInstance("SHA")
                        md.update(signature.toByteArray())
                        Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                        hashCode = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                        Log.e("KeyHash: hashCode", hashCode)
                    }
                }
            }
            //showToast(this, "Key Hash : " + hashCode, Toast.LENGTH_LONG)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *  get Radio Music List
     */
    fun getRadioMusicList() {
        try {
            if (isNetworkAvailable(this)) {
                val manager = NetworkManager()
                manager.createApiRequest(ApiUtilities.getAPIService().getRadioMusicList(), object : ServiceListener<RadioListResponse> {
                    override fun getServerResponse(response: RadioListResponse, requestcode: Int) {
                        try {
                            if (response.code == ResponseCodes.Success) {
                                try {
                                    setRadio(response.data!!)
                                } catch (e: Exception) {
                                    setRadio(ArrayList<RadioListResponse.MusicUrl>())
                                    e.printStackTrace()
                                }
                            } else {
                                setRadio(ArrayList<RadioListResponse.MusicUrl>())
                                Log.e("SWAPLOG", response.message!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            setRadio(ArrayList<RadioListResponse.MusicUrl>())
                            logFirebaseEvents("getRadioMusicList", "Error Message: " + e.printStackTrace())
                        }
                    }

                    override fun getError(error: ErrorModel, requestcode: Int) {
                        setRadio(ArrayList<RadioListResponse.MusicUrl>())
                        Log.e("SWAPLOG", error.error_message)
                        logFirebaseEvents("getRadioMusicList", error.error_message)

                    }
                })
            } else {
                showDialogNoInternet()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setRadio(ArrayList<RadioListResponse.MusicUrl>())
            logFirebaseEvents("getRadioMusicList", "Error Message: " + e.printStackTrace())

        }
    }

    /**
     *  show no internet dialog
     */
    fun showDialogNoInternet() {
        try {
            SwapdroidAlertDialog.Builder(this)
                    .setTitle(getString(R.string.ooops))
                    .setMessage(getString(R.string.check_internet))
                    .isMessageVisible(true)
                    .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                    .setNegativeBtnText(getString(R.string.ok))
                    .isNegativeVisible(false)
                    .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                    .setPositiveBtnText(getString(R.string.ok))
                    .isPositiveVisible(true)
                    .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                    .setAnimation(SwapdroidAnimation.POP)
                    .isCancellable(false)
                    .showCancelIcon(false)
                    .setIcon(R.drawable.ic_nointernet_icon, SwapdroidIcon.Visible)  //ic_star_border_black_24dp
                    .OnPositiveClicked {
                        callReviewApi()
                    }
                    .build()
        } catch (e: Exception) {
            e.printStackTrace()
            showDialogNoInternet(this, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
        }
    }

    private fun showNoInternetMessage() {
        Alerter.create(this)
                .setTitle(getString(R.string.no_internet))
                .setText(getString(R.string.please_check_internet))
                .setBackgroundColorRes(R.color.colorRedLight)
                .setDuration(5000)
                .show()
    }

    /**
     *  Set radio and further process
     */
    private fun setRadio(musicList: ArrayList<RadioListResponse.MusicUrl>) {
        /*try {
            if (musicList.size != 0) {
                setupRadioPlayer(musicList)
            } else {
                setDefaultValuesToRadio()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setDefaultValuesToRadio()
        }

        //check and navigate
        checkandNavigate()*/
    }

    override fun onStart() {
        super.onStart()
        // Branch init
        // Branch.sessionBuilder(this).withData(this.intent?.data).init()
        initBranchIO();
        savePrefBoolean(this, PreferenceConnector.IS_APP_LAUNCHED, true)
        val startServiceIntent = Intent(this, NetworkSchedulerService::class.java);
        startService(startServiceIntent);
    }

    private fun initBranchIO() {
        Branch.sessionBuilder(this).withCallback(object : Branch.BranchReferralInitListener {
            override fun onInitFinished(referringParams: JSONObject?, error: BranchError?) {
                if (error == null) {
                    try {
                        Log.i("BRANCH_SDK", referringParams.toString())
                        val gson = Gson()
                        val refferalData = gson?.fromJson(referringParams.toString(), ReferralData::class.java)
                        saveReferralData(applicationContext, refferalData)
                        //Toast.makeText(applicationContext, refferalData.referringUsername, Toast.LENGTH_SHORT).show()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e("BRANCH_SDK", error.message)
                }
            }
        }).withData(this.intent.data).init()

        // latest
        val sessionParams = Branch.getInstance().latestReferringParams
        Log.i("BRANCH_SDKS", sessionParams.toString())

        // first
        val installParams = Branch.getInstance().firstReferringParams
        Log.i("BRANCH_SDKI", installParams.toString())

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        // Branch reinit (in case Activity is already in foreground when Branch link is clicked)
        Branch.sessionBuilder(this).reInit()
    }

    override fun onStop() {
        stopService(Intent(this, NetworkSchedulerService::class.java));
        super.onStop()
    }

    /**
     *  Set default radio and further provess
     */
    private fun setDefaultValuesToRadio() {
        //prepare radio  https://www.soundhelix.com/audio-examples
        val musicLists: ArrayList<RadioListResponse.MusicUrl> = ArrayList<RadioListResponse.MusicUrl>()
        val mval1 = RadioListResponse.MusicUrl()
        val mval2 = RadioListResponse.MusicUrl()
        val mval3 = RadioListResponse.MusicUrl()
        mval1._id = "1"
        mval1.musicUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        mval2._id = "2"
        mval2.musicUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        mval3._id = "3"
        mval3.musicUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        musicLists.add(mval1)
        musicLists.add(mval2)
        musicLists.add(mval3)
        //setupRadioPlayer(musicLists)
    }

    /**
     *  check and navigate
     */
    private fun checkAndPerformNavigation(userid: String, isBlocked: Boolean, isNormalQuizComplete: Boolean, isBibleQuizComplete: Boolean, isSubscriptionFree: Boolean, isSubscriptionDone: Boolean) {
        try {
            val intentNavigate = if (userid.length > 1 && !isBlocked) {
                if (!isNormalQuizComplete || !isBibleQuizComplete) {
                    Intent(this@Splash, TermsAndConActivity::class.java)
                } else if (!isSubscriptionFree && !isSubscriptionDone) {  // Subscription Free and Done both false then navigate to subscription

                    //commenting subscription code
                    //Intent(this, SubscriptionActivity::class.java)

                    Intent(this, WaitingActivity::class.java)

                } else {
                    if (intent.hasExtra(ALERT)) {
                        Intent(this, WaitingActivity::class.java)
                                .putExtra(ALERT, intent.getStringExtra(ALERT))
                                .putExtra(TIMESLOT, intent.getStringExtra(TIMESLOT))
                    } else if (intent.hasExtra(NOTIFICATION_TYPE)) {
                        Intent(this, WaitingActivity::class.java)
                                .putExtra(REJOIN_USERNAME, intent.getStringExtra(REJOIN_USERNAME))
                                .putExtra(REJOIN_USERID, intent.getStringExtra(REJOIN_USERID))
                                .putExtra(REJOIN_TYPE, intent.getStringExtra(REJOIN_TYPE))
                                .putExtra(NOTIFICATION_TYPE, intent.getStringExtra(NOTIFICATION_TYPE))
                    } else
                        Intent(this, WaitingActivity::class.java)
                }
            } else {
                Intent(this, CommonLoginActivity::class.java)
            }
            intentNavigate.putExtra(SOURCE, COMMON_LOGIN)
            intentNavigate.putExtra(VIA, NORMAL)
            startActivity(intentNavigate)
            this@Splash.finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
        } catch (e: Exception) {
            e.printStackTrace()
            navigateToCommonLogin()
        }
    }

    /**
     *  navigation to Review User
     */
    private fun navigateToReviewUser(to_userid: String) {
        val intent = Intent(this, ReviewActivity::class.java)
        intent.putExtra(TOID, to_userid)
        startActivity(intent)
        this@Splash.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     *  navigation to common login
     */
    private fun navigateToCommonLogin() {
        //call common login intent
        startActivity(Intent(this, CommonLoginActivity::class.java))
        this@Splash.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     *  navigation to user account blocked
     */
    private fun signUpSuccessOrFailed(tag: String) {
        val intent = Intent(this, OtherScreensActivity::class.java)
        intent.putExtra(SOURCE, tag)
        startActivity(intent)
        this@Splash.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * clear all notifications from notification bar
     */
    private fun clearAllNotifications() {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scheduleBackgroundProcess(context: Context) {
        val alarmIntent = Intent(context, BackgroundReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, 20)
        calendar.time = Date()
        mgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pi)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun scheduleNetworkJob() {
        val myJob = JobInfo.Builder(0, ComponentName(this, NetworkSchedulerService::class.java))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build()

        val jobScheduler: JobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(myJob);
    }

    private fun checkAppSubscriptionStatus() {
        val token = AppInstance.userObj!!.getToken()!!
        val p = RequestParams();
        p.add("userid", userid)

        val rest = RestClient(this, RestMethod.GET, p)
        rest.setToken(token);
        rest.execute(WebConstants.APP_SUBSCRIPTION_STATUS_WS);
    }

    private fun autoReviewApiCall() {
        try {
            if (isNetworkAvailable(this)) {
                val token = AppInstance.userObj!!.getToken()!!

                val p = RequestParams();
                p.add("review_from", AppInstance.userObj!!.getId()!!)
                p.add("review_for", to_userid)
                p.add("isInterested", true)
                p.add("pleasant", "3")
                p.add("attractive", "3")
                p.add("religious", "3")
                p.add("notes", "")
                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.ADD_REVIEW_WS);
            } else {
                showDialogNoInternet(this@Splash, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("SWAPLOG", e.message!!)
            showDialogNoInternet(this@Splash, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
        }
    }


    fun logFirebaseEvents(key: String, value: String?) {
        val params = Bundle()
        params.putString(key, value)
        firebaseAnalytics?.logEvent(TAG, params)
    }

    override fun asyncResponse(responseData: String?, label: String?, `object`: Any?) {
        val gson = Gson();
        println(responseData);
        when (label) {
            WebConstants.ADD_REVIEW_WS -> {
                savePrefString(this, REVIEW_TO_USER_ID, "")
                val response = gson.fromJson(responseData, AddReviewResponse1::class.java);
                when (response.code) {
                    ResponseCodes.Success -> {
                        Log.e("SWAPLOG", response.message!!)
                        if (userid.length > 1 && !reviewResponse.data?.isSubscriptionFree!! && reviewResponse.data?.isSubscriptionDone!!) {  // Subscription Not Free and Subscription Done then navigate to check subscription
                            // Subscription Not Free and Subscription Done then navigate to check subscription
                            checkAppSubscriptionDetail(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, reviewResponse.data?.isSubscriptionFree!!, reviewResponse.data?.isSubscriptionDone!!)
                        } else if (!reviewResponse.data?.isSubscriptionFree!! && !reviewResponse.data?.isSubscriptionDone!!) {
                            checkAppSubscriptionDetail(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, reviewResponse.data?.isSubscriptionFree!!, reviewResponse.data?.isSubscriptionDone!!)
                        } else {
                            checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, reviewResponse.data?.isSubscriptionFree!!, reviewResponse.data?.isSubscriptionDone!!)
                        }
                    }
                    ResponseCodes.ACCESS_TOKEN_EXPIRED -> expireAccessToken(this)
                    else -> {
                        Log.e("SWAPLOG", response.message!!)
                        showDialogNoInternet(this, response.message!!, "", R.drawable.ic_alert_icon)
                    }
                }
            }

            WebConstants.CHECK_FOR_REVIEW_WS -> {
                try {
                    println("check_for_reviews***$responseData");
                    reviewResponse = gson.fromJson(responseData, CheckReviewResponse::class.java);
                    when (reviewResponse.code) {
                        ResponseCodes.Success -> {//success
                            AppInstance.userObj!!.setIsSubscriptionDone(reviewResponse.data?.isSubscriptionDone!!)
                            AppInstance.userObj!!.setIsSubscriptionFree(reviewResponse.data?.isSubscriptionFree!!)
                            addData(this@Splash, AppInstance.userObj!!.getLoginType()!!, userid, AppInstance.userObj!!.getFirstName()!!, AppInstance.userObj!!.getLastName()!!, AppInstance.userObj!!.getEmail()!!, AppInstance.userObj!!.getGender()!!, AppInstance.userObj!!.getDOB()!!, AppInstance.userObj!!.getToken()!!, isNormalQuizComplete, isBibleQuizComplete, AppInstance.userObj!!.getIsActive()!!, AppInstance.userObj!!.getFacebookSocialId()!!, AppInstance.userObj!!.getGoogleSocialId()!!, AppInstance.userObj!!.getPassword()!!, AppInstance.userObj!!.getIsSignUpPerformed()!!, isBlocked, reviewResponse.data?.isSubscriptionDone!!, reviewResponse.data?.isSubscriptionFree!!, reviewResponse.data?.phone_no!!)

                            savePrefBoolean(this@Splash, PreferenceConnector.IS_LOCATION_ALARM_ENABLED, reviewResponse.data!!.isLocationAlarmEnabled)
                            savePrefBoolean(this@Splash, PreferenceConnector.IS_GENDER_ALARM_ENABLED, reviewResponse.data!!.isGenderAlarmEnabled)
                            savePrefBoolean(this@Splash, PreferenceConnector.SHOW_SCHEDULING_SCREEN, reviewResponse.data!!.showSchedulingScreen)
                            savePrefBoolean(this@Splash, PreferenceConnector.SAID_YES_IN_REVIEW, reviewResponse.data!!.saidYesInReview)
                            Log.e("SHOW_SCHEDULING_SCREEN", "======= " + reviewResponse.data!!.showSchedulingScreen)

                            if (userid.length > 1 && !reviewResponse.data?.isSubscriptionFree!! && reviewResponse.data?.isSubscriptionDone!!) {  // Subscription Not Free and Subscription Done then navigate to check subscription
                                // Subscription Not Free and Subscription Done then navigate to check subscription
                                checkAppSubscriptionDetail(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, reviewResponse.data?.isSubscriptionFree!!, reviewResponse.data?.isSubscriptionDone!!)
                            } else if (!reviewResponse.data?.isSubscriptionFree!! && !reviewResponse.data?.isSubscriptionDone!!) {
                                checkAppSubscriptionDetail(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, reviewResponse.data?.isSubscriptionFree!!, reviewResponse.data?.isSubscriptionDone!!)
                            } else {
                                checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, reviewResponse.data?.isSubscriptionFree!!, reviewResponse.data?.isSubscriptionDone!!)
                            }

                        }
                        ResponseCodes.ReviewNotDone -> { //review not done
                            if (reviewResponse.data!!.callData != null && reviewResponse.data!!.callData?.fromId != null) {
                                savePrefString(this!!, PreferenceConnector.SESSION_ID, reviewResponse.data!!.callData?.fromUserSessionId!!)
                                savePrefString(this!!, PreferenceConnector.MATCH_USER_ID, reviewResponse.data!!.callData?.toId!!)
                                savePrefInt(this!!, PreferenceConnector.VIDEO_CALL_COUNT, reviewResponse.data!!.callData?.count!!)
                            }
                            AppInstance.userObj!!.setIsSubscriptionDone(reviewResponse.data?.isSubscriptionDone!!)
                            AppInstance.userObj!!.setIsSubscriptionFree(reviewResponse.data?.isSubscriptionFree!!)
                            addData(this@Splash, AppInstance.userObj!!.getLoginType()!!, userid, AppInstance.userObj!!.getFirstName()!!, AppInstance.userObj!!.getLastName()!!, AppInstance.userObj!!.getEmail()!!, AppInstance.userObj!!.getGender()!!, AppInstance.userObj!!.getDOB()!!, AppInstance.userObj!!.getToken()!!, isNormalQuizComplete, isBibleQuizComplete, AppInstance.userObj!!.getIsActive()!!, AppInstance.userObj!!.getFacebookSocialId()!!, AppInstance.userObj!!.getGoogleSocialId()!!, AppInstance.userObj!!.getPassword()!!, AppInstance.userObj!!.getIsSignUpPerformed()!!, isBlocked, reviewResponse.data?.isSubscriptionDone!!, reviewResponse.data?.isSubscriptionFree!!, reviewResponse.data?.phone_no!!)

                            to_userid = reviewResponse.data!!.reviewFor!!
                            // if (getPrefString(this, REVIEW_TO_USER_ID).isNotEmpty())
                            // navigateToReviewUser(to_userid)
                            autoReviewApiCall()
                        }
                        ResponseCodes.AccountBlocked -> //account blocked
                            signUpSuccessOrFailed(SIGNUP_FAILED)
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> //expire Access token
                            expireAccessToken(this@Splash)
                        else -> {
                            Log.e("SWAPLOG", reviewResponse.message!!)
                            navigateToCommonLogin()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    navigateToCommonLogin()
                    logFirebaseEvents("checkandNavigate", "Error Message: " + e.printStackTrace())
                }

            }
            WebConstants.APP_SUBSCRIPTION_STATUS_WS -> {

                println("subscription_status ***" + responseData);
                try {
                    val response = gson.fromJson(responseData, SubscriptionStatus::class.java);
                    if (!response.data?.subscriptionStatus!!)
                        checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, isSubscriptionFree, isSubscriptionDone)
                    else {
                        AppInstance.userObj!!.setIsSubscriptionDone(response.data?.subscriptionStatus!!)
                        AppInstance.userObj!!.setIsSubscriptionFree(response.data?.subscriptionStatus!!)
                        checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, response.data?.subscriptionStatus!!, response.data?.subscriptionStatus!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    navigateToCommonLogin()
                    logFirebaseEvents("checkandNavigate", "Error Message: " + e.printStackTrace())
                }
            }

            WebConstants.APP_SUBSCRIPTION_DETAIL_WS -> {
                println("check_for_subscription***$responseData");
                try {
                    val response = gson.fromJson(responseData, CheckSubscriptionResponse::class.java);
                    when (response.code) {
                        ResponseCodes.Success -> //success
                        {
                            if (response.data.payload.cancelReason == -1 && response.data.payload.paymentState == 2) { // not cancel and free
                                checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, isSubscriptionFree, isSubscriptionDone)
                            } else if (response.data.payload.cancelReason != -1 && response.data.payload.paymentState == 2) { // cancel and free
                                checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, isSubscriptionFree, isSubscriptionDone)
                            } else if (response.data.payload.cancelReason != -1) { // cancel and not free
                                Log.d("whatsssthetime", response.data.payload.expiryTimeMillis.toString() + "   " + System.currentTimeMillis())
                                if (System.currentTimeMillis() > response.data.payload.expiryTimeMillis && !response.data.payload.autoRenewing)
                                    checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, false, false)
                                else
                                    showSubscriptionCancelDialog(this@Splash, getString(R.string.renew_cancellation))
                            } else { //not cancelled
                                when (response.data.payload.paymentState) {
                                    0 -> { // 0 Payment pending
                                        showSubscriptionCancelDialog(this@Splash, getString(R.string.payment_pending))
                                    }
                                    1 -> { // 1 Payment received
                                        checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, isSubscriptionFree, isSubscriptionDone)
                                    }
                                    2 -> { // 2 Free trial
                                        checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, isSubscriptionFree, isSubscriptionDone)
                                    }
                                    3 -> { // 3 Pending deferred upgrade/downgrade
                                        showSubscriptionCancelDialog(this@Splash, getString(R.string.price_changed))
                                    }
                                }
                            }
                        }
                        else -> {
                            Log.e("SWAPLOG", response.message + "  " + isSubscriptionFree + "  " + isSubscriptionDone)
                            checkAppSubscriptionStatus()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, isSubscriptionFree, isSubscriptionDone)
                    logFirebaseEvents("checkAppSubscriptionDetail", "Error Message: " + e.printStackTrace())
                }

            }
        }

    }

    override fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?) {
        when (label) {
            WebConstants.ADD_REVIEW_WS -> {
                Log.e("SWAPLOG", responseData!!.error_message)
                navigateToReviewUser(to_userid)
            }
            WebConstants.CHECK_FOR_REVIEW_WS -> {
                setRadio(ArrayList<RadioListResponse.MusicUrl>())
                Log.e("SWAPLOG", responseData!!.error_message)
                navigateToCommonLogin()
                logFirebaseEvents("checkandNavigate", responseData!!.error_message)
            }
            WebConstants.APP_SUBSCRIPTION_DETAIL_WS -> {
                Log.e("SWAPLOG", responseData!!.error_message)
                checkAndPerformNavigation(userid, isBlocked, isNormalQuizComplete, isBibleQuizComplete, isSubscriptionFree, isSubscriptionDone)
                logFirebaseEvents("checkAppSubscriptionDetail", responseData.error_message!!)
            }
        }
    }

}
