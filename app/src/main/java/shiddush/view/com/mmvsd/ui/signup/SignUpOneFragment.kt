package shiddush.view.com.mmvsd.ui.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.fragment_sign_up_one.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import shiddush.view.com.mmvsd.BuildConfig
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.FragmentSignUpOneBinding
import shiddush.view.com.mmvsd.model.User
import shiddush.view.com.mmvsd.model.signup.SignUpResponse
import shiddush.view.com.mmvsd.repository.ErrorModel
import shiddush.view.com.mmvsd.repository.ResponseCodes
import shiddush.view.com.mmvsd.repository.WebConstants
import shiddush.view.com.mmvsd.rest.RequestParams
import shiddush.view.com.mmvsd.rest.RestClientPlain
import shiddush.view.com.mmvsd.rest.RestClientReferral
import shiddush.view.com.mmvsd.rest.RestMethod
import shiddush.view.com.mmvsd.ui.createResume.CreateResumeActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.widget.loader.UtilLoader
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Sumit Kumar.
 */
class SignUpOneFragment : Fragment(), RestClientPlain.OnAsyncRequestComplete,
        RestClientPlain.OnAsyncRequestError, RestClientReferral.OnAsyncRequestComplete,
        RestClientReferral.OnAsyncRequestError, EasyPermissions.PermissionCallbacks, PickiTCallbacks {

    private lateinit var pickiT: PickiT
    lateinit var binding: FragmentSignUpOneBinding
    private lateinit var viewModel: SignUpOneViewModel
    private lateinit var selectedDate: String
    private lateinit var selectedMonth: String
    private lateinit var selectedYear: String
    private var selectedGender: Int = 0
    private lateinit var mUtilLoader: UtilLoader
    private lateinit var data: User
    private var isPassVisible: Boolean = false
    private var isConPassVisible: Boolean = false
    private var b418Year = 2000
    private var b418B450Year = 1900
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private val TAG = SignUpOneFragment::class.java.simpleName
    private var mAuth: FirebaseAuth? = null
    private var filePath = ""
    private var imagePath = ""
    private var isResumeSelected = false

    companion object {
        private val RC_SETTINGS_SCREEN_PERM = 123
        private const val RC_STORAGE_APP_PERM = 124
        private const val RC_PDF_IMAGE = 125
        private const val REQUEST_IMAGE_CAPTURE = 126
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the fragment_intro_notes for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_one, container, false)
        viewModel = SignUpOneViewModel()
        firebaseAnalytics = FirebaseAnalytics.getInstance(activity!!)
        binding.viewModel = viewModel
        val myView: View = binding.root
        mUtilLoader = UtilLoader(activity!!)
        data = AppInstance.userObj!!
        mAuth = FirebaseAuth.getInstance()

        // setTextSizes()
        onCLickListeners()
        setSpinnerData()
        pickiT = PickiT(activity!!, this, activity!!)
        return myView
    }

    /**
     * to setup all text sizes
     */
    private fun setTextSizes() {
        val size25 = getFontSize(activity!!, 25)
        val size21 = getFontSize(activity!!, 21)
        val size18 = getFontSize(activity!!, 18)
        val size13 = getFontSize(activity!!, 13)

        // binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        binding.tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvGender.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.textViewdob.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvPhone.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvPassword.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvCfPassword.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.etName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size21)
        binding.etEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size21)
        binding.etPhone.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size21)
        binding.etPassword.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size21)
        binding.etCfPassword.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size21)

        binding.signUp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)

        //set Background Image
        //  val bgImage = getBackImageSize(activity!!)
        // binding.run { firstCardCon.background = ContextCompat.getDrawable(activity!!, bgImage) }

        try {
            val size20 = getFontSize(activity!!, 20)
            val ivHeightWidth = dpToPxs(size20.toInt())
            binding.backArrow.layoutParams.height = ivHeightWidth
            binding.backArrow.layoutParams.width = ivHeightWidth
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val size24 = getFontSize(activity!!, 24)
            val size20 = getFontSize(activity!!, 20)
            val size26 = getFontSize(activity!!, 26)
            val size14 = getFontSize(activity!!, 14)
            val size2 = getFontSize(activity!!, 2)
            val etHeight = dpToPxs(size26.toInt())
            val ivHeight = dpToPxs(size14.toInt())
            val etPadding = dpToPxs(size2.toInt())
            val ivDivHeight = dpToPxs(size24.toInt())
            val ivDivWidth = dpToPxs(size20.toInt())
            //Edit texts
            binding.etName.layoutParams.height = etHeight
            binding.ivName.layoutParams.height = etHeight
            binding.ivName.layoutParams.width = etHeight
            binding.ivName.setPadding(etPadding, etPadding, etPadding, etPadding)
            binding.etEmail.layoutParams.height = etHeight
            binding.ivEmail.layoutParams.height = etHeight
            binding.ivEmail.layoutParams.width = etHeight
            binding.ivEmail.setPadding(etPadding, etPadding, etPadding, etPadding)
            binding.etPassword.layoutParams.height = etHeight
            binding.ivPass.layoutParams.height = etHeight
            binding.ivPass.layoutParams.width = etHeight
            binding.ivPass.setPadding(etPadding, etPadding, etPadding, etPadding)
            binding.etCfPassword.layoutParams.height = etHeight
            binding.ivCfPass.layoutParams.height = etHeight
            binding.ivCfPass.layoutParams.width = etHeight
            binding.ivCfPass.setPadding(etPadding, etPadding, etPadding, etPadding)
            //Spinners
            binding.spGender.layoutParams.height = etHeight
            binding.ivGen.layoutParams.height = ivHeight
            binding.ivGen.layoutParams.width = ivHeight
            binding.spDd.layoutParams.height = etHeight
            binding.ivDD.layoutParams.height = ivHeight
            binding.ivDD.layoutParams.width = ivHeight
            binding.spMm.layoutParams.height = etHeight
            binding.ivMM.layoutParams.height = ivHeight
            binding.ivMM.layoutParams.width = ivHeight
            binding.spYyyy.layoutParams.height = etHeight
            binding.ivYYYY.layoutParams.height = ivHeight
            binding.ivYYYY.layoutParams.width = ivHeight
            //Divider
            binding.ivDivDD.layoutParams.height = ivDivHeight
            binding.ivDivDD.layoutParams.width = ivDivWidth
            binding.ivDivMM.layoutParams.height = ivDivHeight
            binding.ivDivMM.layoutParams.width = ivDivWidth
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * all ckick listeners
     */
    private fun onCLickListeners() {

        binding.signUp.setOnClickListener {
            //navigateToSecond()
            onSignUpClick()
        }

        binding.llUploadPic.setOnClickListener {
            isResumeSelected = false
            requestPermissions()
        }
        binding.uploadPic.setOnClickListener {
            isResumeSelected = false
            requestPermissions()
        }

        binding.llUploadResume.setOnClickListener {
            isResumeSelected = true
            requestPermissions()
        }
        binding.uploadResume.setOnClickListener {
            isResumeSelected = true
            requestPermissions()
        }

        binding.ivGen.setOnClickListener {
            binding.spGender.performClick()
        }

        binding.backArrow.setOnClickListener {
            activity!!.onBackPressed()
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

        binding.ivCfPass.setOnClickListener {
            if (isConPassVisible) {
                isConPassVisible = false
                binding.etCfPassword.transformationMethod = PasswordTransformationMethod()
                binding.ivCfPass.setImageResource(R.drawable.ic_pass_hide)
            } else {
                isConPassVisible = true
                binding.etCfPassword.transformationMethod = null
                binding.ivCfPass.setImageResource(R.drawable.ic_pass_view)
            }
        }

        binding.etPromoCode.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //onSignUpClick()
                hideVirtualKeyboard(activity!!)
                true
            } else {
                hideVirtualKeyboard(activity!!)
                false
            }
        }

        binding.textViewdob.setOnClickListener {
            val c = Calendar.getInstance()
            c.add(Calendar.YEAR, -18)
            c.add(Calendar.DATE, -1)

            val defaultDate = c.getTime();

            SingleDateAndTimePickerDialog.Builder(context)
                    .bottomSheet()
                    .curved()
                    .displayAmPm(false)
                    .displayDaysOfMonth(true)
                    .displayDays(false)
                    .displayYears(true)
                    .displayMonth(true)
                    .displayHours(false)
                    .displayMinutes(false)
                    .title("Select date of birth")
                    .defaultDate(defaultDate)
                    .maxDateRange(defaultDate)
                    .listener {
                        binding.textViewdob.setText("" + getDateMonthYear(it.time))
                    }
                    .display()
        }
    }


    private fun getFileChooserIntentForImage(mimeType: String): Intent {
        val mimeTypes = arrayOf(mimeType)
        val intent = Intent(Intent.ACTION_GET_CONTENT)
                .setType(mimeType)
                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        return intent
    }

    private fun getFileChooserIntentForDocAndPdf(): Intent {
        val mimeTypes = arrayOf("application/pdf","application/msword")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
                .setType("application/pdf|application/msword")
                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        return intent
    }

    fun filePickerDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Choose from")
        val options = arrayOf("Camera", "Photo", "Files","Create Resume")
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
                    startActivity(Intent(activity, CreateResumeActivity::class.java))
                }
            }
        }

// create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun takePicture() {

        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        val uri: Uri = FileProvider.getUriForFile(
                activity!!,
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
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            imagePath = absolutePath
        }
    }

    //setup all spinners
    private fun setSpinnerData() {

        val ddList = ArrayList<String>()
        val mmList = ArrayList<String>()
        val yyyyList = ArrayList<String>()
        val genderList = ArrayList<String>()

        //for gender
        genderList.add(getString(R.string.male))
        genderList.add(getString(R.string.female))

        for (i in 1..31) {  //loop for Date
            var value = i.toString()
            if (value.length == 1) {
                value = "0" + value
            }
            ddList.add(value)
        }

        for (i in 1..12) {  //loop for Date
            var value = i.toString()
            if (value.length == 1) {
                value = "0" + value
            }
            mmList.add(value)
        }

        //calculate from and to year
        try {
            val today = Date()
            val yearFormat = SimpleDateFormat("yyyy")
            yearFormat.timeZone = TimeZone.getTimeZone(estTimezone)
            val currentYear = yearFormat.format(today).toInt()
            b418Year = currentYear - 18
            b418B450Year = b418Year - 100
        } catch (e: Exception) {
            e.printStackTrace()
            b418Year = 2000
            b418B450Year = 1900
        }
        for (i in b418B450Year..b418Year) {  //loop for Date
            yyyyList.add(i.toString())
        }

        //make list reversed
        yyyyList.reverse()

        // Creating adapter for Date spinner
        val ddAdapter = CustomSpinAdapter(activity!!, ddList)
        binding.spDd.adapter = ddAdapter

        // Creating adapter for Month spinner
        val mmAdapter = CustomSpinAdapter(activity!!, mmList)
        binding.spMm.adapter = mmAdapter

        // Creating adapter for Year spinner
        val yyyyAdapter = CustomSpinAdapter(activity!!, yyyyList)
        binding.spYyyy.adapter = yyyyAdapter

        // Creating adapter for Gender spinner
        val genderAdapter = CustomGenderSpinnerAdapter(activity!!, genderList)
        binding.spGender.adapter = genderAdapter

        // Date Spinner click listener
        binding.spDd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // On selecting a spinner item
                try {
                    selectedDate = parent.getItemAtPosition(position).toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                //nothing to do
            }
        }

        // Month Spinner click listener
        binding.spMm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // On selecting a spinner item
                try {
                    selectedMonth = parent.getItemAtPosition(position).toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                //nothing to do
            }
        }

        // Year Spinner click listener
        binding.spYyyy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // On selecting a spinner item
                try {
                    selectedYear = parent.getItemAtPosition(position).toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                //nothing to do
            }
        }

        // Gender Spinner click listener
        binding.spGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var selectedPostion = 0;
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // On selecting a spinner item
                try {
                    if (position == 0) {
                        return;
                    } else {
                        selectedPostion = position - 1;
                    }
                    val selGen = parent.getItemAtPosition(selectedPostion).toString()
                    if (selGen.equals(getString(R.string.male))) {
                        selectedGender = 0
                    } else {
                        selectedGender = 1
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                //nothing to do
            }
        }

        //Set all Data
        try {
            if (data != null) {
                if (data.getLastName()!!.trim().isEmpty()) {
                    binding.viewModel!!.setNameCommunicator(data.getFirstName()!!)
                } else {
                    binding.viewModel!!.setNameCommunicator(data.getFirstName()!! + " " + data.getLastName()!!)
                }
                if (data.getGender() == 1) {
                    binding.viewModel!!.setGenderCommunicator(getString(R.string.female))
                } else {
                    binding.viewModel!!.setGenderCommunicator(getString(R.string.male))
                }
                binding.viewModel!!.setEmailCommunicator(data.getEmail()!!)
                binding.viewModel!!.setPhoneCommunicator(data.getPhone()!!)
                binding.viewModel!!.setDOBCommunicator(data.getDOB()!!)

                if (!data.getDOB()!!.isEmpty()) {
                    val splitDate = data.getDOB()!!.split("/")
                    selectedDate = splitDate[0]
                    selectedMonth = splitDate[1]
                    selectedYear = splitDate[2]

                    for (i in 0 until ddList.size) {
                        if (ddList.get(i).equals(selectedDate)) {
                            binding.spDd.setSelection(i)
                            break
                        }
                    }

                    for (i in 0 until mmList.size) {
                        if (mmList.get(i).equals(selectedMonth)) {
                            binding.spMm.setSelection(i)
                            break
                        }
                    }

                    for (i in 0 until yyyyList.size) {
                        if (yyyyList.get(i).equals(selectedYear)) {
                            binding.spYyyy.setSelection(i)
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * show and hide loader
     */
    private fun showLoadingDialog(show: Boolean) {
        if (show) mUtilLoader.startLoader(activity!!) else mUtilLoader.stopLoader()
    }

    /**
     * sign up click
     */
    private fun onSignUpClick() {
        hideVirtualKeyboard(activity!!)
        if (checkValidation()) {
            SignUpWSCall()
        }
    }

    //check validation
    private fun checkValidation(): Boolean {
        var isValid = false

        val name = viewModel.name?.get() as String
        val email = viewModel.email?.get() as String
        val password = viewModel.password?.get() as String
        val conPass = viewModel.confirmPassword?.get() as String
        val phone = viewModel.phone?.get() as String
        // val dob = "" + selectedDate + "/" + selectedMonth + "/" + selectedYear
        val dob = binding.textViewdob.text.toString()
        val defDob = "01/01/" + b418Year

        binding.etName.error = null
        binding.etEmail.error = null
        binding.etPhone.error = null
        binding.etPassword.error = null
        binding.etCfPassword.error = null

        if (name.trim().isEmpty()) {
            binding.etName.error = getString(R.string.please_enter_name)
            binding.etName.requestFocus()
            isValid = false
        } else if (!isValidName(name.trim())) {
            binding.etName.error = getString(R.string.please_enter_valid_name)
            binding.etName.requestFocus()
            isValid = false
        } else if (sp_gender.selectedItemPosition == 0) {
            showDialogNoInternet(activity!!, getString(R.string.please_select_gender), "", R.drawable.ic_alert_icon)
            isValid = false
        } else if (textViewdob.text.toString().isEmpty()) {
            showDialogNoInternet(activity!!, getString(R.string.please_select_dob), "", R.drawable.ic_alert_icon)
            isValid = false
        } else if (phone.trim().isEmpty()) {
            binding.etPhone.error = getString(R.string.please_enter_phone)
            binding.etPhone.requestFocus()
            isValid = false
        } else if (email.trim().isEmpty()) {
            binding.etEmail.error = getString(R.string.please_enter_email)
            binding.etEmail.requestFocus()
            isValid = false
        } else if (!isValidEmail(email.trim())) {
            binding.etEmail.error = getString(R.string.please_enter_valid_email)
            binding.etEmail.requestFocus()
            isValid = false
        } else if (password.trim().isEmpty()) {
            binding.etPassword.error = getString(R.string.please_enter_password)
            binding.etPassword.requestFocus()
            isValid = false
        } else if (password.trim().length < 6) {
            binding.etPassword.error = getString(R.string.password_length_error)
            binding.etPassword.requestFocus()
            isValid = false
        } else if (conPass.trim().isEmpty()) {
            binding.etCfPassword.error = getString(R.string.please_enter_confirm_password)
            binding.etCfPassword.requestFocus()
            isValid = false
        } else if (!password.trim().equals(conPass.trim())) {
            binding.etCfPassword.error = getString(R.string.confirm_password_not_match)
            binding.etCfPassword.requestFocus()
            isValid = false
        } else if (dob.equals(defDob)) {
            showDialogNoInternet(activity!!, getString(R.string.select_valid_dob), "", R.drawable.ic_alert_icon)
            isValid = false
        } else {
            isValid = true
            binding.etName.error = null
            binding.etEmail.error = null
            binding.etPhone.error = null
            binding.etPassword.error = null
            binding.etCfPassword.error = null
        }
        return isValid
    }


    /**
     * get location list using API call
     */
    private fun SignUpWSCall() {
        //try {
        if (isNetworkAvailable(activity!!)) {
            Log.d("selectedDOB", binding.textViewdob.text.toString())
            showLoadingDialog(true)
            val p = RequestParams();
            p.add("firstName", viewModel.name?.get() as String)
            p.add("lastName", "")
            p.add("email", viewModel.email?.get() as String)
            p.add("phone_no", viewModel.phone?.get() as String)
            p.add("password", viewModel.password?.get() as String)
            p.add("gender", selectedGender)
            p.add("DOB", binding.textViewdob.text.toString())
            p.add("loginType", AppInstance.userObj!!.getLoginType()!!)
            p.add("facebookSocialId", AppInstance.userObj!!.getFacebookSocialId()!!)
            p.add("googleSocialId", AppInstance.userObj!!.getGoogleSocialId()!!)
            p.add("device_type", WebConstants.DEVICE_TYPE)
            p.add("device_token", getPrefString(activity!!, PreferenceConnector.DEVICE_TOKEN))
            p.add("appversion", BuildConfig.VERSION_NAME!!)
            p.add("deviceInfo", getDeviceInfo())
            p.add("lat", "" + getPrefString(activity!!, PreferenceConnector.LAT))
            p.add("lng", "" + getPrefString(activity!!, PreferenceConnector.LNG))
            p.add("city", "" + getPrefString(activity!!, PreferenceConnector.CITY))
            p.add("country", "" + getPrefString(activity!!, PreferenceConnector.COUNTRY))
            p.add("countryCode", "" + getPrefString(activity!!, PreferenceConnector.COUNTRYCODE))

            val rest = RestClientPlain(this!!, RestMethod.POST, p)
            rest.execute(WebConstants.SIGNUP_WS)
        }
    }

    /**
     * navigate to second fragment to fill super important questions
     */
    private fun navigateToSecond() {
        // Get the text fragment instance
        val textFragment = SignUpTwoFragment()

        // Get the support fragment manager instance
        val manager = fragmentManager

        // Begin the fragment transition using support fragment manager
        val transaction = manager!!.beginTransaction()

        // Replace the fragment on container
        transaction.replace(R.id.signup_container, textFragment, "TWO_FRAGMENT")
        transaction.addToBackStack(null)

        // Finishing the transition
        transaction.commit()
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
            WebConstants.SIGNUP_WS -> {
                try {
                    val response = gson.fromJson(responseData, SignUpResponse::class.java);
                    showLoadingDialog(false)
                    if (response.getCode() == ResponseCodes.Success) {
                        //Add App instance  AppInstance.userObj!!.getLoginType()!!
                        addData(activity!!, AppInstance.userObj!!.getLoginType()!!, response.getData()!!.getId()!!, response.getData()!!.getFirstName()!!,
                                response.getData()!!.getLastName()!!, response.getData()!!.getEmail()!!,
                                response.getData()!!.getGender()!!, response.getData()!!.getDOB()!!,
                                response.getData()!!.getToken()!!, response.getData()!!.getIsNormalQuizComplete()!!,
                                response.getData()!!.getIsBibleQuizComplete()!!, response.getData()!!.getIsActive()!!,
                                response.getData()!!.getFacebookSocialId()!!, response.getData()!!.getGoogleSocialId()!!,
                                viewModel.password.get() as String, response.getData()!!.getIsSignUpPerformed()!!,
                                response.getData()!!.getIsBlocked()!!, response.getData()!!.getSubscriptionStatus()!!,
                                response.getData()!!.getSubscriptionStatus()!!, response.getData()!!.getPhone()!!)
                        //addData(activity!!, AppInstance.userObj!!.getLoginType()!!, response.getData()!!.getId()!!, response.getData()!!.getFirstName()!!, response.getData()!!.getLastName()!!, response.getData()!!.getEmail()!!, response.getData()!!.getGender()!!, response.getData()!!.getDOB()!!, response.getData()!!.getToken()!!, response.getData()!!.getIsNormalQuizComplete()!!, response.getData()!!.getIsBibleQuizComplete()!!, response.getData()!!.getIsActive()!!, response.getData()!!.getFacebookSocialId()!!, response.getData()!!.getGoogleSocialId()!!, request.password!!, response.getData()!!.getIsSignUpPerformed()!!, response.getData()!!.getIsBlocked()!!, true, true)
                        //navigateToSecond()
                        checkIfReferral()
                    } else {
                        showDialogNoInternet(activity!!, response.getMessage()!!, "", R.drawable.ic_alert_icon)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showLoadingDialog(false)
                    showDialogNoInternet(activity!!, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }

            }
            WebConstants.REFFERAL_WS -> {
                try {
                    Log.e("REFFERAL_WS", responseData!! + " ")
                    showLoadingDialog(false)
                    checkForSelectedFile()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showLoadingDialog(false)
                    showDialogNoInternet(activity!!, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }

            }
        }
    }

    private fun checkForSelectedFile() {
        if (filePath.isNotEmpty())
            uploadFileOnFBStorage()
        else if (imagePath.isNotEmpty())
            uploadImageOnFBStorage()
        else
            navigateToSecond()
    }

    private fun checkIfReferral() {
        if ((getReferralData(activity!!) != null && getReferralData(activity!!)?.id != null) ||
                binding.etPromoCode.text.toString().isNotEmpty())
            callReferralAPI()
        else if (filePath.isNotEmpty())
            uploadFileOnFBStorage()
        else if (imagePath.isNotEmpty())
            uploadImageOnFBStorage()
        else
            navigateToSecond()
    }

    override fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?) {
        when (label) {
            WebConstants.SIGNUP_WS -> {
                showLoadingDialog(false)
                showDialogNoInternet(activity!!, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)

            }
            WebConstants.REFFERAL_WS -> {
                Log.e("REFFERAL_WS", responseData?.error_message!! + "")
                showLoadingDialog(false)
                checkForSelectedFile()

            }
        }
    }

    private fun callReferralAPI() {
        var referringUserId = ""
        var channel = ""
        val promoCode = binding.etPromoCode.text.toString()
        if (getReferralData(activity!!) != null && getReferralData(activity!!)?.referringUserId != null)
            referringUserId = getReferralData(activity!!)?.referringUserId!!

        if (getReferralData(activity!!) != null && getReferralData(activity!!)?.channel != null)
            channel = getReferralData(activity!!)?.channel!!

        if (isNetworkAvailable(activity!!)) {
            showLoadingDialog(true)
            val p = RequestParams();
            p.add("referred_user_id", AppInstance.userObj?.getId())
            p.add("referring_user_id", referringUserId)
            p.add("email", viewModel.email?.get() as String)
            p.add("name", viewModel.name?.get() as String)
            p.add("phone", viewModel.phone?.get() as String)
            p.add("channel", channel)
            p.add("user_api_key", REFERRAL_KEY)
            p.add("promoCode", promoCode)
            val rest = RestClientReferral(this!!, RestMethod.POST, p)
            rest.execute(WebConstants.REFFERAL_WS)

            Log.d("Rerfferlavalue", referringUserId + "  " + channel)
        }
    }


    @AfterPermissionGranted(RC_STORAGE_APP_PERM)
    private fun requestPermissions() {
        try {
            val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (EasyPermissions.hasPermissions(activity!!, *perms)) {
                filePickerDialog()
            } else {
                EasyPermissions.requestPermissions(this,
                        getString(R.string.rationale_storage_app), RC_STORAGE_APP_PERM, *perms)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_STORAGE_APP_PERM)
                    .build()
                    .show()
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.e(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
        //startActivity(getFileChooserIntentForImageAndPdf())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode != Activity.RESULT_OK)
                imagePath = ""
            else {
                Picasso.get()
                        .load(File(imagePath))
                        .placeholder(R.drawable.ic_upload_white)
                        .error(R.drawable.ic_upload_white)
                        .into(binding.imageViewPic)
            }
        } else {
            if (data?.data != null)
                pickiT.getPath(data.getData(), Build.VERSION.SDK_INT)
        }
    }

    override fun PickiTonUriReturned() {
    }

    override fun PickiTonProgressUpdate(progress: Int) {

    }

    override fun PickiTonStartListener() {
        showLoadingDialog(true)
    }

    override fun PickiTonCompleteListener(path: String?, wasDriveFile: Boolean, wasUnknownProvider: Boolean, wasSuccessful: Boolean, Reason: String?) {
        showLoadingDialog(false)
        if (path != null && path.isNotEmpty()) {
            if (isResumeSelected) {
                filePath = path
                binding.imageViewResume.setImageResource(R.drawable.ic_document)
            } else {
                imagePath = path
                Picasso.get()
                        .load(File(path))
                        .placeholder(R.drawable.ic_upload_white)
                        .error(R.drawable.ic_upload_white)
                        .into(binding.imageViewPic)
            }
        }
        Log.d("SelectedImageUriL", path.toString() + "  " + "   " + wasSuccessful)
    }


    private fun uploadFileOnFBStorage() {
        showLoadingDialog(true)
        val email = viewModel.email.get() as String
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val file = Uri.fromFile(File(filePath))
        val riversRef = storageRef.child("resume/" + email + "/${"resume_"+email + "_" + System.currentTimeMillis() + "." + File(filePath).extension}")
        val uploadTask = riversRef.putFile(file)
        uploadTask.addOnFailureListener {
            Log.w(TAG, "uploadFile:failure" + it.message.toString()!!)
            showLoadingDialog(false)
            if (imagePath.isNotEmpty())
                uploadImageOnFBStorage()
            else
                navigateToSecond()
        }.addOnSuccessListener {
            Log.w(TAG, "uploadFile:success")
            showLoadingDialog(false)
            if (imagePath.isNotEmpty())
                uploadImageOnFBStorage()
            else
                navigateToSecond()
        }
    }

    private fun uploadImageOnFBStorage() {
        showLoadingDialog(true)
        val email = viewModel.email.get() as String
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val file = Uri.fromFile(File(imagePath))
        val riversRef = storageRef.child("resume/" + email + "/${"pic_"+email + "_" + System.currentTimeMillis() + "." + File(imagePath).extension}")
        val uploadTask = riversRef.putFile(file)
        uploadTask.addOnFailureListener {
            Log.w(TAG, "uploadFile:failure" + it.message.toString()!!)
            showLoadingDialog(false)
            navigateToSecond()
        }.addOnSuccessListener {
            Log.w(TAG, "uploadFile:success")
            showLoadingDialog(false)
            navigateToSecond()
        }
    }

    public val File.extension: String
        get() = name.substringAfterLast('.', "")
}