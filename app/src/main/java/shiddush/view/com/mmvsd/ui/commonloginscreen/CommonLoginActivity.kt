package shiddush.view.com.mmvsd.ui.commonloginscreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import shiddush.view.com.mmvsd.BuildConfig
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityCommonLoginBinding
import shiddush.view.com.mmvsd.model.login.LoginData
import shiddush.view.com.mmvsd.model.login.LoginResponse
import shiddush.view.com.mmvsd.model.review.AddReviewResponse
import shiddush.view.com.mmvsd.model.sociallogin.SocialLoginResponse
import shiddush.view.com.mmvsd.model.subscription.CheckSubscriptionResponse
import shiddush.view.com.mmvsd.model.subscriptionStatus.SubscriptionStatus
import shiddush.view.com.mmvsd.radioplayer.RadioPlayer
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.rest.RequestParams
import shiddush.view.com.mmvsd.rest.RestClient
import shiddush.view.com.mmvsd.rest.RestMethod
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.ui.review.ReviewActivity
import shiddush.view.com.mmvsd.ui.subscription.SubscriptionActivity
import shiddush.view.com.mmvsd.ui.tnc.TermsAndConActivity
import shiddush.view.com.mmvsd.ui.waitingscreen.WaitingActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.widget.loader.UtilLoader
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidColorConstants
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon
import java.util.*

/**
 * Created by Sumit Kumar.
 * release : https://stackoverflow.com/questions/25001479/app-release-unsigned-apk-is-not-signed/34964168#34964168
 */
class CommonLoginActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, MultiplePermissionsListener,
        RestClient.OnAsyncRequestComplete, RestClient.OnAsyncRequestError {

    private var availableVersion = 0.0
    private var newVersion = ""
    private var appUpdateManager: AppUpdateManager? = null
    lateinit var binding: ActivityCommonLoginBinding
    private lateinit var viewModel: CommonLoginViewModel
    private var newUpdate: Boolean = false
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    protected var mLastLocation: Location? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private val TAG = CommonLoginActivity::class.java.simpleName

    /**
     * Provides the entry point to the Fused Location Provider API.
     */

    //Google
    val RC_SIGN_IN: Int = 1
    val IN_APP_UPDATE: Int = 101
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mUtilLoader: UtilLoader
    private var isLoading: Boolean = false
    private var isPassVisible: Boolean = false
    lateinit var googleSignInAccount: GoogleSignInAccount
    private var to_userid: String = ""
    private var isSocialLogin: Boolean = false
    private lateinit var loginResponse: LoginResponse
    private lateinit var socialLoginResponse: SocialLoginResponse

    companion object {
        private val LOG_TAG = CommonLoginActivity::class.java.simpleName
        private val RC_SETTINGS_SCREEN_PERM = 123
        private const val RC_VIDEO_APP_PERM = 124
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@CommonLoginActivity,
                R.layout.activity_common_login)
        viewModel = CommonLoginViewModel()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        binding.viewModel = viewModel
        mUtilLoader = UtilLoader(this)
        changeNavBack()
        configureGoogleSignIn()
        setupUI()
        setListeners()
        firebaseAuth = FirebaseAuth.getInstance()
        try {
            checkForVersionUpdate(true);
            //GetVersionCode().execute()
            val density = getResources().getDisplayMetrics().densityDpi
            Log.d("codessss", density.toString() + "")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermissions()
    }

    val permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun locationPermissions() {
        Dexter.withActivity(this)
                .withPermissions(permissions)
                .withListener(this)
                .check()
    }

    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
        // This method will be called when the user rejects a permission request
        // You must display a dialog box that explains to the user why the application needs this permission
    }

    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
        // Here you have to check granted permissions
        Log.e("permissions", "===== " + report.areAllPermissionsGranted())
        if (report.areAllPermissionsGranted()) {
            getLastLocation()
        }
    }


    private fun setListeners() {
        //email listener
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 0) {
                    binding.etEmail.error = null
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        //password listener
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    binding.etPassword.error = null
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.etPassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideVirtualKeyboard(this)
                //onSignClick()
                true
            } else {
                hideVirtualKeyboard(this)
                false
            }
        }

        binding.signIn.setOnClickListener {
            onSignClick()
        }

        binding.ivPass.setOnClickListener {
            if (isPassVisible) {
                isPassVisible = false
                binding.etPassword.transformationMethod = PasswordTransformationMethod()
                binding.ivPass.setImageResource(R.drawable.ic_pass_hide)
            } else {
                isPassVisible = true
                binding.etPassword.transformationMethod = null
                binding.ivPass.setImageResource(R.drawable.ic_pass_view)
                binding.ivPass.setImageResource(R.drawable.ic_pass_view)
            }
        }


    }

    //on click sign in button
    private fun onSignClick() {
        try {
            hideVirtualKeyboard(this)
            if (checkValidation()) {
                if (!isLoading) {
                    isLoading = true
                    showLoadingDialog(isLoading)

                    val p = RequestParams();
                    p.add("email", binding.etEmail.text!!.toString())
                    p.add("password", binding.etPassword.text!!.toString())
                    p.add("device_type", WebConstants.DEVICE_TYPE)
                    p.add("device_token", getPrefString(this, PreferenceConnector.DEVICE_TOKEN))
                    p.add("appversion", BuildConfig.VERSION_NAME)
                    p.add("deviceInfo", getDeviceInfo())
                    p.add("lat", "" + getPrefString(this, PreferenceConnector.LAT))
                    p.add("lng", "" + getPrefString(this, PreferenceConnector.LNG))
                    p.add("city", "" + getPrefString(this, PreferenceConnector.CITY))
                    p.add("country", "" + getPrefString(this, PreferenceConnector.COUNTRY))
                    p.add("countryCode", "" + getPrefString(this, PreferenceConnector.COUNTRYCODE))
                    val rest = RestClient(this, RestMethod.POST, p)
                    rest.execute(WebConstants.LOGIN_WS);
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //check validations
    private fun checkValidation(): Boolean {
        var isValid = false
        if (binding.etEmail.text!!.trim().isEmpty()) {
            binding.etEmail.error = getString(R.string.please_enter_email)
            binding.etEmail.requestFocus()
            isValid = false

        } else if (!isValidEmail(binding.etEmail.text!!)) {
            binding.etEmail.error = getString(R.string.please_enter_valid_email)
            binding.etEmail.requestFocus()
            isValid = false

            logFirebaseEvents("checkValidation", getString(R.string.please_enter_valid_email))

        } else if (binding.etPassword.text!!.trim().isEmpty()) {
            binding.etPassword.error = getString(R.string.please_enter_password)
            binding.etPassword.requestFocus()
            isValid = false
        } else {
            isValid = true
            binding.etEmail.error = null
            binding.etPassword.error = null
        }
        return isValid
    }


    private fun showLoadingDialog(show: Boolean) {
        if (show) mUtilLoader.startLoader(this) else mUtilLoader.stopLoader()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLastLocation = task.result
                        mLastLocation?.let { getAddressFromLocation(it) }
                    } else {
                        Log.e("", "getLastLocation:exc ", task.exception)
                    }
                }
    }

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var city: String = ""
    var country: String = ""
    var countryCode: String = ""

    fun getAddressFromLocation(mLastLocation: Location) {

        //Set Address
        try {
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude
            Log.e("currentlocation", "===" + latitude + " , " + longitude)

            var geocoder: Geocoder = Geocoder(this, Locale.getDefault())
            var addresses: List<Address> = geocoder.getFromLocation(mLastLocation.latitude, mLastLocation.longitude, 1)
            if (addresses != null && addresses.size > 0) {

                city = addresses.get(0).locality
                country = addresses.get(0).countryName
                countryCode = addresses.get(0).countryCode

                savePrefString(this, PreferenceConnector.LAT, "" + latitude)
                savePrefString(this, PreferenceConnector.LNG, "" + longitude)
                savePrefString(this, PreferenceConnector.CITY, "" + city)
                savePrefString(this, PreferenceConnector.COUNTRY, "" + country)
                savePrefString(this, PreferenceConnector.COUNTRYCODE, "" + countryCode)

                Log.d("currentlocation", "getAddress:  city" + city)
                Log.d("currentlocation", "getAddress:  country" + country)
                Log.d("countryCode", "countryCode:  country" + countryCode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * to change bottom navigation bar color
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
     * to configure Google signin
     */
    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun setupUI() {
        binding.ivGoogleSignin.setOnClickListener {
            signIn()
        }
    }

    /**
     * Google sign in
     */
    private fun signIn() {
        if (isNetworkAvailable(this)) {
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } else {
            showDialogNoInternet(this, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
        }
    }

    /**
     * Google sign out
     */
    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    /**
     * on activity result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)

                try {
                    FirebaseAuth.getInstance().signOut()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                e.printStackTrace()
                showToast(this, getString(R.string.google_login_failed), Toast.LENGTH_SHORT)
            }
        } else if (requestCode == IN_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                showUpdateAppDialog(
                        this@CommonLoginActivity,
                        resources.getString(R.string.app_name),
                        """${resources.getString(R.string.update_app)} $availableVersion""",
                        false
                )
            } else {
                showToast(this, getString(R.string.downloading), Toast.LENGTH_SHORT)
            }
        } else {
            viewModel.mFacebookCustomClass.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * this function use to retrieve all google account details
     * @param acct : contain user details
     */
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                //go for social login
                socialLogin(acct, this@CommonLoginActivity)
            } else {
                showToast(this, getString(R.string.google_login_failed), Toast.LENGTH_SHORT)
            }
        }
    }

    /**
     * Social Login with Google
     */
    private fun socialLogin(googleSignInAccount: GoogleSignInAccount, context: Context) {
        this.googleSignInAccount = googleSignInAccount
        try {
            mUtilLoader.startLoader(context)
            val p = RequestParams();
            p.add("firstName", googleSignInAccount.displayName)
            p.add("lastName", "")
            p.add("email", googleSignInAccount.email)
            p.add("gender", 0)
            p.add("DOB", "")
            p.add("loginType", 2)
            p.add("facebookSocialId", "")
            p.add("googleSocialId", googleSignInAccount.id)
            p.add("isTandC", false)
            p.add("device_type", WebConstants.DEVICE_TYPE)
            p.add("device_token", getPrefString(context, PreferenceConnector.DEVICE_TOKEN))
            p.add("appversion", BuildConfig.VERSION_NAME)
            p.add("deviceInfo", getDeviceInfo())
            p.add("lat", "" + getPrefString(this, PreferenceConnector.LAT))
            p.add("lng", "" + getPrefString(this, PreferenceConnector.LNG))
            p.add("city", "" + getPrefString(this, PreferenceConnector.CITY))
            p.add("country", "" + getPrefString(this, PreferenceConnector.COUNTRY))
            p.add("countryCode", "" + getPrefString(this, PreferenceConnector.COUNTRYCODE))
            val rest = RestClient(this, RestMethod.POST, p);
            rest.execute(WebConstants.SOCIAL_LOGIN_WS);
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("socialLogin", "Error Message: " + e.printStackTrace()!!)
        }
    }

    //++Permission
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
                Log.e(LOG_TAG, "hasPermissions:")
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, *perms)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //--Permission

    override fun onResume() {
        super.onResume()
        RadioPlayer.pauseRadio()

        appUpdateManager
                ?.appUpdateInfo
                ?.addOnSuccessListener { appUpdateInfo ->
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        showUpdateAppDialog(
                                this@CommonLoginActivity,
                                resources.getString(R.string.app_name),
                                "An update has just been downloaded.",
                                true
                        )
                    }

                }
    }

    override fun onBackPressed() {
        this@CommonLoginActivity.finish()
    }

    /**
     *  check app subscription details
     */
    fun checkAppSubscriptionDetail() {
        try {
            if (isNetworkAvailable(this)) {
                mUtilLoader.startLoader(this)
                val token = AppInstance.userObj!!.getToken()!!
                val p = RequestParams();
                p.add("userid", AppInstance.userObj!!.getId()!!)
                p.add("packageName", BuildConfig.APPLICATION_ID)
                p.add("productId", ITEM_SKU)
                p.add("device_type", DEVICE_TYPE)
                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.APP_SUBSCRIPTION_DETAIL_WS);

            } else {
                showDialogNoInternet(this, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            goForHome(this@CommonLoginActivity)
            logFirebaseEvents("checkAppSubscriptionDetail", "Error Message: " + e.message!!)
        }
    }

    /**
     * navigate to signup
     */
    private fun goForSignUP(context: Context) {
        val intent = Intent(context, TermsAndConActivity::class.java)
        intent.putExtra(SOURCE, COMMON_LOGIN)
        intent.putExtra(VIA, GOOGLE)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * navigate to waiting screen
     */
    private fun goForHome(context: Context) {
        val intent = Intent(context, WaitingActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * navigate to SubscriptionActivity
     */
    private fun gotoSubscriptionActivity(context: Context) {
        val intent = Intent(context, SubscriptionActivity::class.java)
        intent.putExtra(SOURCE, COMMON_LOGIN)
        intent.putExtra(VIA, NORMAL)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * navigate to signup dailed screen
     */
    private fun signUpSuccessOrFailed(context: Context, tag: String) {
        val intent = Intent(context, OtherScreensActivity::class.java)
        intent.putExtra(SOURCE, tag)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     *  navigation to Review User
     */
    private fun navigateToReviewUser(to_userid: String) {
        val intent = Intent(this, ReviewActivity::class.java)
        intent.putExtra(TOID, to_userid)
        startActivity(intent)
        this@CommonLoginActivity.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }


    private fun showUpdateAppDialog(context: Context, title: String, message: String, isUpdated: Boolean) {
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

                .build()
    }

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
                            this@CommonLoginActivity,
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
                            this@CommonLoginActivity,
                            resources.getString(R.string.app_name),
                            """${resources.getString(R.string.update_app)} $availableVersion""",
                            false
                    )
                } else {
                    appUpdateManager?.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            this,
                            IN_APP_UPDATE)
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
                showDialogNoInternet(this@CommonLoginActivity, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("SWAPLOG", e.message!!)
            showDialogNoInternet(this@CommonLoginActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
        }
    }


    private fun checkAppSubscriptionStatus() {
        val token = AppInstance.userObj!!.getToken()!!
        val userid = AppInstance.userObj!!.getId()!!
        val p = RequestParams();
        p.add("userid", userid)
        val rest = RestClient(this, RestMethod.GET, p)
        rest.setToken(token);
        rest.execute(WebConstants.APP_SUBSCRIPTION_STATUS_WS);
    }


    /**
     * navigate to sign up
     */
    private fun gotoSignup() {
        val intent = Intent(this, TermsAndConActivity::class.java)
        intent.putExtra(SOURCE, LOGIN)
        intent.putExtra(VIA, NORMAL)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }


    fun scheduleAlarms(data: LoginData) {
        //if (data.getlo)
        if (data != null) {
            savePrefBoolean(this, PreferenceConnector.IS_LOCATION_ALARM_ENABLED, data.getLocationAlarmEnabled())
            savePrefBoolean(this, PreferenceConnector.IS_GENDER_ALARM_ENABLED, data.getGenderAlarmEnabled())
            if (data.getLocationAlarmEnabled()) {
                //scheduleAlarms()
            }
        }
        goToHome()
    }

    /**
     * navigate to waiting screen
     */
    private fun goToHome() {
        val intent = Intent(this, WaitingActivity::class.java)
        startActivity(intent)
        this@CommonLoginActivity.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }


    fun logFirebaseEvents(key: String, value: String) {
        val params = Bundle()
        params.putString(key, value)
        firebaseAnalytics?.logEvent(TAG, params)
    }

    override fun asyncResponse(responseData: String?, label: String?, `object`: Any?) {
        val gson = Gson();
        when (label) {
            WebConstants.SOCIAL_LOGIN_WS -> {
                isSocialLogin = true
                println(responseData);
                socialLoginResponse = gson.fromJson(responseData, SocialLoginResponse::class.java);
                try {
                    signOut()
                    when (socialLoginResponse.getCode()) {
                        ResponseCodes.Success -> //positive response
                            try {
                                AppInstance.sociallogObj = socialLoginResponse
                                addData(this@CommonLoginActivity, 2, socialLoginResponse.getData()!!.getId()!!, socialLoginResponse.getData()!!.getFirstName()!!, socialLoginResponse.getData()!!.getLastName()!!, socialLoginResponse.getData()!!.getEmail()!!, socialLoginResponse.getData()!!.getGender()!!, socialLoginResponse.getData()!!.getDOB()!!, socialLoginResponse.getData()!!.getToken()!!, socialLoginResponse.getData()!!.getIsNormalQuizComplete()!!, socialLoginResponse.getData()!!.getIsBibleQuizComplete()!!, socialLoginResponse.getData()!!.getIsActive()!!, socialLoginResponse.getData()!!.getFacebookSocialId()!!, socialLoginResponse.getData()!!.getGoogleSocialId()!!, "", socialLoginResponse.getData()!!.getIsSignUpPerformed()!!, socialLoginResponse.getData()!!.getIsBlocked()!!, socialLoginResponse.getData()!!.getSubscriptionStatus()!!, socialLoginResponse.getData()!!.getSubscriptionStatus()!!, socialLoginResponse.getData()!!.getPhoneNo()!!)
                                when {
                                    socialLoginResponse.getData()!!.getIsBlocked()!! -> signUpSuccessOrFailed(applicationContext, SIGNUP_FAILED)
                                    !socialLoginResponse.getData()!!.getIsSignUpPerformed()!! || !socialLoginResponse.getData()!!.getIsNormalQuizComplete()!! || !socialLoginResponse.getData()!!.getIsBibleQuizComplete()!! -> goForSignUP(applicationContext)
                                    !socialLoginResponse.getData()!!.getSubscriptionFree()!! && !socialLoginResponse.getData()!!.getSubscriptionDone()!! -> checkAppSubscriptionDetail()
                                    !socialLoginResponse.getData()!!.getSubscriptionFree()!! && socialLoginResponse.getData()!!.getSubscriptionDone()!! -> checkAppSubscriptionDetail()
                                    else -> goForHome(applicationContext)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                showDialogNoInternet(applicationContext, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                                logFirebaseEvents("socialLogin", "Error Message: " + e.message!!)
                            }
                        ResponseCodes.NoUserFound -> //first time user
                            try {
                                addData(this@CommonLoginActivity, 2, "", googleSignInAccount.displayName!!, "", googleSignInAccount.email!!, 0, "", "", false, false, false, "", googleSignInAccount.id!!, "", false, false, false, false, "")
                                goForSignUP(applicationContext)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                addData(this@CommonLoginActivity, 2, "", googleSignInAccount.displayName!!, "", googleSignInAccount.email!!, 0, "", "", false, false, false, "", googleSignInAccount.id!!, "", false, false, false, false, "")
                                goForSignUP(applicationContext)
                                logFirebaseEvents("socialLogin", "Error Message: " + e.message!!)
                            }
                        ResponseCodes.ReviewNotDone -> { //review not done
                            AppInstance.sociallogObj = socialLoginResponse
                            addData(this@CommonLoginActivity, 2, socialLoginResponse.getData()!!.getId()!!, socialLoginResponse.getData()!!.getFirstName()!!, socialLoginResponse.getData()!!.getLastName()!!, socialLoginResponse.getData()!!.getEmail()!!, socialLoginResponse.getData()!!.getGender()!!, socialLoginResponse.getData()!!.getDOB()!!, socialLoginResponse.getData()!!.getToken()!!, socialLoginResponse.getData()!!.getIsNormalQuizComplete()!!, socialLoginResponse.getData()!!.getIsBibleQuizComplete()!!, socialLoginResponse.getData()!!.getIsActive()!!, socialLoginResponse.getData()!!.getFacebookSocialId()!!, socialLoginResponse.getData()!!.getGoogleSocialId()!!, "", socialLoginResponse.getData()!!.getIsSignUpPerformed()!!, socialLoginResponse.getData()!!.getIsBlocked()!!, socialLoginResponse.getData()!!.getSubscriptionStatus()!!, socialLoginResponse.getData()!!.getSubscriptionStatus()!!, socialLoginResponse.getData()!!.getPhoneNo()!!)

//                            navigateToReviewUser(response.getData()!!.getReviewFor()!!)
                            to_userid = socialLoginResponse.getData()!!.getReviewFor()!!
                            autoReviewApiCall()
                        }
                        else -> showDialogNoInternet(applicationContext, socialLoginResponse.getMessage()!!, "", R.drawable.ic_alert_icon)
                    }
                    mUtilLoader.stopLoader()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            WebConstants.APP_SUBSCRIPTION_STATUS_WS -> {
                println("subscription_status ***" + responseData);
                val response = gson.fromJson(responseData, SubscriptionStatus::class.java);
                //commenting subscription code
                AppInstance.userObj!!.setIsSubscriptionDone(response.data?.subscriptionStatus!!)
                AppInstance.userObj!!.setIsSubscriptionFree(response.data?.subscriptionStatus!!)
                goToHome()

//                if (!response.data?.subscriptionStatus!!)
//                    gotoSubscriptionActivity(this@CommonLoginActivity)
//                else {
//                    AppInstance.userObj!!.setIsSubscriptionDone(response.data?.subscriptionStatus!!)
//                    AppInstance.userObj!!.setIsSubscriptionFree(response.data?.subscriptionStatus!!)
//                    goToHome()
//                }
            }

            WebConstants.APP_SUBSCRIPTION_DETAIL_WS -> {
                println("check_for_subscription***$responseData");
                try {
                    val response = gson.fromJson(responseData, CheckSubscriptionResponse::class.java);
                    when (response.code) {
                        ResponseCodes.Success -> //success
                        {
                            if (response.data.payload.cancelReason == -1 && response.data.payload.paymentState == 2) { // not cancel and free
                                goForHome(this@CommonLoginActivity)
                            } else if (response.data.payload.cancelReason != -1 && response.data.payload.paymentState == 2) { // cancel and free
                                goForHome(this@CommonLoginActivity)
                            } else if (response.data.payload.cancelReason != -1) { // cancel and not free
                                //commenting subscription code
                                goForHome(this@CommonLoginActivity)

//                                if (System.currentTimeMillis() > response.data.payload.expiryTimeMillis && !response.data.payload.autoRenewing)
//                                    gotoSubscriptionActivity(this@CommonLoginActivity)
//                                else
//                                    showSubscriptionCancelDialog(this@CommonLoginActivity, getString(R.string.renew_cancellation))
                            } else { //not cancelled
                                when (response.data.payload.paymentState) {
                                    0 -> showSubscriptionCancelDialog(this@CommonLoginActivity, getString(R.string.payment_pending))
                                    1, 2 -> goForHome(this@CommonLoginActivity)
                                    3 -> showSubscriptionCancelDialog(this@CommonLoginActivity, getString(R.string.price_changed))
                                }
                            }
                        }
                        else -> {
                            Log.e("SWAPLOG", response.message)
                            checkAppSubscriptionStatus()
                        }
                    }
                    mUtilLoader.stopLoader()
                } catch (e: Exception) {
                    e.printStackTrace()
                    checkAppSubscriptionStatus()
                    logFirebaseEvents("checkAppSubscriptionDetail", "Error Message: " + e.message!!)
                }
            }

            WebConstants.ADD_REVIEW_WS -> {
                savePrefString(this, PreferenceConnector.REVIEW_TO_USER_ID, "")
                val response = gson.fromJson(responseData, AddReviewResponse::class.java);
                when (response.code) {
                    ResponseCodes.Success -> {
                        Log.e("SWAPLOG", response.message!!)
                        if (isSocialLogin) {
                            when {
                                socialLoginResponse.getData()!!.getIsBlocked()!! -> signUpSuccessOrFailed(applicationContext, SIGNUP_FAILED)
                                !socialLoginResponse.getData()!!.getIsSignUpPerformed()!! || !socialLoginResponse.getData()!!.getIsNormalQuizComplete()!! || !socialLoginResponse.getData()!!.getIsBibleQuizComplete()!! -> goForSignUP(applicationContext)
                                !socialLoginResponse.getData()!!.getSubscriptionFree()!! && !socialLoginResponse.getData()!!.getSubscriptionDone()!! -> checkAppSubscriptionDetail()
                                !socialLoginResponse.getData()!!.getSubscriptionFree()!! && socialLoginResponse.getData()!!.getSubscriptionDone()!! -> checkAppSubscriptionDetail()
                                else -> goForHome(applicationContext)
                            }
                        } else {
                            when {
                                loginResponse.getData()!!.getIsBlocked()!! -> signUpSuccessOrFailed(this, SIGNUP_FAILED)
                                !loginResponse.getData()!!.getIsSignUpPerformed()!! || !loginResponse.getData()!!.getIsNormalQuizComplete()!! || !loginResponse.getData()!!.getIsBibleQuizComplete()!! -> gotoSignup()
                                !loginResponse.getData()!!.getSubscriptionFree()!! && !loginResponse.getData()!!.getSubscriptionDone()!! -> checkAppSubscriptionDetail()
                                !loginResponse.getData()!!.getSubscriptionFree()!! && loginResponse.getData()!!.getSubscriptionDone()!! -> checkAppSubscriptionDetail()
                                else -> scheduleAlarms(loginResponse.getData()!!)
                            }
                        }

                    }
                    ResponseCodes.ACCESS_TOKEN_EXPIRED -> expireAccessToken(this)
                    else -> {
                        Log.e("SWAPLOG", response.message!!)
                        showDialogNoInternet(this, response.message!!, "", R.drawable.ic_alert_icon)
                    }
                }
            }


            WebConstants.LOGIN_WS -> {
                try {
                    isSocialLogin = false
                    println("login_Ws ***" + responseData);
                    loginResponse = gson.fromJson(responseData, LoginResponse::class.java);
                    isLoading = false
                    showLoadingDialog(isLoading)
                    when (loginResponse.getCode()) {
                        ResponseCodes.Success -> try {
                            //   addData(this, 0, response.getData()!!.getId()!!, response.getData()!!.getFirstName()!!, response.getData()!!.getLastName()!!, response.getData()!!.getEmail()!!, response.getData()!!.getGender()!!, response.getData()!!.getDOB()!!, response.getData()!!.getToken()!!, response.getData()!!.getIsNormalQuizComplete()!!, response.getData()!!.getIsBibleQuizComplete()!!, response.getData()!!.getIsActive()!!, "", "", "", response.getData()!!.getIsSignUpPerformed()!!, response.getData()!!.getIsBlocked()!!, response.getData()!!.getSubscriptionDone()!!, response.getData()!!.getSubscriptionFree()!!)
                            addData(this, 0, loginResponse.getData()!!.getId()!!, loginResponse.getData()!!.getFirstName()!!, loginResponse.getData()!!.getLastName()!!, loginResponse.getData()!!.getEmail()!!, loginResponse.getData()!!.getGender()!!, loginResponse.getData()!!.getDOB()!!, loginResponse.getData()!!.getToken()!!, loginResponse.getData()!!.getIsNormalQuizComplete()!!, loginResponse.getData()!!.getIsBibleQuizComplete()!!, true, "", "", "", loginResponse.getData()!!.getIsSignUpPerformed()!!, loginResponse.getData()!!.getIsBlocked()!!, loginResponse.getData()!!.getSubscriptionStatus(), loginResponse.getData()!!.getSubscriptionStatus(), loginResponse.getData()!!.getPhoneNo()!!)
                            when {
                                loginResponse.getData()!!.getIsBlocked()!! -> signUpSuccessOrFailed(this, SIGNUP_FAILED)
                                !loginResponse.getData()!!.getIsSignUpPerformed()!! || !loginResponse.getData()!!.getIsNormalQuizComplete()!! || !loginResponse.getData()!!.getIsBibleQuizComplete()!! -> gotoSignup()
                                !loginResponse.getData()!!.getSubscriptionFree()!! && !loginResponse.getData()!!.getSubscriptionDone()!! -> checkAppSubscriptionDetail()
                                !loginResponse.getData()!!.getSubscriptionFree()!! && loginResponse.getData()!!.getSubscriptionDone()!! -> checkAppSubscriptionDetail()
                                else -> scheduleAlarms(loginResponse.getData()!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            logFirebaseEvents("onSignClick", getString(R.string.failure_response))
                            showDialogNoInternet(this, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                        }
                        ResponseCodes.NoUserFound -> SwapdroidAlertDialog.Builder(this)
                                .setTitle(getString(R.string.register_yourself))
                                .isMessageVisible(true)
                                .setMessage(getString(R.string.register_yourself_message))
                                .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                                .setNegativeBtnText(getString(R.string.try_again))
                                .isNegativeVisible(true)
                                .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                                .setPositiveBtnText(getString(R.string.register))
                                .isPositiveVisible(true)
                                .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                                .setAnimation(SwapdroidAnimation.POP)
                                .isCancellable(false)
                                .showCancelIcon(true)
                                .setIcon(R.drawable.ic_error_icon, SwapdroidIcon.Visible)  //ic_star_border_black_24dp
                                .OnNegativeClicked {
                                    // nothing
                                }.OnPositiveClicked {
                                    val email = viewModel.email.get() as String
                                    addData(this, 0, "", "", "", email, 0, "", "", false, false, false, "", "", "", false, false, false, false, "")
                                    gotoSignup()
                                }
                                .build()
                        ResponseCodes.ReviewNotDone -> {
                            addData(this, 0, loginResponse.getData()!!.getId()!!, loginResponse.getData()!!.getFirstName()!!, loginResponse.getData()!!.getLastName()!!, loginResponse.getData()!!.getEmail()!!, loginResponse.getData()!!.getGender()!!, loginResponse.getData()!!.getDOB()!!, loginResponse.getData()!!.getToken()!!, loginResponse.getData()!!.getIsNormalQuizComplete()!!, loginResponse.getData()!!.getIsBibleQuizComplete()!!, loginResponse.getData()!!.getIsActive()!!, "", "", "", loginResponse.getData()!!.getIsSignUpPerformed()!!, loginResponse.getData()!!.getIsBlocked()!!, loginResponse.getData()!!.getSubscriptionStatus()!!, loginResponse.getData()!!.getSubscriptionStatus()!!, loginResponse.getData()!!.getPhoneNo()!!)
                            //  navigateToReviewUser(response.getData()!!.getReviewFor()!!)
                            to_userid = loginResponse.getData()!!.getReviewFor()!!
                            autoReviewApiCall()
                        }
                        else -> showDialogNoInternet(this, loginResponse.getMessage()!!, "", R.drawable.ic_alert_icon)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showDialogNoInternet(this, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }
            }

        }
    }

    override fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?) {
        when (label) {
            WebConstants.SOCIAL_LOGIN_WS -> {
                signOut()
                System.out.println("Login error Response ==> " + responseData?.error_message)
                showDialogNoInternet(applicationContext,
                        applicationContext.getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                mUtilLoader.stopLoader()
                logFirebaseEvents("socialLogin", "Error Message: " + responseData?.error_message!!)
            }
            WebConstants.ADD_REVIEW_WS -> {
                Log.e("SWAPLOG", responseData!!.error_message)
                navigateToReviewUser(to_userid)
            }

            WebConstants.APP_SUBSCRIPTION_DETAIL_WS -> {
                goForHome(this@CommonLoginActivity)
                mUtilLoader.stopLoader()
                logFirebaseEvents("checkAppSubscriptionDetail", "Error Message: " + responseData?.error_message!!)
            }
            WebConstants.LOGIN_WS -> {
                isLoading = false
                showLoadingDialog(isLoading)
                showDialogNoInternet(this, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
            }
        }
    }


}
