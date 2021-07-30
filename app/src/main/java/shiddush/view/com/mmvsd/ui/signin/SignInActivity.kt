package shiddush.view.com.mmvsd.ui.signin

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.TypedValue
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

import shiddush.view.com.mmvsd.BuildConfig
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivitySignInBinding
import shiddush.view.com.mmvsd.model.login.LoginData
import shiddush.view.com.mmvsd.model.login.LoginResponse
import shiddush.view.com.mmvsd.model.subscription.CheckSubscriptionResponse
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.rest.RequestParams
import shiddush.view.com.mmvsd.rest.RestClient
import shiddush.view.com.mmvsd.rest.RestMethod
import shiddush.view.com.mmvsd.ui.commonloginscreen.CommonLoginActivity
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.ui.recievers.AlarmReciever
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
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Sumit Kumar.
 */
class SignInActivity : AppCompatActivity(), MultiplePermissionsListener, RestClient.OnAsyncRequestComplete,
        RestClient.OnAsyncRequestError {

    private var firebaseAnalytics: FirebaseAnalytics? = null
    lateinit var binding: ActivitySignInBinding
    private lateinit var viewModel: SignInViewModel
    private lateinit var mUtilLoader: UtilLoader
    private var isLoading: Boolean = false
    private var isPassVisible: Boolean = false
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    protected var mLastLocation: Location? = null
    private val TAG = SignInActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        viewModel = SignInViewModel()
        binding.viewModel = viewModel
        mUtilLoader = UtilLoader(this)
        changeNavBack()
        setTextSizes()
        setListeners()

        /*mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermissions()*/
    }


/*
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

                Log.d("currentlocation", "getAddress:  city" + city)
                Log.d("currentlocation", "getAddress:  country" + country)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
*/

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
        val size21 = getFontSize(this, 21)
        val size18 = getFontSize(this, 18)
        val size15 = getFontSize(this, 15)
        val size13 = getFontSize(this, 13)

        binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        binding.tvEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvPassword.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.etEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size21)
        binding.etPassword.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size21)
        binding.forgotPassword.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size15)

        binding.signIn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)

        binding.tvDontHaveAccount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvSignUp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        //set Background Image
        val bgImage = getBackImageSize(this)
        binding.clMainContainer.background = ContextCompat.getDrawable(this, bgImage)

        try {
            val size20 = getFontSize(this, 20)
            val ivHeightWidth = dpToPxs(size20.toInt())
            binding.backArrow.layoutParams.height = ivHeightWidth
            binding.backArrow.layoutParams.width = ivHeightWidth
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val size26 = getFontSize(this, 26)
            val size2 = getFontSize(this, 2)
            val etHeight = dpToPxs(size26.toInt())
            val etPadding = dpToPxs(size2.toInt())
            //Edit texts
            binding.etEmail.layoutParams.height = etHeight
            binding.ivEmail.layoutParams.height = etHeight
            binding.ivEmail.layoutParams.width = etHeight
            binding.ivEmail.setPadding(etPadding, etPadding, etPadding, etPadding)
            binding.etPassword.layoutParams.height = etHeight
            binding.ivPass.layoutParams.height = etHeight
            binding.ivPass.layoutParams.width = etHeight
            binding.ivPass.setPadding(etPadding, etPadding, etPadding, etPadding)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
            //getLastLocation()
        }
    }

    /**
     * to set up all click listeners
     */

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

        binding.backView.setOnClickListener {
            binding.backView.isEnabled = false
            goBack()
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

    /**
     *  check app subscription details
     */
    fun checkAppSubscriptionDetail() {
        try {
            if (isNetworkAvailable(this)) {
                isLoading = true
                showLoadingDialog(isLoading)
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
                showDialogNoInternet(this, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            goToHome()
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

    //on back press by user
    override fun onBackPressed() {
        goBack()
    }

    /**
     * navigate to commonlogin
     */
    private fun goBack() {
        val intent = Intent(this, CommonLoginActivity::class.java)
        startActivity(intent)
        this@SignInActivity.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
    }

    /**
     * navigate to waiting screen
     */
    private fun goToHome() {
        val intent = Intent(this, WaitingActivity::class.java)
        startActivity(intent)
        this@SignInActivity.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * navigate to sign up
     */
    private fun gotoSignup() {
        val intent = Intent(this, TermsAndConActivity::class.java)
        intent.putExtra(SOURCE, LOGIN)
        intent.putExtra(VIA, NORMAL)
        startActivity(intent)
        this@SignInActivity.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * navigate to SubscriptionActivity
     */
    private fun gotoSubscriptionActivity() {
        val intent = Intent(this, SubscriptionActivity::class.java)
        intent.putExtra(SOURCE, LOGIN)
        intent.putExtra(VIA, NORMAL)
        startActivity(intent)
        this@SignInActivity.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * navigate to other screens
     */
    private fun signUpSuccessOrFailed(tag: String) {
        val intent = Intent(this, OtherScreensActivity::class.java)
        intent.putExtra(SOURCE, tag)
        startActivity(intent)
        this@SignInActivity.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     *  navigation to Review User
     */
    private fun navigateToReviewUser(to_userid: String) {
        val intent = Intent(this, ReviewActivity::class.java)
        intent.putExtra(TOID, to_userid)
        startActivity(intent)
        this@SignInActivity.finish()
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


    fun scheduleAlarms() {

        val localCalendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
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
        } else if (currentDayOfWeek == 6) {
            setTimings(1, 2)
            setTimings(2, 3)
            setTimings(3, 4)
            setTimings(4, 5)
            setTimings(5, 6)
        } else if (currentDayOfWeek == 7) {
            setTimings(1, 1)
            setTimings(2, 2)
            setTimings(3, 3)
            setTimings(4, 4)
            setTimings(5, 5)
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
        Log.e("hour==", "====== " + hour)
        calendar.timeZone = TimeZone.getTimeZone("America/New_York")
        calendar.add(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 28)
        calendar.set(Calendar.SECOND, 0)

        val istDf: SimpleDateFormat = SimpleDateFormat("MM dd yyyy HH mm");
        istDf.setTimeZone(TimeZone.getDefault())
        val newDate: String = istDf.format(calendar.getTimeInMillis());
        System.out.println("india  " + istDf.format(calendar.getTimeInMillis()));

        val calendar2 = Calendar.getInstance()

        val dates = newDate.split(" ")
        calendar2.timeZone = TimeZone.getDefault()
        calendar2.add(Calendar.DAY_OF_MONTH, dates[1].toInt())
        calendar2.set(Calendar.HOUR_OF_DAY, dates[3].toInt())
        calendar2.set(Calendar.MINUTE, dates[4].toInt())
        calendar2.set(Calendar.SECOND, 0)

        Log.e("hour==", "after ====== " + getDateFromTimestamp(calendar2.timeInMillis) + "    " + calendar2.get(Calendar.HOUR_OF_DAY) + " " + calendar2.get(Calendar.MINUTE))
        val timestamp: Long = calendar2.timeInMillis
        setAlarm(reqCode, timestamp)
    }


    fun getDateFromTimestamp(milliSeconds: Long): String {
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

    fun logFirebaseEvents(key: String, value: String?) {
        val params = Bundle()
        params.putString(key, value)
        firebaseAnalytics?.logEvent(TAG, params)
    }

    override fun asyncResponse(responseData: String?, label: String?, `object`: Any?) {
        val gson = Gson();
        println(responseData);
        when (label) {
            WebConstants.LOGIN_WS -> {
                try {
                    val response = gson.fromJson(responseData, LoginResponse::class.java);
                    isLoading = false
                    showLoadingDialog(isLoading)
                    when (response.getCode()) {
                        ResponseCodes.Success -> try {
                            //   addData(this@SignInActivity, 0, response.getData()!!.getId()!!, response.getData()!!.getFirstName()!!, response.getData()!!.getLastName()!!, response.getData()!!.getEmail()!!, response.getData()!!.getGender()!!, response.getData()!!.getDOB()!!, response.getData()!!.getToken()!!, response.getData()!!.getIsNormalQuizComplete()!!, response.getData()!!.getIsBibleQuizComplete()!!, response.getData()!!.getIsActive()!!, "", "", "", response.getData()!!.getIsSignUpPerformed()!!, response.getData()!!.getIsBlocked()!!, response.getData()!!.getSubscriptionDone()!!, response.getData()!!.getSubscriptionFree()!!)
                            addData(this@SignInActivity, 0, response.getData()!!.getId()!!, response.getData()!!.getFirstName()!!, response.getData()!!.getLastName()!!, response.getData()!!.getEmail()!!, response.getData()!!.getGender()!!, response.getData()!!.getDOB()!!, response.getData()!!.getToken()!!, response.getData()!!.getIsNormalQuizComplete()!!, response.getData()!!.getIsBibleQuizComplete()!!, true, "", "", "", response.getData()!!.getIsSignUpPerformed()!!, response.getData()!!.getIsBlocked()!!, true, true,response.getData()!!.getPhoneNo()!!)
                            //saveUserObject(this, AppInstance.userObj!!)
                            when {
                                response.getData()!!.getIsBlocked()!! -> signUpSuccessOrFailed(SIGNUP_FAILED)
                                !response.getData()!!.getIsSignUpPerformed()!! || !response.getData()!!.getIsNormalQuizComplete()!! || !response.getData()!!.getIsBibleQuizComplete()!! -> gotoSignup()
                                !response.getData()!!.getSubscriptionFree()!! && !response.getData()!!.getSubscriptionDone()!! -> gotoSubscriptionActivity()
                                !response.getData()!!.getSubscriptionFree()!! && response.getData()!!.getSubscriptionDone()!! -> checkAppSubscriptionDetail()
                                else -> scheduleAlarms(response.getData()!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            logFirebaseEvents("onSignClick", getString(R.string.failure_response))
                            showDialogNoInternet(this@SignInActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                        }
                        ResponseCodes.NoUserFound -> SwapdroidAlertDialog.Builder(this@SignInActivity)
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
                                    addData(this@SignInActivity, 0, "", "", "", email, 0, "", "", false, false, false, "", "", "", false, false, false, false,"")
                                    gotoSignup()
                                }
                                .build()
                        ResponseCodes.ReviewNotDone -> {
                            addData(this@SignInActivity, 0, response.getData()!!.getId()!!, response.getData()!!.getFirstName()!!, response.getData()!!.getLastName()!!, response.getData()!!.getEmail()!!, response.getData()!!.getGender()!!, response.getData()!!.getDOB()!!, response.getData()!!.getToken()!!, response.getData()!!.getIsNormalQuizComplete()!!, response.getData()!!.getIsBibleQuizComplete()!!, response.getData()!!.getIsActive()!!, "", "", "", response.getData()!!.getIsSignUpPerformed()!!, response.getData()!!.getIsBlocked()!!, response.getData()!!.getSubscriptionDone()!!, response.getData()!!.getSubscriptionFree()!!,response.getData()!!.getPhoneNo()!!)
                            navigateToReviewUser(response.getData()!!.getReviewFor()!!)
                        }
                        else -> showDialogNoInternet(this@SignInActivity, response.getMessage()!!, "", R.drawable.ic_alert_icon)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showDialogNoInternet(this@SignInActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }
            }
            WebConstants.APP_SUBSCRIPTION_DETAIL_WS->{
                val response = gson.fromJson(responseData, CheckSubscriptionResponse::class.java);
                when (response.code) {
                    ResponseCodes.Success -> //success
                    {
                        if (response.data.payload.cancelReason == -1 && response.data.payload.paymentState == 2) { // not cancel and free
                            goToHome()
                        } else if (response.data.payload.cancelReason != -1 && response.data.payload.paymentState == 2) { // cancel and free
                            goToHome()
                        } else if (response.data.payload.cancelReason != -1) { // cancel and not free
                            showSubscriptionCancelDialog(this@SignInActivity, getString(R.string.renew_cancellation))
                        } else { //not cancelled
                            when (response.data.payload.paymentState) {
                                0 -> showSubscriptionCancelDialog(this@SignInActivity, getString(R.string.payment_pending))
                                1, 2 -> goToHome()
                                3 -> showSubscriptionCancelDialog(this@SignInActivity, getString(R.string.price_changed))
                            }
                        }
                    }
                    else -> {
                        Log.e("SWAPLOG", response.message)
                        goToHome()
                    }
                }

            }
        }
    }

    override fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?) {
        when (label) {
            WebConstants.LOGIN_WS -> {
                isLoading = false
                showLoadingDialog(isLoading)
                showDialogNoInternet(this@SignInActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
            }
            WebConstants.APP_SUBSCRIPTION_DETAIL_WS -> {
                isLoading = false
                showLoadingDialog(isLoading)
                goToHome()
            }
        }
    }
}