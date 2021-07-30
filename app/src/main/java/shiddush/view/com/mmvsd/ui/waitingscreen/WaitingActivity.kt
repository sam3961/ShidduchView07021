package shiddush.view.com.mmvsd.ui.waitingscreen

import android.Manifest
import android.animation.*
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.gson.Gson
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.smartlook.sdk.smartlook.Smartlook
import com.smartlook.sdk.smartlook.analytics.identify.UserProperties
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter
import com.tooltip.Tooltip
import com.yuyakaido.android.cardstackview.*
import io.branch.referral.Branch

import kotlinx.android.synthetic.main.activity_waiting.*
import kotlinx.android.synthetic.main.bottom_sheet_drop_call.*
import kotlinx.android.synthetic.main.bottom_sheet_player.*
import kotlinx.android.synthetic.main.dialog_add_phone_number.*
import kotlinx.android.synthetic.main.dialog_add_phone_number.skDialogLoader
import kotlinx.android.synthetic.main.dialog_alert_notification.*
import kotlinx.android.synthetic.main.dialog_reconnect_call.*
import kotlinx.android.synthetic.main.dialog_reconnect_call.tvMessage
import kotlinx.android.synthetic.main.dialog_waiting_before_date.*
import org.json.JSONObject
import org.puder.highlight.HighlightManager
import org.puder.highlight.internal.callback.TutorialClickListener
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import shiddush.view.com.mmvsd.MyApplication
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityWaitingBinding
import shiddush.view.com.mmvsd.model.CallData
import shiddush.view.com.mmvsd.model.MainModel
import shiddush.view.com.mmvsd.model.chat.ReceivedMessageResponse
import shiddush.view.com.mmvsd.model.onlineOffline.OnlineOfflineResponse
import shiddush.view.com.mmvsd.model.questions.AIQuestionsResponse
import shiddush.view.com.mmvsd.model.questions.Data
import shiddush.view.com.mmvsd.model.timer.CountDownTimerResponse
import shiddush.view.com.mmvsd.model.updatePhone.UpdatePhoneResponse
import shiddush.view.com.mmvsd.model.videocall.SocketCallResponse
import shiddush.view.com.mmvsd.model.waitingscreenmodels.YoutubeLinksData
import shiddush.view.com.mmvsd.model.waitingscreenmodels.YoutubeLinksResponse
import shiddush.view.com.mmvsd.radioplayer.RadioPlayer.pauseRadio
import shiddush.view.com.mmvsd.radioplayer.RadioPlayer.playRadio
import shiddush.view.com.mmvsd.radioplayer.RadioPlayer.setIsInWaitingScreen
import shiddush.view.com.mmvsd.repository.ErrorModel
import shiddush.view.com.mmvsd.repository.ResponseCodes
import shiddush.view.com.mmvsd.repository.WebConstants
import shiddush.view.com.mmvsd.rest.RequestParams
import shiddush.view.com.mmvsd.rest.RestClient
import shiddush.view.com.mmvsd.rest.RestMethod
import shiddush.view.com.mmvsd.socket.SocketCallBackListeners
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.ui.chat.ChatActivity
import shiddush.view.com.mmvsd.ui.commonloginscreen.CommonLoginActivity
import shiddush.view.com.mmvsd.ui.createResume.CreateResumeActivity
import shiddush.view.com.mmvsd.ui.introvideo.IntroVideoActivity
import shiddush.view.com.mmvsd.ui.schedule.ScheduleFormActivity
import shiddush.view.com.mmvsd.ui.services.NetworkSchedulerService
import shiddush.view.com.mmvsd.ui.subscription.SubscriptionActivity
import shiddush.view.com.mmvsd.ui.videocall.OpenTokConfig
import shiddush.view.com.mmvsd.ui.videocall.VideoCallActivity
import shiddush.view.com.mmvsd.ui.welcome.WelcomeActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.widget.loader.UtilLoader
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidColorConstants
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * Created by Sumit Kumar.
 * this is the Waiting screen
 * https://stackoverflow.com/questions/7324759/how-to-display-thumbnail-of-youtube-videos-in-android
 * https://github.com/PierfrancescoSoffritti/android-youtube-player
 **/

class WaitingActivity : AppCompatActivity(), YoutubeListAdapter.OnClickPlayListeners,
    TrainingTheAIAdapter.OnClickCardListeners, CardStackListener,
    EasyPermissions.PermissionCallbacks,
    SocketCallBackListeners, TutorialClickListener, RestClient.OnAsyncRequestComplete,
    RestClient.OnAsyncRequestError,
    RejoinListAdapter.OnRejoinClickListeners, BestMatchAdapter.OnBestMatchClickListeners,
    PickiTCallbacks, NetworkSchedulerService.onNetworkConnectionListener {

    private lateinit var pickiT: PickiT
    private var availableVersion = 0.0
    private var mediaPlayer: MediaPlayer? = null
    private var matcheUserName = ""
    private var appUpdateManager: AppUpdateManager? = null
    val IN_APP_UPDATE: Int = 101
    private lateinit var addPhoneDialog: Dialog
    private var reconnectDialog: Dialog? = null
    private var dialogContacting: Dialog? = null

    //private lateinit var youtubeDialog: BottomSheetDialog
    private var reconnectStatus = ""
    lateinit var binding: ActivityWaitingBinding
    private lateinit var viewModel: WaitingViewModel
    private lateinit var mUtilLoader: UtilLoader
    private var screenLock: PowerManager.WakeLock? = null
    private var walkOverIndex = 0
    private lateinit var shrinkGrowAnimation: ObjectAnimator
    private var swingAnimation: Animation? = null

    //timer
    private var timer: CountDownTimer? = null
    private var timerCallReconnect: CountDownTimer? = null
    private val extra15Secs: Long = 15000
    private var waitingTimeDuration: Long = 60000
    private var isTimerEnd: Boolean = false

    //youtube
    private var youtubeListAdapter: YoutubeListAdapter? = null
    private var rejoinListAdapter: RejoinListAdapter? = null
    private var bestMatchAdapter: BestMatchAdapter? = null
    private var youtubeListData = ArrayList<YoutubeLinksData>()
    private var rejoinListData = ArrayList<OnlineOfflineResponse>()
    private var isReady: Boolean = false
    private var youTubeVideoPlayer: YouTubePlayer? = null
    private var playerHeight = 200
    private var isFullScreenCall: Boolean = false
    private var isClickable: Boolean = false
    private var beforeOnPause: Boolean = false
    private var isRejoinListEmpty: Boolean = false
    private var filePath = ""


    //socket communication
    lateinit var mSocketCallBackListeners: SocketCallBackListeners

    //training the ai
    lateinit var cardStackView: CardStackView
    lateinit var cardStackLayoutManager: CardStackLayoutManager
    private lateinit var trainingTheAIAdapter: TrainingTheAIAdapter
    private var trainingTheAIData = ArrayList<Data>()
    private var cardHeight = 300
    private var currentVideoId: String = ""
    private var currentVideoDuration: Float = 0F
    private var currentVideoState: Boolean = false
    private val ADD_VIDEO_TASK_REQUEST: Int = 112
    private var cardItemSize: Int = 0
    private val TAG = WaitingActivity::class.java.simpleName
    private var highlightManager: HighlightManager? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var waitingDialog: Dialog? = null
    private lateinit var mBottomSheetCallBehaviour: BottomSheetBehavior<*>
    private lateinit var mBottomSheetYoutubeBehaviour: BottomSheetBehavior<*>
    private var hadCall = false
    private var imagePath = ""
    private var isResumeSelected = false

    companion object {
        private val LOG_TAG = WaitingActivity::class.java.simpleName
        private val RC_SETTINGS_SCREEN_PERM = 123
        private const val RC_VIDEO_APP_PERM = 124
        private const val RC_PDF_IMAGE = 125
        private const val REQUEST_IMAGE_CAPTURE = 126
        private const val RC_STORAGE_APP_PERM = 127
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.purple_bar)
        }
        //firebase initialization and set user id
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setUserId(getUserObject(this).getEmail());
        initializeSmartLook()


        //branch io
        Branch.getInstance().setIdentity(getUserObject(this).getEmail()!!)

        try {
            //timer
            callTimerWebService()

            socketCommunication()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // highlightManager = HighlightManager(this)
        highlightManager?.nextClick(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_waiting)
        viewModel = WaitingViewModel()
        binding.viewModel = viewModel
        mUtilLoader = UtilLoader(this)
        NetworkSchedulerService.registerForNetworkService(this, this)
        pickiT = PickiT(this, this, this)

        setAllLayouts()
        requestPermissions()
        onClickListener()
        try {
            this.clearCache()
            checkForVersionUpdate(true)
            checkIfResumeUploaded()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Handler().postDelayed(Runnable {
            if (intent.hasExtra(SHOW_TUTORIALS) && intent.getBooleanExtra(SHOW_TUTORIALS, false)) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.putExtra(SOURCE, SHOW_TUTORIALS)
                startActivity(intent)
            } else if (intent.hasExtra(NOTIFICATION_TYPE)) {
                if (intent.getStringExtra(NOTIFICATION_TYPE).equals(MESSAGE)) {

                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivityForResult(
                            Intent(this, ChatActivity::class.java)
                                .putExtra(REJOIN_USERID, intent.getStringExtra(REJOIN_USERID)!!)
                                .putExtra(REJOIN_TYPE, intent.getStringExtra(REJOIN_TYPE)!!)
                                .putExtra(REJOIN_USERNAME, intent.getStringExtra(REJOIN_USERNAME)!!)
                                .putExtra(CHAT_USER_TYPE, BEST_MATCH), CHAT_SCREEN_CODE
                        )
                        // finish()
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }, 2000)


                } else {
                    playRejoinSound()
                    reconnectStatus = "accept"
                    matcheUserName = intent.getStringExtra(REJOIN_USERNAME)!!
                    if (matcheUserName?.contains(" ")!!)
                        matcheUserName = matcheUserName.split(" ")[0]

                    showCallReconnectDialog(
                        AppInstance.userObj!!.getId()!!,
                        intent.getStringExtra(REJOIN_USERID)!!,
                        matcheUserName,
                        intent.getStringExtra(REJOIN_TYPE)!!
                    )

                    //  SocketCommunication.emitRejoinRequest(intent.getStringExtra(REJOIN_USERID)!!, intent.getStringExtra(REJOIN_USERNAME)!!)
                }
            }
            //           else if (intent.hasExtra(ALERT)) {
//                showInterestInUpComingCall()
//            }
        }, 500)

        if (getPrefBoolean(this, PreferenceConnector.SHOW_SCHEDULING_SCREEN)) {
            val intent = Intent(this, ScheduleFormActivity::class.java)
            startActivity(intent)
        }

        //turnScreenOnAndKeyguardOff()

    }


    private fun checkIfResumeUploaded() {
        val storage = FirebaseStorage.getInstance()
        val listRef = storage.reference.child("resume/" + AppInstance.userObj?.getEmail()!!)
        //val listRef = storage.reference.child("resume/" + "sumitttk@gmail.com")
        listRef.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                //Log.d("checkIfUploadedNAME", )
                val resumeCount = items.filter { s -> s.name.startsWith("resume_") }
                val picCount = items.filter { s -> s.name.startsWith("pic_") }
                Log.d("checkIfUploadedCount", resumeCount.size.toString() + "  " + picCount.size)

                if (resumeCount.isNotEmpty() && picCount.isNotEmpty()) {
                    binding.llUploadResumeContainer.visibility = View.GONE
                } else {
                    binding.llUploadResumeContainer.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                Log.d("checkIfResumeUploaded", it.message!! + " ")
                // Uh-oh, an error occurred!
            }
    }

    private fun animateTimerOrRejoin(hadCall: Boolean) {
        if (getPrefBoolean(this, PreferenceConnector.IS_APP_LAUNCHED)) {
            if (isRejoinListEmpty) {
                if (hadCall)
                    animateHadDated()
                else
                    animateTimerClick()
            } else {
//                if (rvRejoinDate.visibility == View.VISIBLE)
//                    animateRejoin()
                //    if (binding.llUploadResumeContainer.visibility == View.VISIBLE)
                //      binding.nsvParent.focusOnView(binding.llUploadResumeContainer);
                // else
                //   binding.nsvParent.focusOnView(binding.viewTop);
            }

//            if (AppInstance.userObj?.getPhone() == null || AppInstance.userObj?.getPhone()?.isEmpty()!!)
//                showAddPhoneDialog()

            savePrefBoolean(this, PreferenceConnector.IS_APP_LAUNCHED, false)
        }

        if (intent.hasExtra(VIA) && intent.getStringExtra(VIA).equals(REVIEW_SUCCESS)
            && youtubeListData.size > 0
        ) {
            //play youtube video
            Handler(Looper.getMainLooper()).postDelayed({
                if (youTubeVideoPlayer != null)
                    onPlayClick(youtubeListData!!.get(0).getVideoId()!!)
            }, 4000)
        }


    }

    private fun initializeSmartLook() {
        val userProperties = UserProperties()
        //user properties
        userProperties.putEmail(getUserObject(this).getEmail()!!)
        userProperties.putName(getUserObject(this).getFirstName()!!)
        userProperties.putUsername(getUserObject(this).getFirstName()!!)
        Smartlook.setUserProperties(userProperties)

        //user identifier
        Smartlook.setUserIdentifier(getUserObject(this).getEmail()!!)
    }


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

    private fun setTextSizes() {
        try {
            //set youtube
            binding.llYoutubeVideoContainer.visibility = View.INVISIBLE
            binding.llNoYoutubeLinks.visibility = View.INVISIBLE
            binding.skLoader.visibility = View.VISIBLE

            //set card view
            binding.cardStackView.visibility = View.INVISIBLE
            binding.llNoQuestions.visibility = View.INVISIBLE
            binding.skAILoader.visibility = View.VISIBLE

            val size140 = getFontSize(this, 140)
            val size100 = getFontSize(this, 100)
            val size70 = getFontSize(this, 70)
            val size60 = getFontSize(this, 60)
            val size80 = getFontSize(this, 80)
            val size40 = getFontSize(this, 40)
            val size34 = getFontSize(this, 34)
            val size30 = getFontSize(this, 30)
            val size25 = getFontSize(this, 25)
            val size18 = getFontSize(this, 18)
            val size15 = getFontSize(this, 15)
            val size13 = getFontSize(this, 13)
            val size12 = getFontSize(this, 12)
            val size11 = getFontSize(this, 11)
            val size6 = getFontSize(this, 6)

            binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
            binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
            binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

            binding.tvTrainAI.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
            binding.tvRejoinListTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
            binding.tvBestMatchListTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
            binding.tvYouTubeClassesList.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
            binding.tvTimeZones.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)

            binding.tvGoesLiveIn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
            binding.tvHH.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size11)
            binding.tvFirstQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size11)
            binding.tvMM.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size11)
            binding.tvSecondQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size11)
            binding.tvSS.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size11)

            binding.tvHour.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
            binding.tvHHCOl.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
            binding.tvMin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
            binding.tvMMCOL.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
            binding.tvSec.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)

            binding.tvFirstTZ.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
            binding.tvSecondTZ.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
            binding.tvForthTZ.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

            binding.tvFirstMTTZ.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size15)
            binding.tvSecondMTTZ.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size15)
            binding.tvForthMTTZ.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size15)

            binding.tvNoQuestions.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
            binding.tvNoQuRetry.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
            binding.tvNoYoutubeLinks.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
            binding.tvNoYTRetry.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

            try {
                //  set Background Image
                val bgImage = getYoutubeBackImageSize(this)
                val bg80Image = getBackImage80Size(this)
                val bg40Image = getBackImage40Size(this)
                //   binding.llYoutubeVideoContainer.background = ContextCompat.getDrawable(this, bgImage)
                //   binding.llRejoinDateContainer.background = ContextCompat.getDrawable(this, bgImage)
                //   binding.llFirstTZ.background = ContextCompat.getDrawable(this, bg40Image)
                //   binding.llSecondTZ.background = ContextCompat.getDrawable(this, bg40Image)
                //   binding.llForthTZ.background = ContextCompat.getDrawable(this, bg40Image)
                //   binding.llNoQuestions.background = ContextCompat.getDrawable(this, bg80Image)
                //   binding.llNoYoutzubeLinks.background = ContextCompat.getDrawable(this, bg80Image)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val topImgHeight = getPercentHeightOfDevice(this, 0.14F)
                binding.viewTop.layoutParams.height = topImgHeight
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                //cardHeight
                val noIntHeight = dpToPxs(size100.toInt())
                val cardBotImgHeight = dpToPxs(size140.toInt())
                val cardBotViewHeight = dpToPxs(size70.toInt())
                val cardBottextHeight = dpToPxs(size60.toInt())
                val cardTzHeight = dpToPxs(size80.toInt())
                val cardBarHeight = dpToPxs(size40.toInt())
                val card30Height = dpToPxs(size30.toInt())
                val card20Height = dpToPxs(size25.toInt())
                val card6Height = dpToPxs(size6.toInt())
                binding.llFirstTZ.layoutParams.height = cardTzHeight
                binding.llSecondTZ.layoutParams.height = cardTzHeight
                binding.llForthTZ.layoutParams.height = cardTzHeight

                binding.viewFirstCircle.layoutParams.height = card30Height
                binding.viewFirstCircle.layoutParams.width = card30Height
                binding.viewSecondCircle.layoutParams.height = card30Height
                binding.viewSecondCircle.layoutParams.width = card30Height
                binding.viewForthCircle.layoutParams.height = card30Height
                binding.viewForthCircle.layoutParams.width = card30Height

                binding.viewFirstLine.layoutParams.width = card6Height
                binding.viewSecondLine.layoutParams.width = card6Height
                binding.viewForthLine.layoutParams.width = card6Height

                binding.viewFirstBar.layoutParams.height = cardBarHeight
                binding.viewSecondBar.layoutParams.height = cardBarHeight
                binding.viewForthBar.layoutParams.height = cardBarHeight
                binding.ivBottomImage.layoutParams.height = cardBotImgHeight
                binding.topView.layoutParams.height = cardBotViewHeight
                binding.includeBottom.layoutParams.height = cardBottextHeight

                binding.ivNoAIIcon.layoutParams.height = noIntHeight
                binding.ivNoAIIcon.layoutParams.width = noIntHeight
                binding.ivNoYoutubeIcon.layoutParams.height = noIntHeight
                binding.ivNoYoutubeIcon.layoutParams.width = noIntHeight

                //binding.ivAIHint.layoutParams.height = card20Height
                //binding.ivAIHint.layoutParams.width = card20Height
                // binding.ivYoutubeHint.layoutParams.height = card20Height
                //binding.ivYoutubeHint.layoutParams.width = card20Height
                binding.ivLiveTimeHint.layoutParams.height = card20Height
                binding.ivLiveTimeHint.layoutParams.width = card20Height

            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val cardRadious = dpToPxs(size34.toInt())
                binding.cvYoutube.radius = cardRadious.toFloat()
                binding.cvRejoin.radius = cardRadious.toFloat()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            binding.cardSchedule.getDimensions { width, height ->
                println("width = $width")
                println("height = $height")

                val params: ViewGroup.MarginLayoutParams =
                    llYouTubeContainer!!.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin = height
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

        //logout click listener
        try {
            binding.tvLogout.setOnClickListener {
                if (isNetworkAvailable(this)) {
                    showCommonDialog(true, getString(R.string.sure_to_logout))
                } else {
                    showDialogNoInternet(
                        this@WaitingActivity,
                        getString(R.string.ooops),
                        getString(R.string.check_internet),
                        R.drawable.ic_nointernet_icon
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inline fun View.getDimensions(crossinline onDimensionsReady: (Int, Int) -> Unit) {
        lateinit var layoutListener: ViewTreeObserver.OnGlobalLayoutListener
        layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
            onDimensionsReady(width, height)
        }
        viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    }

    private fun onClickListener() {
        binding.topLayout.setOnClickListener {
            //call get updated time api
            //callTimerWebService()

            // this function calling feature only for testing purpose to make direct video call
            //comment this feature for live


            binding.topLayout.isEnabled = false;
            navigateForVideoCall()

            // timerView(60000)
        }

        binding.llNextDate.setOnClickListener {

            binding.topLayout.isEnabled = false;
            navigateForVideoCall()

            //timerView(60000)
        }

        binding.llDonate.setOnClickListener {
            //showDialogDonation()
            openBrowser(DONATION_URL, this@WaitingActivity)
        }

        binding.ivRefreshMatch.setOnClickListener {
            binding.skBestMatchLoader.visibility = View.VISIBLE
            SocketCommunication.emitBestMatchRefresh()
        }


        binding.llbtnUploadPic.setOnClickListener {
            isResumeSelected = false
            requestPermissionsStorage()
        }
        binding.uploadPic.setOnClickListener {
            isResumeSelected = false
            requestPermissionsStorage()
        }
        binding.llbtnUploadResume.setOnClickListener {
            isResumeSelected = true
            requestPermissionsStorage()
        }
        binding.uploadResume.setOnClickListener {
            isResumeSelected = true
            requestPermissionsStorage()
        }

        binding.dropCallButton.setOnClickListener {
            mBottomSheetCallBehaviour!!.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.llNoQuestions.setOnClickListener {
            if (isNetworkAvailable(this)) {
                callQuestionsApiTree()
            } else {
                showDialogNoInternet(
                    this,
                    getString(R.string.ooops),
                    getString(R.string.check_internet),
                    R.drawable.ic_nointernet_icon
                )
                noTrainTheAiQuestions()
                setNoAiUI(0)
            }
        }


        binding.llNoYoutubeLinks.setOnClickListener {
            if (isNetworkAvailable(this)) {
                callYoutubeList()
            } else {
                showDialogNoInternet(
                    this,
                    getString(R.string.ooops),
                    getString(R.string.check_internet),
                    R.drawable.ic_nointernet_icon
                )
            }
        }

        binding.ivAIHint.setOnClickListener {
            val intent = Intent(this@WaitingActivity, WelcomeActivity::class.java)
            intent.putExtra(SOURCE, SHOW_TUTORIALS)
            startActivity(intent)
        }

        binding.ivYoutubeHint.setOnClickListener {
            val intent = Intent(this@WaitingActivity, WelcomeActivity::class.java)
            intent.putExtra(SOURCE, SHOW_TUTORIALS)
            startActivity(intent)
        }

        binding.ivLiveTimeHint.setOnClickListener {
            showToolTip(binding.ivLiveTimeHint, getString(R.string.timezone_tool_tip))
        }

        binding.tvGoesLiveIn.setOnClickListener {
            //            Toast.makeText(this, "scroll", Toast.LENGTH_SHORT).show()
            val scrollTo = (cvYoutube.parent.parent as View).top + cvYoutube.top
            val size = Point()
            windowManager.defaultDisplay.getSize(size)
            val screenHeight = size.y
            nsvParent.smoothScrollTo(scrollTo, screenHeight)
        }

        binding.ivRejoinHint.setOnClickListener {
            showToolTip(binding.ivRejoinHint, getString(R.string.rejoin_alert_window))
        }

        binding.ivUploadResumeHint.setOnClickListener {
            showToolTip(binding.ivUploadResumeHint, getString(R.string.resume_alert_window))

        }
    }

    private fun showDialogDonation() {
        try {
            val donationDialog = Dialog(this, R.style.myDialog)
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
                openBrowser(DONATION_URL, this@WaitingActivity)
                donationDialog!!.dismiss()
            }

            //dialog show
            donationDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun uploadImageOnFBStorage() {
        binding.skUploadLoader.visibility = View.VISIBLE
        val email = AppInstance.userObj?.getEmail()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val file = Uri.fromFile(File(imagePath))
        val riversRef = storageRef.child(
            "resume/" + email + "/${
                "pic_" + email + "_" + System.currentTimeMillis() + "." + File(imagePath).extension
            }"
        )
        val uploadTask = riversRef.putFile(file)
        uploadTask.addOnFailureListener {
            Log.w(TAG, "uploadFile:failure" + it.message.toString()!!)
            binding.skUploadLoader.visibility = View.GONE
            checkIfResumeUploaded()
        }.addOnSuccessListener {
            Log.w(TAG, "uploadFile:success")
            binding.skUploadLoader.visibility = View.GONE
            checkIfResumeUploaded()
        }
    }

    private fun uploadFileOnFBStorage() {
        binding.skUploadLoader.visibility = View.VISIBLE
        val email = AppInstance.userObj?.getEmail()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val file = Uri.fromFile(File(filePath))

        val riversRef = storageRef.child(
            "resume/" + email + "/${
                "resume_" + email + "_" + System.currentTimeMillis() + "." + File(filePath).extension
            }"
        )

        val uploadTask = riversRef.putFile(file)

        uploadTask.addOnFailureListener {
            binding.skUploadLoader.visibility = View.GONE
            checkIfResumeUploaded()
            Log.w(TAG, "uploadFile:failure" + it.message.toString()!!)
        }.addOnSuccessListener {
            binding.skUploadLoader.visibility = View.GONE
            checkIfResumeUploaded()
            Log.w(TAG, "uploadFile:success")
        }
    }


    private fun showToolTip(view: View, message: String) {
        try {
            Tooltip.Builder(view)
                .setText(message)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.tool_tip_gray))
                // .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                .setTextColor(ContextCompat.getColor(this, R.color.colorDarkGrayText))
                .setCornerRadius(dpToPxs(20).toFloat())
                .setPadding(dpToPxs(20))
                .setCancelable(true)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setNoAiUI(case: Int) {
        try {
            when (case) {
                0 -> { // no internet
                    binding.tvNoQuestions.text = getString(R.string.find_wifi)
                    binding.ivNoAIIcon.setImageResource(R.drawable.no_internet_icons)
                    binding.tvNoQuRetry.visibility = View.VISIBLE
                }
                1 -> { // given all answers
                    binding.tvNoQuestions.text = getString(R.string.good_job)
                    binding.ivNoAIIcon.setImageResource(R.drawable.ic_all_questions_done)
                    binding.tvNoQuRetry.visibility = View.GONE
                }
                2 -> { //no response
                    binding.tvNoQuestions.text = getString(R.string.advance_matching)
                    binding.ivNoAIIcon.setImageResource(R.drawable.ic_no_response)
                    binding.tvNoQuRetry.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setNoYoutubeUI(case: Int) {
        try {
            when (case) {
                0 -> { // no internet
                    binding.tvNoYoutubeLinks.text = getString(R.string.find_wifi)
                    binding.ivNoYoutubeIcon.setImageResource(R.drawable.no_internet_icons)
                }
                1 -> { //no response
                    binding.tvNoYoutubeLinks.text = getString(R.string.advance_matching_youtube)
                    binding.ivNoYoutubeIcon.setImageResource(R.drawable.ic_no_response)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *  communicate with socket
     */
    private fun socketCommunication() {
        try {
            //creating socket callback
            setOnSocketCallBackListener(this)
            //creating socket connection
            SocketCommunication.connectSocket(this@WaitingActivity, mSocketCallBackListeners)
            if (SocketCommunication.isSocketConnected()) {
                SocketCommunication.emitOnlineActivity()
                SocketCommunication.emitInScreenActivity(WAITING_SCREEN)
                SocketCommunication.emitBestMatches()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setOnSocketCallBackListener(mSocketCallBackListeners: SocketCallBackListeners) {
        this.mSocketCallBackListeners = mSocketCallBackListeners
    }

    override fun onMatchedResponse(data: SocketCallResponse) {
        Log.e("SWAPSOC", "onMatchedResponse = WaitingScreen")
    }

    override fun onAcceptRejectResponse(data: SocketCallResponse) {
        Log.e("SWAPSOC", "onAcceptRejectResponse = WaitingScreen")
    }

    override fun onUserInfoResponse(data: SocketCallResponse) {
        Log.e("SWAPSOC", "onUserInfoResponse = WaitingScreen")
    }

    override fun onSocketConnected() {
        Log.e("SWAPSOC", "onSocketConnected = WaitingScreen")
        SocketCommunication.emitOnlineActivity()
        SocketCommunication.emitInScreenActivity(WAITING_SCREEN)
        SocketCommunication.emitBestMatches()
        //checkCallDrop()
    }

    override fun onRefreshBestMatch(status: String) {
        if (status.equals("success"))
            SocketCommunication.emitBestMatches()
        else
            skBestMatchLoader.visibility = View.GONE
    }


    override fun onSocketDisconnected() {
        Log.e("SWAPSOC", "onSocketDisconnected = WaitingScreen")
    }

    override fun callReconnectIsUserOnline(status: Boolean) {
        Log.e("SWAPSOC", "callReconnect = WaitingScreen")
        runOnUiThread {
            if (status) {
                showReconnectWaitingDialog()
                callDropReconnectTimer()
            } else {
                if (reconnectDialog != null && reconnectDialog!!.isShowing) {
                    reconnectDialog!!.dismiss()
                }
                binding.dropCallButton.visibility = View.GONE
                binding.imageViewRightClick.visibility = View.GONE
                clearAnimation()
                //     clearReconnectTimer()
                callDropReconnectTimer()
                showDialogContactingUser(
                    "Contacting " + matcheUserName + "...",
                    getString(R.string.please_wait)
                )
            }
        }

    }

    override fun callDropWantToConnect(
        userId: String,
        friendId: String,
        firstName: String,
        friendFirstName: String,
        type: String
    ) {
        runOnUiThread {
            matcheUserName = firstName;
            if (matcheUserName?.contains(" ")!!)
                matcheUserName = matcheUserName.split(" ")[0]

            playRejoinSound()
            showCallReconnectDialog(userId, friendId, matcheUserName, type)
        }
    }

    private fun playRejoinSound() {
        stopRejoinSound()
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.mutualring)
            mediaPlayer?.isLooping = true
        }
        mediaPlayer?.start()
    }

    private fun stopRejoinSound() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying!!) {
            mediaPlayer?.stop()
            mediaPlayer = null
        }
    }


    override fun connectedToCall(
        status: Boolean, tokboxToken: String, time: Long, matchFound: Boolean, count: Int,
        fromsocketid: String, toSocketId: String, toUserSessionId: String, fromId: String,
        toId: String, fromUserSessionId: String, tokboxApi: String, rejoin: Boolean
    ) {
        runOnUiThread(Runnable {
            if (status && reconnectStatus.equals("accept")) {
                navigateToCall(
                    tokboxToken,
                    time,
                    matchFound,
                    count,
                    fromsocketid,
                    toSocketId,
                    toUserSessionId,
                    fromId,
                    toId,
                    fromUserSessionId,
                    tokboxApi,
                    rejoin
                )
            } else {
                binding.dropCallButton.visibility = View.GONE
                binding.imageViewRightClick.visibility = View.GONE
                waitingDialog?.dismiss()
                clearReconnectTimer()
                //  clearAnimation()
                // clearCallData(applicationContext)
            }
        })

    }

    override fun friendOnline(arrayListUsers: MutableList<OnlineOfflineResponse>) {
        setRejoinListAdapter(arrayListUsers)
    }


    override fun friendOffline(arrayListUsers: MutableList<OnlineOfflineResponse>) {
        setRejoinListAdapter(arrayListUsers)

    }

    override fun bestMatches(arrayListUsers: MutableList<OnlineOfflineResponse>) {
        setBestMatchAdapter(arrayListUsers)

    }

    override fun onNotifyEndCall(data: JSONObject) {

    }

    override fun onChatMessageReceive(data: ReceivedMessageResponse) {
        // binding.skBestMatchLoader.visibility = View.VISIBLE
        SocketCommunication.emitBestMatchRefresh()
    }

    override fun callAccepted(
        status: Boolean,
        tokboxToken: String,
        time: Long,
        matchFound: Boolean,
        count: Int,
        fromsocketid: String,
        toSocketId: String,
        toUserSessionId: String,
        fromId: String,
        toId: String,
        fromUserSessionId: String,
        tokboxApi: String,
        rejoin: Boolean
    ) {
        Log.e("SWAPSOC", "callAccepted = WaitingScreen" + status + "   " + reconnectStatus)
        runOnUiThread(Runnable {
            if (status && reconnectStatus.equals("accept")) {
                navigateToCall(
                    tokboxToken,
                    time,
                    matchFound,
                    count,
                    fromsocketid,
                    toSocketId,
                    toUserSessionId,
                    fromId,
                    toId,
                    fromUserSessionId,
                    tokboxApi,
                    rejoin
                )
            } else {
                //show Dialog
                if (matcheUserName.isEmpty())
                    matcheUserName = "User"
                if (dialogContacting != null && dialogContacting!!.isShowing)
                    dialogContacting?.dismiss()

                showDialogUserNotRespond(matcheUserName + " " + getString(R.string.is_currently_unavailable))
            }
        })

    }


    //timer for waiting call drop to be accepted
    private fun callDropReconnectTimer() {
        timerCallReconnect = object : CountDownTimer(waitingTimeDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                binding.dropCallButton.visibility = View.GONE
                binding.imageViewRightClick.visibility = View.GONE
                clearAnimation()
                waitingDialog?.dismiss()
                if (dialogContacting != null && dialogContacting!!.isShowing)
                    dialogContacting?.dismiss()
                //    showDialogUserNotRespond(matcheUserName + " " + getString(R.string.not_respond_to_call))
                showDialogUserNotRespond(matcheUserName + " " + getString(R.string.not_respond_to_call))
                //clearCallData(applicationContext)
            }
        }
        timerCallReconnect?.start()
    }

    private fun setRejoinListAdapter(arrayListUsers: MutableList<OnlineOfflineResponse>) {
        runOnUiThread {
            binding.skRejoinLoader.visibility = View.GONE
            if (rejoinListAdapter == null) {
                rejoinListAdapter = RejoinListAdapter(
                    arrayListUsers,
                    this@WaitingActivity
                )
                binding.rvRejoinDate.adapter = rejoinListAdapter
                rejoinListAdapter!!.OnRejoinClickListener(this@WaitingActivity)

            } else {
                rejoinListAdapter?.customNotify(arrayListUsers)
            }

            if (rejoinListAdapter?.itemCount == 0) {
                binding.llRejoinContainer.visibility = View.GONE
                binding.tvRejoinContainerHint.visibility = View.VISIBLE
                binding.rvRejoinDate.visibility = View.GONE
                binding.tvRejoinContainerHint.text = getString(R.string.hint_rejoin)
                //binding.tvRejoinListTitle.text = getString(R.string.previous_dates)
                isRejoinListEmpty = true
            } else {
                binding.llRejoinContainer.visibility = View.VISIBLE
                binding.tvRejoinContainerHint.visibility = View.GONE
                binding.rvRejoinDate.visibility = View.VISIBLE
                binding.tvRejoinContainerHint.text = getString(R.string.hint_rejoin)
               // binding.tvRejoinListTitle.text = getString(R.string.rejoin_date)
                isRejoinListEmpty = false
            }

            /*  Log.d("timeBetweenInterval", timeBetweenInterval("06:00:00", "23:00:00").toString() + "")
              if (!timeBetweenInterval("06:00:00", "23:00:00")) {
                  binding.tvRejoinListTitle.text = getString(R.string.previous_date)
                  binding.tvRejoinContainerHint.text = getString(R.string.rejoin_date_between_time)
                  binding.tvRejoinContainerHint.visibility = View.VISIBLE
                  binding.rvRejoinDate.visibility = View.GONE
                  isRejoinListEmpty = true
              }*/
        }
    }

    private fun setBestMatchAdapter(arrayListUsers: MutableList<OnlineOfflineResponse>) {
        runOnUiThread {
            binding.skBestMatchLoader.visibility = View.GONE
            if (bestMatchAdapter == null) {
                bestMatchAdapter = BestMatchAdapter(
                    arrayListUsers,
                    this@WaitingActivity
                )
                binding.rvBestMatch.adapter = bestMatchAdapter
                bestMatchAdapter!!.onBestMatchClickListener(this@WaitingActivity)

            } else {
                bestMatchAdapter?.customNotify(arrayListUsers)
            }

            if (bestMatchAdapter?.itemCount == 0) {
                binding.llBestMatchContainer.visibility = View.GONE
                binding.rvBestMatch.visibility = View.GONE
            } else {
                binding.llBestMatchContainer.visibility = View.VISIBLE
                binding.rvBestMatch.visibility = View.VISIBLE
            }
        }
    }

    fun NestedScrollView.focusOnView(toView: View) {
        Handler().post {
            this.smoothScrollTo(0, toView.bottom)
        }
    }

    fun NestedScrollView.isViewVisible(view: View): Boolean {
        val scrollBounds = Rect()
        this.getDrawingRect(scrollBounds)
        var top = 0f
        var temp = view
        while (temp !is NestedScrollView) {
            top += (temp).y
            temp = temp.parent as View
        }
        val bottom = top + view.height
        return scrollBounds.top < top && scrollBounds.bottom > bottom
    }

    private fun navigateToCall(
        tokboxToken: String, time: Long, matchFound: Boolean, count: Int,
        fromsocketid: String, toSocketId: String, toUserSessionId: String, fromId: String,
        toId: String, fromUserSessionId: String, tokboxApi: String, rejoin: Boolean
    ) {

        try {
            viewModel.setPauseCommunicator(true)
            Smartlook.stopRecording()
            //timer
            if (timer != null) {
                timer!!.cancel()
                timer = null
            }
            OpenTokConfig.API_KEY = tokboxApi
            OpenTokConfig.MATCHED_USER_NAME = matcheUserName
            OpenTokConfig.SESSION_ID = fromUserSessionId
            OpenTokConfig.TOKEN = tokboxToken

            viewModel.setPauseCommunicator(true)
            val mIntent = Intent(this, VideoCallActivity::class.java)
            mIntent.putExtra(FROMID, fromId)
            mIntent.putExtra(TOID, toId)
            mIntent.putExtra(COUNT, count)
            mIntent.putExtra(CALLDURATION, time)
            mIntent.putExtra(FROMUSER_SOCKET_ID, fromsocketid)
            mIntent.putExtra(TOUSER_SOCKET_ID, toSocketId)
            mIntent.putExtra(REJOIN, rejoin)
            startActivity(mIntent)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // fo
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //show call reconnect dialog
    private fun showCallReconnectDialog(
        userId: String,
        friendId: String,
        firstName: String,
        type: String
    ) {
        try {
            reconnectDialog = Dialog(this, R.style.myDialog)
            reconnectDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                reconnectDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                reconnectDialog!!.window!!.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            reconnectDialog!!.setCancelable(false)
            reconnectDialog!!.setContentView(R.layout.dialog_reconnect_call)

            var imageView = reconnectDialog!!.findViewById<ImageView>(R.id.icon)

            var matchedUserName = "User"

            if (!firstName.isEmpty()!!)
                matchedUserName = firstName

//            if (type.equals("rejoin")) {
//                reconnectDialog!!.textTitle.text = matchedUserName + " Is trying to Reconnect with you!"
//                reconnectDialog!!.tvMessage.text = "Would you like to Accept and go out again with " + matchedUserName + "  Now?"
//
//            } else {
//                reconnectDialog!!.textTitle.text = matchedUserName + " would like to go out again with you!"
//                reconnectDialog!!.tvMessage.text = "Would you like to Accept and reconnect with " + matchedUserName + " Now?"
//            }

            reconnectDialog!!.textTitle.text =
                matchedUserName + " would like to have a Video Date Now."
            reconnectDialog!!.tvMessage.text = "Would you like to Accept and have a Video Date Now?"

            Glide.with(this).load(R.drawable.video_call).into(imageView);

            reconnectDialog!!.acceptCall.setOnClickListener(View.OnClickListener {
                reconnectStatus = "accept"
                SocketCommunication.emitRejoinResponse(
                    friendId!!,
                    userId,
                    firstName,
                    reconnectStatus
                )
                reconnectDialog!!.dismiss()
                stopRejoinSound()
            })
            reconnectDialog!!.currentlyUnavail.setOnClickListener(View.OnClickListener {
                reconnectStatus = "later"
                SocketCommunication.emitRejoinResponse(
                    friendId!!,
                    userId,
                    firstName,
                    reconnectStatus
                )
                reconnectDialog!!.dismiss()
                stopRejoinSound()
            })
            reconnectDialog!!.closeCallDialog.setOnClickListener(View.OnClickListener {
                //clearCallData(applicationContext)
                reconnectStatus = "later"
                SocketCommunication.emitRejoinResponse(
                    friendId!!,
                    userId,
                    firstName,
                    reconnectStatus
                )
                reconnectDialog!!.dismiss()
                stopRejoinSound()
            })
            reconnectDialog!!.permanentRejectBtn.setOnClickListener(View.OnClickListener {
                //clearCallData(applicationContext)
                reconnectStatus = "reject"
                reconnectDialog!!.dismiss()
                dialogRemoveUserFromRejoin(friendId, type, matchedUserName, false)
                stopRejoinSound()
            })

            reconnectDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //show Add Phone Number Dialog
    private fun showAddPhoneDialog() {
        try {
            addPhoneDialog = Dialog(this, R.style.myDialog)
            addPhoneDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                addPhoneDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                addPhoneDialog.window!!.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            addPhoneDialog.setCancelable(false)
            addPhoneDialog.setContentView(R.layout.dialog_add_phone_number)

            addPhoneDialog.textViewAdd.setOnClickListener(View.OnClickListener {
                val phone = addPhoneDialog.etPhone.text.toString()
                if (phone.isEmpty())
                    addPhoneDialog.etPhone.error = getString(R.string.please_enter_phone)
                else {
                    addPhoneDialog.skDialogLoader.visibility = View.VISIBLE
                    callAddPhoneNumberApi(addPhoneDialog.ccPicker.selectedCountryCodeWithPlus + phone)
                }
            })

            addPhoneDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun callAddPhoneNumberApi(phoneNumber: String) {
        if (isNetworkAvailable(this)) {
            //show Loader
            var p = RequestParams();
            p.add("userid", AppInstance.userObj!!.getId()!!);
            p.add("phone_no", phoneNumber);
            var rest = RestClient(this, RestMethod.POST, p);
            rest.setToken(AppInstance.userObj!!.getToken()!!);
            rest.execute(WebConstants.UPDATE_PHONE_NO);
        } else {
            Log.d(TAG, "NO Internet")
        }
    }


    //Interested in Meetings Dialog
    private fun showInterestInUpComingCall() {
        try {
            val meetingsAlertDialog = Dialog(this, R.style.myDialog)
            meetingsAlertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                meetingsAlertDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                meetingsAlertDialog?.window!!.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            meetingsAlertDialog.setCancelable(false)
            meetingsAlertDialog.setContentView(R.layout.dialog_alert_notification)

            meetingsAlertDialog.tvAlertMessage.text = intent.getStringExtra(ALERT)
            val timeSlot = intent.getStringExtra(TIMESLOT)

            meetingsAlertDialog.textViewAlertYes?.setOnClickListener {
                SocketCommunication.emitAlertNotificationResponse(
                    AppInstance.userObj!!.getId()!!!!,
                    timeSlot!!,
                    YES
                )
                meetingsAlertDialog.dismiss()
            }

            meetingsAlertDialog.closeDialog?.setOnClickListener {
                SocketCommunication.emitAlertNotificationResponse(
                    AppInstance.userObj!!.getId()!!!!,
                    timeSlot!!,
                    NO
                )
                meetingsAlertDialog.dismiss()
            }

            meetingsAlertDialog.textViewAlertNo?.setOnClickListener {
                SocketCommunication.emitAlertNotificationResponse(
                    AppInstance.userObj!!.getId()!!!!,
                    timeSlot!!,
                    NO
                )
                meetingsAlertDialog.dismiss()
            }

            meetingsAlertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //show Waiting Dialog
    private fun showReconnectWaitingDialog() {
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

            waitingDialog?.textViewMessage?.text = getString(R.string.date_reconnect_momentarily)

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

    private fun dialogRemoveUserFromRejoin(
        toId: String,
        rejoinType: String,
        matchingUserName: String,
        userClicked: Boolean
    ) {
        try {
            var matchedUserName = matchingUserName
            if (matchingUserName.contains(" "))
                matchedUserName = matchingUserName.split(" ")[0]

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
            val textViewTitle = dialog.findViewById<View>(R.id.textViewtitle) as TextView
            val cancel_icon_click = dialog.findViewById<View>(R.id.cancel_icon_click) as ImageView
            val editTextMessage = dialog.findViewById<View>(R.id.editTextmessage) as EditText
            val textViewPositiveBtn =
                dialog.findViewById<View>(R.id.textViewPositiveBtn) as TextView

            if (rejoinType.equals("best_match")) {
                textViewTitle.text =
                    "Clicking this will PERMANENTLY REMOVE " + matchedUserName + " from " +
                            "your " + tvBestMatchListTitle.text.toString() + " list as a match, You will NOT be able to go out again with " +
                            "" + matchedUserName + " on video on another day/time...Are you sure you want to proceed?";
            } else {
                textViewTitle.text =
                    "Clicking this will PERMANENTLY REMOVE " + matchedUserName + " from " +
                            "your " + tvRejoinListTitle.text.toString() + " list as a match, You will NOT be able to go out again with " +
                            "" + matchedUserName + " on video on another day/time...Are you sure you want to proceed?";
            }
            val fromId = AppInstance.userObj?.getId()

            textViewPositiveBtn.setOnClickListener {
                val reasonRemove = editTextMessage.text.toString()
                if (reasonRemove.isEmpty()) {
                    showDialogNoInternet(
                        this,
                        getString(R.string.please_enter_reason),
                        "",
                        R.drawable.ic_alert_icon
                    )
                } else {
                    if (rejoinType.equals("best_match")) {
                        binding.skBestMatchLoader.visibility = View.VISIBLE
                        SocketCommunication.emitRemoveUserFromBestMatch(
                            fromId!!,
                            toId,
                            reasonRemove
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            SocketCommunication.emitBestMatches()
                        }, 2000)

                    } else {
                        SocketCommunication.emitRemoveUserFromRejoin(fromId!!, toId, reasonRemove)
                        Handler(Looper.getMainLooper()).postDelayed({
                            SocketCommunication.emitInScreenActivity(WAITING_SCREEN)
                        }, 2000)
                    }
                    dialog.dismiss()
                    hideSoftKeyboard(this, editTextMessage)
                }
            }
            cancel_icon_click.setOnClickListener {
                dialog.dismiss()
                hideSoftKeyboard(this, editTextMessage)
                if (!userClicked && rejoinType != "best_match")
                    SocketCommunication.emitRejoinResponse(
                        toId!!,
                        fromId!!,
                        matchedUserName,
                        reconnectStatus
                    )
            }

            //dialog show
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showDialogUserNotRespond(message: String) {
        try {
            SwapdroidAlertDialog.Builder(this)
                .setTitle(getString(R.string.ooops))
                .setMessage(message)
                .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                .setNegativeBtnText(getString(R.string.no))
                .isNegativeVisible(false)
                .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                .setPositiveBtnText(getString(R.string.ok))
                .isPositiveVisible(true)
                .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                .setAnimation(SwapdroidAnimation.POP)
                .isCancellable(false)
                .showCancelIcon(false)
                .setIcon(
                    R.drawable.ic_error_icon,
                    SwapdroidIcon.Visible
                )  //ic_star_border_black_24dp
                .OnPositiveClicked {
                    waitingDialog?.dismiss()
                    binding.dropCallButton.visibility = View.GONE
                    binding.imageViewRightClick.visibility = View.GONE
                    clearAnimation()
                    clearReconnectTimer()
                }
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showDialogContactingUser(title: String, message: String) {
        try {
            dialogContacting = Dialog(this, R.style.myDialog)
            dialogContacting!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                dialogContacting!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                dialogContacting!!.window!!.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            dialogContacting!!.setCancelable(false)
            dialogContacting!!.setContentView(R.layout.dialog_contacting_rejoin)

            //getting resources
            val textViewTitle =
                dialogContacting!!.findViewById<View>(R.id.titleContacting) as TextView
            val cancel_icon_click =
                dialogContacting!!.findViewById<View>(R.id.closeContacting) as ImageView
            val imageView = dialogContacting!!.findViewById<View>(R.id.icon) as ImageView

            Glide.with(this).load(R.drawable.video_call).into(imageView)

            textViewTitle.setText(title)

            cancel_icon_click.setOnClickListener {
                clearReconnectTimer()
                dialogContacting!!.dismiss()
            }

            //dialog show
            dialogContacting!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setAllLayouts() {
        //train the ai
        setTrainingAiLayout()
        //setup youtube
        setYoutubeLayout()
        //set rejoin
        setRejoinLayout()

        getScreenDimensions()

        initYoutubePlayerDialog()
        bottomSheetDropCallView()
        bottomSheetYoutubeView()
        changeNavBack()
        setTextSizes()

    }

    private fun callMatchSubscriptionStatus() {
        //call matching webservice
        callMatchWebService()

        //check subscription status
        //commenting subscription code
        //callAppSubscriptionStatus()
    }

    //Permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
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
            val perms = arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
            if (EasyPermissions.hasPermissions(this, *perms)) {
                Log.e(LOG_TAG, "hasPermissions:")
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_video_app),
                    RC_VIDEO_APP_PERM,
                    *perms
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @AfterPermissionGranted(RC_STORAGE_APP_PERM)
    private fun requestPermissionsStorage() {
        try {
            val perms = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (EasyPermissions.hasPermissions(this, *perms)) {
                filePickerDialog()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_storage_app), RC_STORAGE_APP_PERM, *perms
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun filePickerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose from")

// add a list
        val options = arrayOf("Camera", "Photo", "Files", "Create Resume")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    takePicture()
                }
                1 -> {
                    startActivityForResult(getFileChooserIntentForImage("image/*"), RC_PDF_IMAGE)
                }
                2 -> {
                    startActivityForResult(getFileChooserIntentForDocAndPdf(), RC_PDF_IMAGE)
                }
                3 -> {
                    startActivity(Intent(this, CreateResumeActivity::class.java))
                    overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )  // for open
                }
            }
        }

// create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun getFileChooserIntentForImage(mimeType: String): Intent {
        val mimeTypes = arrayOf(mimeType)
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType(mimeType)
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        return intent
    }

    private fun getFileChooserIntentForDocAndPdf(): Intent {
        val mimeTypes = arrayOf("application/pdf", "application/msword")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("application/pdf|application/msword")
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        return intent
    }


    private fun takePicture() {

        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        val uri: Uri = FileProvider.getUriForFile(
            this,
            "shiddush.view.com.mmvsd.fileprovider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }

    @Throws(IOException::class)
    private fun createFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            imagePath = absolutePath
        }
    }

    //function for timer
    private fun timerView(timeDuration: Long) {//currentDate: String, currentTime: String, dayofweekName: String
        try {
            try {
                if (timer != null) {
                    timer!!.cancel()
                    timer = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //timeDuration = getTimerTime(currentDate, currentTime, dayofweekName)
            if (timeDuration != 0L) {
                timer = object : CountDownTimer(timeDuration, 1) {
                    override fun onFinish() {
                        binding.tvHour.text = "00"
                        binding.tvMin.text = "00"
                        binding.tvSec.text = "00"

                        if (MyApplication.isAppInBackground)
                            return

                        if (!hadCall) {
                            // navigate for call
                            navigateForVideoCall()
                        } else {
                            callTimerWebService()
                        }

                        Log.d("onFinish", "onFinishCalled")
                    }

                    override fun onTick(durationSeconds: Long) {
                        try {
                            val hms: String = String.format(
                                "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(durationSeconds),
                                TimeUnit.MILLISECONDS.toMinutes(durationSeconds) - TimeUnit.HOURS.toMinutes(
                                    TimeUnit.MILLISECONDS.toHours(durationSeconds)
                                ),
                                TimeUnit.MILLISECONDS.toSeconds(durationSeconds) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(durationSeconds)
                                )
                            )
                            binding.tvHour.text = hms.split(":")[0]
                            binding.tvMin.text = hms.split(":")[1]
                            binding.tvSec.text = hms.split(":")[2]

                            /*     val formatter = SimpleDateFormat("HH:mm:ss")
                                 val date = Date(durationSeconds)
                                 val returnFormat: String = formatter.format(date)
                                 Log.d("returnTime_",returnFormat)*/
                        } catch (e: Exception) {
                            e.printStackTrace()
                            logFirebaseEvents(
                                "timerGlitch",
                                "Error Message " + e.printStackTrace()
                            );
                        }
                    }
                }
                timer!!.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //Training the ai
    private fun setTrainingAiLayout() {

        try {
            //set card properties
            cardStackView = binding.cardStackView
            cardStackLayoutManager = CardStackLayoutManager(this@WaitingActivity, this)
            cardStackLayoutManager.setStackFrom(StackFrom.Bottom)
            cardStackLayoutManager.setDirections(Direction.HORIZONTAL)
            cardStackLayoutManager.setVisibleCount(2)
            cardStackLayoutManager.setTranslationInterval(12f)
            cardStackLayoutManager.setScaleInterval(0.85f)
            cardStackLayoutManager.setCanScrollHorizontal(false)
            cardStackLayoutManager.setCanScrollVertical(false)

            //set card fragment_intro_notes
            cardStackView.layoutManager = cardStackLayoutManager
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set delay while clicking on options
    private fun setDelay() {
        if (trainingTheAIAdapter != null) {
            trainingTheAIAdapter.setDelay(true)
            Handler().postDelayed({
                trainingTheAIAdapter.setDelay(false)
            }, 1500)
        }
    }

    override fun oncardOptionClick(questionId: String, answerId: String, isMain: Boolean) {
        setDelay()
        postTrainTheAIAnswer(questionId, answerId, isMain)
        //swipeYourTopCard()

    }

    override fun oncardSkipClick(questionId: String, option: String, isMain: Boolean) {
        setDelay()
        postTrainTheAIAnswer(questionId, "", isMain) //Skip
        //swipeYourTopCard()
    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {
        try {
            cardStackLayoutManager.setCanScrollHorizontal(false)
            if (cardItemSize != 0) {
                cardItemSize -= 1
                if (cardItemSize == 0) {
                    Log.d("postTrainTheAIAnswer", "0")
                    //call api
                    callQuestionsApiTree()
                }
            } else {
                Log.d("postTrainTheAIAnswer", "1")
                //call api
                callQuestionsApiTree()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("postTrainTheAIAnswer", "Exception")
            //call api
            callQuestionsApiTree()
        }
    }

    override fun onCardCanceled() {
        Log.d("postTrainTheAIAnswer", "onCardCanceled")

    }

    override fun onCardAppeared(view: View?, position: Int) {
        Log.d("postTrainTheAIAnswer", "onCardAppeared")

    }

    override fun onCardRewound() {
        Log.d("postTrainTheAIAnswer", "onCardRewound")

    }

    private fun swipeYourTopCard() {
        //on click option
        try {
            cardStackLayoutManager.setCanScrollHorizontal(true)
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Slow.duration) //Normal.duration
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
            cardStackLayoutManager.setCanScrollHorizontal(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //get quiz questions api call
    private fun callQuestionsApiTree() {
        try {
            binding.cardStackView.visibility = View.INVISIBLE
            binding.llNoQuestions.visibility = View.GONE
            binding.skAILoader.visibility = View.VISIBLE
            trainingTheAIData = ArrayList<Data>()
            if (isNetworkAvailable(this)) {

                val p = RequestParams();
                p.add("user_id", AppInstance.userObj!!.getId()!!);
                val rest = RestClient(this, RestMethod.GET, p);
                rest.setToken(AppInstance.userObj!!.getToken()!!);
                rest.execute(WebConstants.GET_QUESTIONS);

            } else {
                noTrainTheAiQuestions()
                setNoAiUI(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            noTrainTheAiQuestions()
            setNoAiUI(0)
            logFirebaseEvents("getTrainTheAIList", "Error Message: " + e.printStackTrace())

        }
    }

    private fun noTrainTheAiQuestions() {
        binding.skAILoader.visibility = View.GONE
        binding.llTrainingAiContainer.visibility = View.GONE
        binding.llNoQuestions.visibility = View.VISIBLE
        binding.cardStackView.visibility = View.INVISIBLE
    }

    //get quiz questions api call
    private fun postTrainTheAIAnswer(questionId: String, answerId: String, isMain: Boolean) {
        Log.d("postTrainTheAIAnswer", "postTrainTheAIAnswer");
        try {
            if (isNetworkAvailable(this)) {
                binding.skAILoader.visibility = View.VISIBLE
                var p = RequestParams();
                p.add("user_id", AppInstance.userObj!!.getId()!!);
                p.add("answer_id", answerId);
                p.add("question_id", questionId);
                p.add("is_main", isMain);
                var rest = RestClient(this, RestMethod.POST, p);
                rest.setToken(AppInstance.userObj!!.getToken()!!);
                rest.execute(WebConstants.SUBMIT_QUESTION_ANSWER);

            } else {
                showDialogNoInternet(
                    this,
                    getString(R.string.ooops),
                    getString(R.string.check_internet),
                    R.drawable.ic_nointernet_icon
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("postTrainTheAIAnswer", "Error Message: " + e.printStackTrace())

        }
    }


//--Training the ai

    //Youtube
    private fun setYoutubeLayout() {
        val mLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.rvYoutubeLinks.layoutManager = mLayoutManager
    }

    //Rejoin
    private fun setRejoinLayout() {
        val mLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.rvRejoinDate.layoutManager = mLayoutManager
    }

    private fun setYoutube(videoId: String) {
        if (youTubeVideoPlayer != null)
            return
        currentVideoId = videoId
        getLifecycle().addObserver(youtubePlayerView);
        youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                isReady = true
                youTubeVideoPlayer = youTubePlayer
                youTubeVideoPlayer?.loadVideo(videoId, 0F)
                youTubeVideoPlayer?.pause();
                checkIfFromReview()
            }

            override fun onError(
                youTubePlayer: YouTubePlayer,
                error: PlayerConstants.PlayerError
            ) {
                super.onError(youTubePlayer, error)
                isReady = false
                showToast(
                    this@WaitingActivity, getString(R.string.youtube_error),
                    Toast.LENGTH_SHORT
                )
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)
                try {
                    if (state.name.equals(PLAYING)) {
                        currentVideoState = true
                        pauseRadio()
                    } else if (state.name.equals(PAUSED)) {
                        currentVideoState = false
                        playRadio()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                currentVideoDuration = second
            }

        })

        youtubePlayerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                try {
                    if (!isFullScreenCall) {
                        isFullScreenCall = true
                        if (youTubeVideoPlayer != null) {
                            youTubeVideoPlayer?.pause()
                        }
                        playFullScreenVideo()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onYouTubePlayerExitFullScreen() {

            }

        })
    }

    private fun checkIfFromReview() {
        if (isRejoinListEmpty && intent.hasExtra(VIA) && intent.getStringExtra(VIA)
                .equals(REVIEW_SUCCESS)
            && youtubeListData.size > 0
        ) {
            //play youtube video
            //  Handler(Looper.getMainLooper()).postDelayed({
            // if (youTubeVideoPlayer != null)
            onPlayClick(youtubeListData!!.get(0).getVideoId()!!)
            //}, 5000)
        }
    }

    private fun animateRejoin() {
        binding.viewRejoinTop.visibility = View.VISIBLE
        binding.llRejoinFocus.visibility = View.VISIBLE
        binding.viewRejoinBottom.visibility = View.VISIBLE
        binding.viewRejoinBottomTwo.visibility = View.VISIBLE
        swingAnimation = AnimationUtils.loadAnimation(this, R.anim.swinging)
        binding.imageViewRejoinClick.startAnimation(swingAnimation)

        //    if (binding.llUploadResumeContainer.visibility == View.VISIBLE)
        //      nsvParent.post(Runnable { findDistanceToScroll(binding.llTrainingAiContainer, R.id.llRejoinContainer) })
        // else
        nsvParent.post(Runnable { findDistanceToScroll(binding.viewTop, R.id.llRejoinContainer) })


        val timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                binding.viewRejoinTop.visibility = View.GONE
                binding.llRejoinFocus.visibility = View.GONE
                binding.viewRejoinBottom.visibility = View.GONE
                binding.viewRejoinBottomTwo.visibility = View.GONE
                binding.imageViewRejoinClick.clearAnimation()
            }
        }
        timer.start()

    }

    private fun animateTimerClick() {
        binding.viewLeft.visibility = View.VISIBLE
        binding.viewRight.visibility = View.VISIBLE
        binding.llTimerFocus.visibility = View.VISIBLE
        val rotate50 = RotateAnimation(
            -50f, 50f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate50.setDuration(900)

        val rotate_50 = RotateAnimation(
            50f, -50f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate_50.setDuration(900)

        binding.imageViewTimerClick.startAnimation(rotate50)

        rotate50.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.imageViewTimerClick.startAnimation(rotate_50)
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        rotate_50.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.imageViewTimerClick.startAnimation(rotate50)
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        val timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                binding.viewLeft.visibility = View.GONE
                binding.viewRight.visibility = View.GONE
                binding.llTimerFocus.visibility = View.GONE
                binding.imageViewTimerClick.clearAnimation()
            }
        }
        timer.start()

    }

    private fun animateHadDated() {
        binding.viewHasDate.visibility = View.VISIBLE
        binding.viewHasDateRight.visibility = View.VISIBLE
        binding.llNextDateFocus.visibility = View.VISIBLE
        val rotate50 = RotateAnimation(
            -50f, 50f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate50.setDuration(900)

        val rotate_50 = RotateAnimation(
            50f, -50f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate_50.setDuration(900)

        binding.imageViewNextDate.startAnimation(rotate50)

        rotate50.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.imageViewNextDate.startAnimation(rotate_50)
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        rotate_50.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.imageViewNextDate.startAnimation(rotate50)
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        val timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                binding.llNextDateFocus.visibility = View.GONE
                binding.viewHasDate.visibility = View.GONE
                binding.viewHasDateRight.visibility = View.GONE
                binding.imageViewNextDate.clearAnimation()
            }
        }
        timer.start()

    }

    private fun animateDropCallClick() {
        swingAnimation = AnimationUtils.loadAnimation(this, R.anim.swinging)
        binding.imageViewRightClick.startAnimation(swingAnimation)
    }

    private fun animateButtonReconnect() {
        shrinkGrowAnimation = ObjectAnimator.ofPropertyValuesHolder(
            binding.dropCallButton,
            PropertyValuesHolder.ofFloat("scaleX", 0.8f),
            PropertyValuesHolder.ofFloat("scaleY", 0.8f)
        )
        shrinkGrowAnimation.duration = 2000
        shrinkGrowAnimation.repeatMode = ValueAnimator.REVERSE
        shrinkGrowAnimation.repeatCount = ValueAnimator.INFINITE
        shrinkGrowAnimation.start()
    }

    private fun clearAnimation() {
        swingAnimation?.reset()
        swingAnimation?.cancel()
        binding.imageViewRightClick.clearAnimation()
    }

    //get youtube api call
    private fun callYoutubeList() {
        try {
            binding.llYoutubeVideoContainer.visibility = View.GONE
            binding.llNoYoutubeLinks.visibility = View.INVISIBLE
            binding.skLoader.visibility = View.VISIBLE
            if (isNetworkAvailable(this)) {

                var p = RequestParams();
                p.add("user_id", AppInstance.userObj!!.getId()!!);
                var rest = RestClient(this, RestMethod.GET, p);
                rest.setToken(AppInstance.userObj!!.getToken()!!);
                rest.execute(WebConstants.GET_YOUTUBE_LINKS_WS);

            } else {
                noYoutubeVideos()
                setNoYoutubeUI(0)
                showDialogNoInternet(
                    this,
                    getString(R.string.ooops),
                    getString(R.string.check_internet),
                    R.drawable.ic_nointernet_icon
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            noYoutubeVideos()
            setNoYoutubeUI(1)
            logFirebaseEvents("getYoutubeList", "Error Message: " + e.printStackTrace())

        }
    }

    private fun noYoutubeVideos() {
        binding.skLoader.visibility = View.GONE
        binding.llYouTubeContainer.visibility = View.GONE
        binding.llNoYoutubeLinks.visibility = View.VISIBLE
        binding.llYoutubeVideoContainer.visibility = View.GONE
    }

    //On play click
    override fun onPlayClick(videoID: String) {
        if (isReady) {
            currentVideoId = videoID
            try {
                mBottomSheetYoutubeBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                if (youTubeVideoPlayer != null) {
                    youTubeVideoPlayer?.loadVideo(videoID, 0F)
                    youTubeVideoPlayer?.play()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
//--Youtube

    //Timer Webservice
    private fun callTimerWebService() {
        try {
            if (isNetworkAvailable(this)) {
                val p = RequestParams();
                p.add("userid", AppInstance.userObj!!.getId()!!);
                val rest = RestClient(this, RestMethod.GET, p);
                rest.setToken(AppInstance.userObj!!.getToken()!!);
                rest.execute(WebConstants.GET_TIMER_WS);
                Log.d("token==", AppInstance.userObj!!.getToken()!! + " ")

            } else {
                //no internet
                showDialogNoInternet(
                    this@WaitingActivity,
                    getString(R.string.ooops),
                    getString(R.string.check_internet),
                    R.drawable.ic_nointernet_icon
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("callTimerWebService", "Error Message: " + e.printStackTrace())

        }
    }
//--Timer Webservice

    //Matching Webservice
    private fun callMatchWebService() {
        try {
            if (isNetworkAvailable(this)) {

                val p = RequestParams()
                p.add("userid", AppInstance.userObj!!.getId()!!)
                p.add("device_token", getPrefString(this, PreferenceConnector.DEVICE_TOKEN));
                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(AppInstance.userObj!!.getToken()!!)
                rest.execute(WebConstants.MATCH_WHILE_REGISTRATION_WS)

            } else {
                //no internet
                logFirebaseEvents("callMatchWebService", "No Internet Connection")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("callMatchWebService", "Error Message: " + e.printStackTrace())

        }
    }
//--Matching Webservice

    //Logout web service
    private fun logoutWSCall() {
        try {
            if (isNetworkAvailable(this)) {
                val token = AppInstance.userObj!!.getToken()!!

                val p = RequestParams();
                p.add("userid", AppInstance.userObj!!.getId()!!)
                p.add("device_token", getPrefString(this, PreferenceConnector.DEVICE_TOKEN))

                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.POST_LOGOUT_WS);
            } else {
                //no internet
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("logoutWSCall", "Error Message: " + e.printStackTrace())

        }
    }

    //provide screen dimensions
    private fun getScreenDimensions() {
        playerHeight = dpToPxs(200)
        cardHeight = dpToPxs(270)
        try {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels
            playerHeight = height / 3
            //  binding.llYoutubeLayout.layoutParams.height = playerHeight
            //  binding.cardStackView.layoutParams.height = cardHeight
            //  binding.llNoQuestions.layoutParams.height = cardHeight
            //  binding.llNoYoutubeLinks.layoutParams.height = cardHeight
        } catch (e: Exception) {
            e.printStackTrace()
            // binding.llYoutubeLayout.layoutParams.height = playerHeight
            //binding.cardStackView.layoutParams.height = cardHeight
            // binding.llNoQuestions.layoutParams.height = cardHeight
            //binding.llNoYoutubeLinks.layoutParams.height = cardHeight
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            isFullScreenCall = false
            try {
                youtubePlayerView.exitFullScreen()
            } catch (es: Exception) {
                es.printStackTrace()
            }
            if (requestCode == ADD_VIDEO_TASK_REQUEST) {
                val startSeconds = data?.getFloatExtra(VIDEO_DURATION, currentVideoDuration)
                val currentstate = data?.getBooleanExtra(VIDEO_STATE, currentVideoState)
                if (startSeconds != null) {
                    currentVideoDuration = startSeconds
                    currentVideoState = currentstate!!
                    try {
                        if (youTubeVideoPlayer != null) {
                            youTubeVideoPlayer?.seekTo(currentVideoDuration)
                            if (currentVideoState) {
                                youTubeVideoPlayer?.play()
                            } else {
                                youTubeVideoPlayer?.pause()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if (requestCode == IN_APP_UPDATE) {
                if (resultCode != RESULT_OK) {
                    showUpdateAppDialog(
                        this,
                        resources.getString(R.string.app_name),
                        """${resources.getString(R.string.update_app)} $availableVersion""",
                        false
                    )
                } else {
                    showToast(this, getString(R.string.downloading), Toast.LENGTH_SHORT)
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (resultCode != Activity.RESULT_OK)
                    imagePath = ""
                else {
                    Picasso.get()
                        .load(File(imagePath))
                        .placeholder(R.drawable.ic_upload_white)
                        .error(R.drawable.ic_upload_white)
                        .into(binding.imageViewPic)

                    uploadImageOnFBStorage()
                }
            } else if (requestCode == RC_PDF_IMAGE) {
                if (data?.data != null)
                    pickiT.getPath(data.getData(), Build.VERSION.SDK_INT)
            }else if (requestCode == CHAT_SCREEN_CODE) {
                setOnSocketCallBackListener(this)
                SocketCommunication.connectSocket(this@WaitingActivity, mSocketCallBackListeners)
                Handler(Looper.getMainLooper()).postDelayed({
                    SocketCommunication.emitBestMatchRefresh()
                }, 2000)

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onResume() {
        super.onResume()

        viewModel.setPauseCommunicator(false)
        //play radio
        setIsInWaitingScreen(true)

        try {
            //set app is open
            savePrefBoolean(this, PreferenceConnector.IS_APP_OPEN, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        //to set app instance
        try {
            AppInstance.isCallCut = false
            AppInstance.userObj = getUserObject(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            @Suppress("DEPRECATION")
            screenLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, LOG_TAG
            )
            screenLock!!.acquire()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //clear opentok data
        OpenTokConfig.API_KEY = ""
        OpenTokConfig.SESSION_ID = ""
        OpenTokConfig.TOKEN = ""

        appUpdateManager
            ?.appUpdateInfo
            ?.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    showUpdateAppDialog(
                        this,
                        resources.getString(R.string.app_name),
                        "An update has just been downloaded.",
                        true
                    )
                }

            }

    }

    override fun onPause() {
        super.onPause()
        //pause radio
        setIsInWaitingScreen(false)
        pauseRadio()
        beforeOnPause = true
        if (youTubeVideoPlayer != null) {
            youTubeVideoPlayer?.pause()
        }
        if (SocketCommunication.isSocketConnected()) {
            SocketCommunication.emitInScreenActivity(LOCK_SCREEN)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            youtubePlayerView.release()
            screenLock!!.release()
            try {
                //timer
                if (timer != null) {
                    timer!!.cancel()
                    timer = null
                }
                if (timerCallReconnect != null) {
                    timerCallReconnect!!.cancel()
                    timerCallReconnect = null
                }
                //  turnScreenOffAndKeyguardOn()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // on back press
    override fun onBackPressed() {
        showCommonDialog(false, getString(R.string.sure_to_exit)) // false for finish app
    }

    // use to finish application or logout user
    private fun showCommonDialog(forLogout: Boolean, title: String) {
        try {
            SwapdroidAlertDialog.Builder(this)
                .setTitle(title)
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
                .setIcon(
                    R.drawable.ic_exit_icon,
                    SwapdroidIcon.Visible
                )  //ic_star_border_black_24dp
                .OnNegativeClicked {
                    //do nothing
                }
                .OnPositiveClicked {
                    if (forLogout) {
                        //User Logout
                        logoutWSCall()
                        openCommonLogin()
                    } else {
                        //finish application
                        finishApplication()
                    }
                }
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // play youtube video in full screen
    private fun playFullScreenVideo() {
        Log.e("SWAPVID", "playFullScreenVideo")
        //pause radio
        pauseRadio()
        pauseRadio()
        viewModel.setPauseCommunicator(true)
        val intent = Intent(this@WaitingActivity, YoutubeVideoPlayerActivity::class.java)
        intent.putExtra(VIDEO_ID, currentVideoId)
        intent.putExtra(VIDEO_DURATION, currentVideoDuration)
        intent.putExtra(VIDEO_STATE, currentVideoState)
        startActivityForResult(intent, ADD_VIDEO_TASK_REQUEST)
    }

    //navigation for video call
    private fun navigateForVideoCall() {
        Smartlook.stopRecording()
        if (!isTimerEnd) {
            isTimerEnd = true
            try {
                //timer
                if (timer != null) {
                    timer!!.cancel()
                    timer = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            SocketCommunication.emitMobileLog(
                "WAITING_SCREEN_TIMMER",
                "Timer reach to 0",
                "info",
                "Timer reach to zero on waiting screen and call matching method call"
            );

            Log.e("SWAPVID", "navigateForVideoCall")
            Handler().postDelayed({
                if (!isClickable) {
                    isClickable = true
                    viewModel.setPauseCommunicator(true)
                    val intent = Intent(this@WaitingActivity, IntroVideoActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    this@WaitingActivity.finish()
                    overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )  // for open
                }
            }, 500)
        }
    }

    // finish application
    private fun finishApplication() {
        if (!isTimerEnd) {
            isTimerEnd = true
            //disconnect socket
            try {
                SocketCommunication.disconnectSocket(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                //set app is open
                savePrefBoolean(this, PreferenceConnector.IS_APP_OPEN, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                //timer
                if (timer != null) {
                    timer!!.cancel()
                    timer = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.e("SWAPVID", "finish app")
            Handler().postDelayed({
                this@WaitingActivity.finish()
            }, 500)
        }
    }

    //navigate to common login
    private fun openCommonLogin() {
        //clearCallData(applicationContext)
        if (!isTimerEnd) {
            isTimerEnd = true
            //disconnect socket
            try {
                SocketCommunication.emitInScreenActivity(LOGIN_SCREEN)
                SocketCommunication.disconnectSocket(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                //set app is open
                savePrefBoolean(this, PreferenceConnector.IS_APP_OPEN, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                //timer
                if (timer != null) {
                    timer!!.cancel()
                    timer = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.e("SWAPVID", "navigateForCommonLogin")
            Handler().postDelayed({
                if (!isClickable) {
                    isClickable = true
                    viewModel.setPauseCommunicator(true)
                    addData(
                        this, 0, "", "", "", "", 0,
                        "", "", false, false, false, "",
                        "", "", false, false, false, false, ""
                    )
                    PreferenceConnector.writeBoolean(this, PreferenceConnector.isRemember, false)
                    val intent = Intent(this, CommonLoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    this@WaitingActivity.finish()
                    overridePendingTransition(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )    // for close
                }
            }, 500)
        }
    }

    fun showAlert(title: String, message: String, color: Int) {
        Alerter.create(this)
            .setTitle(title)
            .setText(message)
            .setBackgroundColorRes(color)
            .setDuration(5000)
            .setOnClickListener(View.OnClickListener {
                binding.dropCallButton.performClick()
            })
            .show()
    }

    private fun clearReconnectTimer() {
        if (timerCallReconnect != null) {
            timerCallReconnect!!.cancel()
            timerCallReconnect = null
        }
    }


    //show update app dialog
    private fun showUpdateAppDialog(
        context: Context,
        title: String,
        message: String,
        isUpdated: Boolean
    ) {
        SwapdroidAlertDialog.Builder(context as Activity)
            .setTitle(title)
            .setMessage(message)
            .isMessageVisible(true)
            .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
            .setNegativeBtnText(context.getString(R.string.ok))
            .isNegativeVisible(false)
            .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
            .setPositiveBtnText(context.getString(R.string.ok))
            .isPositiveVisible(true)
            .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
            .setAnimation(SwapdroidAnimation.POP)
            .isCancellable(false)
            .showCancelIcon(false)
            .setIcon(R.drawable.play_store_icon, SwapdroidIcon.Visible)
            .OnPositiveClicked {
                if (isUpdated) {
                    appUpdateManager?.completeUpdate()
                } else {
                    checkForVersionUpdate(false)
                }
            }
            .OnCancelClicked {

            }
            .build()
    }

    //intent call to open play store to update app
    private fun checkForVersionUpdate(forChecking: Boolean) {
        /*  try {
             val packageName = application.packageName
              val intent: Intent = Intent(
                       "android.intent.action.VIEW",
                       Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
               )
               startActivity(intent)
               this@CommonLoginActivity.finish()*/


        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager?.registerListener(object : InstallStateUpdatedListener {
            override fun onStateUpdate(state: InstallState?) {
                if (state?.installStatus() == InstallStatus.DOWNLOADED) {
                    showUpdateAppDialog(
                        this@WaitingActivity,
                        resources.getString(R.string.app_name),
                        "An update has just been downloaded.",
                        true
                    )
                }
            }

        })
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo

        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                availableVersion = BuildConfig.VERSION_NAME.toDouble() + 0.01
                if (forChecking) {
                    showUpdateAppDialog(
                        this@WaitingActivity,
                        resources.getString(R.string.app_name),
                        """${resources.getString(R.string.update_app)} $availableVersion""",
                        false
                    )
                } else {
                    appUpdateManager?.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        this,
                        IN_APP_UPDATE
                    )
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateAppDialog(
                    this,
                    resources.getString(R.string.app_name),
                    "An update has just been downloaded.",
                    true
                )
            }

        }

    }

    private fun initYoutubePlayerDialog() {
        imageViewCloseYoutube.setOnClickListener {
            if (youTubeVideoPlayer != null && youtubeListAdapter !== null) {
                youTubeVideoPlayer?.pause()
                youtubeListAdapter?.positionToPlay = -1
                youtubeListAdapter?.previousPosition = -1
                youtubeListAdapter?.notifyDataSetChanged()
            }
            mBottomSheetYoutubeBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun callAppSubscriptionStatus() {
        try {
            if (isNetworkAvailable(this)) {
                if (AppInstance.userObj!!.getToken() != null && !AppInstance.userObj!!.getToken()
                        ?.isEmpty()!!
                ) {
                    val token = AppInstance.userObj!!.getToken()!!
                    val p = RequestParams();
                    p.add("userid", AppInstance.userObj!!.getId()!!)
                    val rest = RestClient(this, RestMethod.GET, p)
                    rest.setToken(token);
                    rest.execute(WebConstants.APP_SUBSCRIPTION_STATUS_WS);
                } else {
                    expireAccessToken(this@WaitingActivity)
                }
            } else {
                showDialogNoInternet(
                    this@WaitingActivity,
                    getString(R.string.ooops),
                    getString(R.string.check_internet),
                    R.drawable.ic_nointernet_icon
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    override fun clickListener(isNext: Boolean, viewId: Int) {

        println("Click on Next $viewId")
        val item = findViewById<View>(viewId)
        //    val scrollTo = (item.parent.parent as View).top + item.top
        //  nsvParent.smoothScrollTo(0, scrollTo)
        nsvParent.post(Runnable { findDistanceToScroll(item, viewId) })


        //ViewPropertyAnimator.animate(mNestedScrollView).scrollY(mChildView.getTop()).start();
//        nsvParent.scrollBy(item.scrollX, item.scrollY)
        // highlightManager?.getItem(walkOverIndex, item)
    }

    /**
     * Code to check app version is available on play store corrected otherwise show update app dialog
     *  END
     **/

    private fun findDistanceToScroll(view: View, viewId: Int) {
        var scrollTo = 0
        if (R.id.tv_contactus == viewId) {
            scrollTo = nsvParent.top + (view.parent.parent as View).top
        } else
            scrollTo = nsvParent.top + view.top

        nsvParent.smoothScrollTo(0, scrollTo)
    }


    fun logFirebaseEvents(key: String, value: String) {
        val params = Bundle()
        params.putString(key, value)
        firebaseAnalytics.logEvent(TAG, params)
    }


    override fun asyncError(error: ErrorModel?, label: String?, `object`: Any?) {
        println("ASYNC ERROR");
        println(error);
        when (label) {
            WebConstants.GET_QUESTIONS -> {

                noTrainTheAiQuestions()
                setNoAiUI(2)
                if (error != null) {
                    Log.e("SWAPLOG", error.error_message)
                    logFirebaseEvents("postTrainTheAIAnswer", error.error_message)

                }
            }

            WebConstants.SUBMIT_QUESTION_ANSWER -> {
                noTrainTheAiQuestions()
                setNoAiUI(2)
                if (error != null) {
                    Log.e("SWAPLOG", error.error_message)
                    logFirebaseEvents("postTrainTheAIAnswer", error.error_message)
                }
            }

            WebConstants.MATCH_WHILE_REGISTRATION_WS -> {
                if (error != null) {
                    Log.e("SWAPLOG", error.error_message)
                    logFirebaseEvents("postTrainTheAIAnswer", error.error_message)
                }
            }

            WebConstants.GET_TIMER_WS -> {
                //call fetch questions tree  api
                callQuestionsApiTree()

                //call fetch youtube links api
                callYoutubeList()


                if (error != null) {
                    Log.e("SWAPLOG", error.error_message)
                    logFirebaseEvents("postTrainTheAIAnswer", error.error_message)
                }
            }

            WebConstants.GET_YOUTUBE_LINKS_WS -> {
                //call matching and subscription status api
                callMatchSubscriptionStatus()

                Handler().postDelayed({
                    //animate timer
                    animateTimerOrRejoin(hadCall)
                }, 500)

                noYoutubeVideos()
                setNoYoutubeUI(1)
                if (error != null) {
                    Log.e("SWAPLOG", error.error_message)
                    logFirebaseEvents("postTrainTheAIAnswer", error.error_message)
                }
            }
            WebConstants.POST_LOGOUT_WS -> {
                logFirebaseEvents("logoutWSCall", error!!.error_message)
            }
            WebConstants.UPDATE_PHONE_NO -> {
                addPhoneDialog.dismiss()
                if (error != null) {
                    Log.e("SWAPLOG", error.error_message)
                    logFirebaseEvents("updatePhoneNo", error.error_message)
                    showAlert("", error.error_message, R.color.purple_bar)


                }
            }

        }
    }

    override fun asyncResponse(responseData: String?, label: String?, `object`: Any?) {
        println("ASYNC RESPONSE");
        val gson = Gson()
        when (label) {
            WebConstants.GET_YOUTUBE_LINKS_WS -> {

                Handler().postDelayed({
                    //animate timer
                    animateTimerOrRejoin(hadCall)
                }, 500)

                //call matching and subscription status api
                callMatchSubscriptionStatus()

                println("get you tube links ***");
                println(responseData);
                val response = gson.fromJson(responseData, YoutubeLinksResponse::class.java);
                try {
                    when (response.getCode()) {
                        ResponseCodes.Success -> try {
                            youtubeListData = response.getData()!!
                            if (youtubeListData.size > 0) {
                                setYoutube(youtubeListData.get(0).getVideoId()!!)
                                //set fragment_intro_notes
                                youtubeListAdapter =
                                    YoutubeListAdapter(youtubeListData, this@WaitingActivity)
                                binding.rvYoutubeLinks.adapter = youtubeListAdapter

                                youtubeListAdapter?.OnPlayClickListener(this@WaitingActivity)
                                //youtubeListAdapter.positionToPlay = 0
                                //youtubeListAdapter.previousPosition = 0
                                youtubeListAdapter?.notifyDataSetChanged()
                                //loader
                                binding.skLoader.visibility = View.GONE
                                binding.llNoYoutubeLinks.visibility = View.GONE
                                binding.llYoutubeVideoContainer.visibility = View.VISIBLE
                            } else {
                                noYoutubeVideos()
                                setNoYoutubeUI(1)
                            }
                        } catch (e: Exception) {
                            noYoutubeVideos()
                            setNoYoutubeUI(1)
                            e.printStackTrace()
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> expireAccessToken(this@WaitingActivity)
                        else -> {
                            noYoutubeVideos()
                            setNoYoutubeUI(1)
                            Log.e("SWAPLOG", response.getMessage()!!)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    noYoutubeVideos()
                    setNoYoutubeUI(1)
                    Log.e("SWAPLOG", "Error Message: " + e.message)
                    logFirebaseEvents("getYoutubeList", "Error Message: " + e.printStackTrace())

                }
            }

            WebConstants.SUBMIT_QUESTION_ANSWER -> {
                try {
                    println("getNextquestions ***" + responseData);
                    val response = gson.fromJson(responseData, AIQuestionsResponse::class.java)
                    // set date schedule timings
                    if (response.data.timeSlots != null) {

                        if (response.data.timeSlots.timeToText != null && !response.data.timeSlots.timeToText!!.isEmpty()) {

                            if (response.data.timeSlots.timeToText!!.contains("OR")) {
                                var orString =
                                    "<big><b><font color='#ff9800'> OR </font></b></big>";
                                val separatedTimings: List<String> =
                                    response.data.timeSlots.timeToText!!.split("OR")

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    binding.textViewTime1.setText(
                                        Html.fromHtml(
                                            "Sun - Thurs: " + separatedTimings[0] + orString + separatedTimings[1],
                                            HtmlCompat.FROM_HTML_MODE_LEGACY
                                        )
                                    )
                                    binding.textViewTimeTop.setText(
                                        Html.fromHtml(
                                            response.data.timeSlots.timeToText!!,
                                            HtmlCompat.FROM_HTML_MODE_LEGACY
                                        )
                                    )
                                } else {
                                    binding.textViewTime1.setText(
                                        Html.fromHtml(
                                            "Sun - Thurs: " + separatedTimings[0] + orString + separatedTimings[1]
                                        )
                                    )
                                    binding.textViewTimeTop.setText(
                                        Html.fromHtml(
                                            response.data.timeSlots.timeToText!!
                                        )
                                    )
                                }

                            } else {
                                binding.textViewTime1.setText("Sun - Thurs: " + response.data.timeSlots.timeToText)
                                binding.textViewTimeTop.setText(response.data.timeSlots.timeToText)
                                binding.textViewTime2.setText("")
                                binding.textViewOr.visibility = View.GONE
                            }
                        }

                        if (response.data.IsMain()) {
                            // reset timers
                            val timeDuration = response.data.timeSlots.timeForCountDown
                            if (timeDuration != null) {
                                if (timeDuration <= extra15Secs) {
                                    if (!hadCall && !MyApplication.isAppInBackground) {
                                        // navigate for call
                                        navigateForVideoCall()
                                    }
                                } else {
                                    val updatedTime = timeDuration - extra15Secs
                                    timerView(updatedTime)
                                }
                            }
                        }
                    }

                    //update question
                    trainingTheAIData = ArrayList()
                    if (response.data.question != null) {
                        swipeYourTopCard()
                        trainingTheAIData.add(response.data)
                        trainingTheAIAdapter =
                            TrainingTheAIAdapter(trainingTheAIData, this@WaitingActivity)
                        cardStackView.adapter = trainingTheAIAdapter
                        trainingTheAIAdapter.OnCardClickListener(this@WaitingActivity)
                        trainingTheAIAdapter.notifyDataSetChanged()

                        //loader
                        binding.skAILoader.visibility = View.GONE
                        binding.llNoQuestions.visibility = View.GONE
                        binding.cardStackView.visibility = View.VISIBLE
                        cardItemSize = trainingTheAIData.size

                    } else {
                        noTrainTheAiQuestions()
                        setNoAiUI(1)
                    }
                } catch (ex: Exception) {
                    logFirebaseEvents(
                        "SUBMIT_QUESTION_ANSWER",
                        "Error Message: " + ex.printStackTrace()
                    )
                    noTrainTheAiQuestions()
                    setNoAiUI(2)
                }
            }
            WebConstants.GET_QUESTIONS -> {
                try {

                    println("getquestions ***" + responseData);
                    var response = gson.fromJson(responseData, AIQuestionsResponse::class.java);
                    when (response.getCode()) {
                        ResponseCodes.Success -> try {
                            trainingTheAIData = ArrayList()
                            if (response.data.question != null) {
                                trainingTheAIData.add(response.getData()!!)
                                try {
                                    if (youtubeListData.size > 0) {
                                        setYoutube(youtubeListData[0].getVideoId()!!)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    logFirebaseEvents(
                                        "getTrainTheAIList",
                                        "Error Message: " + e.printStackTrace()
                                    )
                                }
                                //adapter
                                trainingTheAIAdapter =
                                    TrainingTheAIAdapter(trainingTheAIData, this@WaitingActivity)
                                cardStackView.adapter = trainingTheAIAdapter
                                trainingTheAIAdapter.OnCardClickListener(this@WaitingActivity)
                                trainingTheAIAdapter.notifyDataSetChanged()
                                //loader
                                binding.skAILoader.visibility = View.GONE
                                binding.llNoQuestions.visibility = View.GONE
                                binding.cardStackView.visibility = View.VISIBLE
                                cardItemSize = trainingTheAIData.size
                            } else {
                                noTrainTheAiQuestions()
                                setNoAiUI(1)
                            }
                        } catch (e: Exception) {
                            noTrainTheAiQuestions()
                            setNoAiUI(2)
                            e.printStackTrace()
                            logFirebaseEvents(
                                "getTrainTheAIList",
                                "Error Message: " + e.printStackTrace()
                            )
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> expireAccessToken(this@WaitingActivity)
                        else -> {
                            noTrainTheAiQuestions()
                            setNoAiUI(2)
                            Log.e("SWAPLOG", response.getMessage()!!)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    noTrainTheAiQuestions()
                    setNoAiUI(2)
                    Log.e("SWAPLOG", e.message!!)
                    logFirebaseEvents("getTrainTheAIList", "Error Message: " + e.printStackTrace())
                }
            }

            WebConstants.MATCH_WHILE_REGISTRATION_WS -> {
                println("match while registration ***");
                val response = gson.fromJson(responseData, MainModel::class.java);
                try {
                    when (response.getCode()) {
                        ResponseCodes.Success -> Log.e("SWAPLOG", response.getMessage()!!)
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> expireAccessToken(this@WaitingActivity)
                        else -> Log.e("SWAPLOG", response.getMessage()!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logFirebaseEvents(
                        "callMatchWebService",
                        "Error Message: " + e.printStackTrace()
                    )
                }
            }
            WebConstants.UPDATE_PHONE_NO -> {
                println("updatePhoneNo ***" + responseData);
                try {
                    addPhoneDialog.dismiss()
                    val response = gson.fromJson(responseData, UpdatePhoneResponse::class.java);
                    addData(
                        this,
                        AppInstance.userObj!!.getLoginType()!!,
                        AppInstance.userObj!!.getId()!!,
                        AppInstance.userObj!!.getFirstName()!!,
                        AppInstance.userObj!!.getLastName()!!,
                        AppInstance.userObj!!.getEmail()!!,
                        AppInstance.userObj!!.getGender()!!,
                        AppInstance.userObj!!.getDOB()!!,
                        AppInstance.userObj!!.getToken()!!,
                        AppInstance.userObj!!.getIsNormalQuizComplete()!!,
                        AppInstance.userObj!!.getIsBibleQuizComplete()!!,
                        AppInstance.userObj!!.getIsActive()!!,
                        AppInstance.userObj!!.getFacebookSocialId()!!,
                        AppInstance.userObj!!.getGoogleSocialId()!!,
                        AppInstance.userObj!!.getPassword()!!,
                        AppInstance.userObj!!.getIsSignUpPerformed()!!,
                        AppInstance.userObj!!.getIsBlocked()!!,
                        AppInstance.userObj!!.getSubscriptionDone()!!,
                        AppInstance.userObj!!.getSubscriptionFree()!!,
                        response?.data?.phoneNo!!
                    )

                    showAlert("", response.message!!, R.color.purple_bar)
                } catch (e: Exception) {
                    e.printStackTrace()
                    logFirebaseEvents("updatePhoneNo", "Error Message: " + e.printStackTrace())
                }
            }

            WebConstants.GET_TIMER_WS -> {
                try {
                    //call fetch questions tree  api
                    callQuestionsApiTree()

                    //call fetch youtube links api
                    callYoutubeList()

                    println("timer ***" + responseData);
                    val response = gson.fromJson(responseData, CountDownTimerResponse::class.java);
                    when (response.code) {
                        ResponseCodes.Success -> {
                            binding.textViewTimeTitle.text = response.data?.datingEvent!!
                            binding.tvRejoinListTitle.text = response.data?.rejoin!!
                            binding.tvBestMatchListTitle.text = response.data?.bestMatch!!
                            binding.tvYouTubeClassesList.text = response.data?.youtubeClassList!!
                            binding.tvTrainAI.text = response.data?.introduceYourself!!
                            hadCall = response.data?.hadCall!!
                            val timeDuration = response.data?.timeForCountDown!!
                            //val timeDuration = 60000
                            val dayOfWeek = response.data.dayOfWeek
                            val dateTimings = response.data.timeToText
                            if (timeDuration != null) {
                                if (timeDuration <= extra15Secs) {
                                    if (!hadCall && !MyApplication.isAppInBackground) {
                                        // navigate for call
                                        navigateForVideoCall()
                                    }
                                } else {
                                    val updatedTime = timeDuration - extra15Secs
                                    timerView(updatedTime)
                                }
                            }
                            if (dateTimings != null && !dateTimings.isEmpty()) {
                                if (dateTimings.contains("OR")) {
                                    // val str = "8:00, 8:10, 8:20, 8:30, 8:30, 8:40, 8:45, 8:50 OR 9:00 PM ET"
                                    val separatedTimings: List<String> = dateTimings.split("OR")
                                    var orString =
                                        "<big><b><font color='#ff9800'> OR </font></b></big>";
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        binding.textViewTime1.setText(
                                            Html.fromHtml(
                                                "Sun - Thurs: " + separatedTimings[0] + orString + separatedTimings[1],
                                                HtmlCompat.FROM_HTML_MODE_LEGACY
                                            )
                                        )
                                        binding.textViewTimeTop.setText(
                                            Html.fromHtml(
                                                dateTimings,
                                                HtmlCompat.FROM_HTML_MODE_LEGACY
                                            )
                                        )
                                    } else {
                                        binding.textViewTime1.setText(
                                            Html.fromHtml(
                                                "Sun - Thurs: " + separatedTimings[0] + orString + separatedTimings[1]
                                            )
                                        )
                                        binding.textViewTimeTop.setText(
                                            Html.fromHtml(
                                                dateTimings
                                            )
                                        )

                                    }
                                    //binding.textViewTime1.setText("Sun - Thurs: " + separatedTimings[0])
                                    //   binding.textViewTime2.setText(separatedTimings[1])
                                    //  binding.textViewOr.visibility = View.VISIBLE
                                } else {
                                    binding.textViewTime1.setText("Sun - Thurs: " + dateTimings)
                                    binding.textViewTimeTop.setText(dateTimings)
                                    binding.textViewTime2.setText("")
                                    binding.textViewOr.visibility = View.GONE
                                }
                            }

                            if (hadCall) {
                                binding.textViewNextDateDay.text = dayOfWeek
                                binding.llNextDate.visibility = View.VISIBLE
                                binding.topLayout.visibility = View.GONE
                            } else {
                                binding.llNextDate.visibility = View.GONE
                                binding.topLayout.visibility = View.VISIBLE
                            }
                        }
                        else -> Log.e("SWAPLOG", response.message!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("SWAPLOG", e.message!!)
                    logFirebaseEvents("timerapi", "Error Message: " + e.printStackTrace())
                }
            }
            WebConstants.POST_LOGOUT_WS -> {
                try {
                    val response = gson.fromJson(responseData, MainModel::class.java);
                    when (response.getCode()) {
                        ResponseCodes.Success -> Log.e("SWAPLOG", response.getMessage()!!)
                        else -> Log.e("SWAPLOG", response.getMessage()!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("SWAPLOG", e.message!!)
                    logFirebaseEvents("logoutWSCall", "Error Message: " + e.printStackTrace())
                }

            }

            WebConstants.APP_SUBSCRIPTION_STATUS_WS -> {
                println("subscription_status ***" + responseData);
                //commenting subscription code
//                try {
//                    val response = gson.fromJson(responseData, SubscriptionStatus::class.java);
//                    if (!response.data?.subscriptionStatus!!)
//                    navigateToSubscription()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    Log.e("SWAPLOG", e.message!!)
//                    logFirebaseEvents("subscriptionCall", "Error Message: " + e.printStackTrace())
//                }
            }
        }
    }

    private fun navigateToSubscription() {
        startActivity(Intent(this, SubscriptionActivity::class.java))
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    private fun bottomSheetYoutubeView() {
        mBottomSheetYoutubeBehaviour = BottomSheetBehavior.from(llYoutubePlayer)
        mBottomSheetYoutubeBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED

        mBottomSheetCallBehaviour.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED
                    || newState == BottomSheetBehavior.STATE_HIDDEN
                ) {
                    if (youTubeVideoPlayer != null && youtubeListAdapter != null) {
                        youTubeVideoPlayer?.pause()
                        youtubeListAdapter?.positionToPlay = -1
                        youtubeListAdapter?.previousPosition = -1
                        youtubeListAdapter?.notifyDataSetChanged()
                    }
                }
            }

        })
    }


    private fun bottomSheetDropCallView() {
        mBottomSheetCallBehaviour = BottomSheetBehavior.from(dropCallView)
        mBottomSheetCallBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun bottomSideDropCallData(userName: String, id: String, type: String) {
        if (AppInstance.userObj?.getGender()!! == 1) { //Female
            ivUserImage.setImageResource(R.drawable.ic_male)
            ivGenderSign.setImageResource(R.drawable.ic_male_sign)
        } else {
            ivUserImage.setImageResource(R.drawable.ic_female)
            ivGenderSign.setImageResource(R.drawable.ic_female_sign)
        }

        if (type.equals("rejoin")) {
            textViewTitle.text = resources.getString(R.string.rejoin_call_now)
            tvRejoinMessage.text = resources.getString(R.string.went_wrong_with_previous_call)
        } else {
            textViewTitle.text = resources.getString(R.string.both_mutual_yes)
            tvRejoinMessage.text =
                "You can choose to go out at anytime with " + userName + "\nWould you like to go out again now?"
        }

        tvUsername.setText(userName)

        positiveButton.setOnClickListener {
            reconnectStatus = "accept"
            //   SocketCommunication.emitCallRequestActivity(id)
            SocketCommunication.emitRejoinRequest(id, userName, type)
            mBottomSheetCallBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED

        }

        backView.setOnClickListener {
            mBottomSheetCallBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED

        }
        negativeButton.setOnClickListener {
            mBottomSheetCallBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED

        }

        imageViewClose.setOnClickListener {
            //binding.dropCallButton.visibility = View.GONE
            //binding.imageViewRightClick.visibility = View.GONE
            //clearAnimation()
            //clearCallData(applicationContext)
            mBottomSheetCallBehaviour!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onRejoinClick(userID: String, userName: String, rejoinType: String) {
        matcheUserName = userName;
        if (matcheUserName?.contains(" ")!!)
            matcheUserName = matcheUserName.split(" ")[0]

        viewModel.setPauseCommunicator(true)
        startActivityForResult(
            Intent(this, ChatActivity::class.java)
                .putExtra(REJOIN_USERID, userID)
                .putExtra(REJOIN_TYPE, rejoinType)
                .putExtra(REJOIN_USERNAME, matcheUserName)
                .putExtra(CHAT_USER_TYPE, REJOIN),
            CHAT_SCREEN_CODE

        )
        //    finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        //  bottomSideDropCallData(matcheUserName, userID, rejoinType)
        //  mBottomSheetCallBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun onRemoveUser(userID: String, rejoinType: String, matchedUserName: String) {
        dialogRemoveUserFromRejoin(userID, rejoinType, matchedUserName, true)
    }

    override fun onBestMatchClick(userID: String, userName: String) {
        var matcheUserName = userName;
        if (matcheUserName?.contains(" ")!!)
            matcheUserName = matcheUserName.split(" ")[0]

        viewModel.setPauseCommunicator(true)
        startActivityForResult(
            Intent(this, ChatActivity::class.java)
                .putExtra(REJOIN_USERID, userID)
                .putExtra(REJOIN_TYPE, MUTUAL_TYPE)
                .putExtra(REJOIN_USERNAME, matcheUserName)
                .putExtra(CHAT_USER_TYPE, BEST_MATCH), CHAT_SCREEN_CODE
        )
        //  finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun PickiTonUriReturned() {

    }

    override fun PickiTonProgressUpdate(progress: Int) {
    }

    override fun PickiTonStartListener() {
        binding.skUploadLoader.visibility = View.VISIBLE
    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        binding.skUploadLoader.visibility = View.GONE
        if (path != null && path.isNotEmpty()) {
            if (isResumeSelected) {
                filePath = path
                binding.imageViewResume.setImageResource(R.drawable.ic_document)
                uploadFileOnFBStorage()
            } else {
                imagePath = path
                Picasso.get()
                    .load(File(path))
                    .placeholder(R.drawable.ic_upload_white)
                    .error(R.drawable.ic_upload_white)
                    .into(binding.imageViewPic)

                uploadImageOnFBStorage()
            }
        }

    }

    override fun onNetworkListener(isConnected: Boolean) {

    }

}



