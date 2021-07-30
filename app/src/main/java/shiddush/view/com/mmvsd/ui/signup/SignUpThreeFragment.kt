package shiddush.view.com.mmvsd.ui.signup


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson

import kotlinx.android.synthetic.main.terms_and_conditions.view.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.FragmentSignUpThreeBinding
import shiddush.view.com.mmvsd.model.signup.SignUpResponse
import shiddush.view.com.mmvsd.model.signupbiblequiz.SignUpBibleQuizData
import shiddush.view.com.mmvsd.model.signupbiblequiz.SignUpBibleQuizPostRequest
import shiddush.view.com.mmvsd.model.signupbiblequiz.SignUpBibleQuizResponse
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.rest.RequestParams
import shiddush.view.com.mmvsd.rest.RestClient
import shiddush.view.com.mmvsd.rest.RestMethod
import shiddush.view.com.mmvsd.ui.commonloginscreen.CommonLoginActivity
import shiddush.view.com.mmvsd.ui.otherscreens.OtherScreensActivity
import shiddush.view.com.mmvsd.ui.welcome.WelcomeActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.widget.loader.UtilLoader
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidColorConstants
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon
import java.util.*

/**
 * Created by Sumit Kumar.
 */
class SignUpThreeFragment : Fragment(), RestClient.OnAsyncRequestComplete,
        RestClient.OnAsyncRequestError {

    lateinit var binding: FragmentSignUpThreeBinding
    private lateinit var viewModel: SignUpThreeViewModel
    private lateinit var mUtilLoader: UtilLoader
    private lateinit var signUpThreeQuizAdapter: SignUpThreeQuizAdapter
    private var quizListData = ArrayList<SignUpBibleQuizData>()

    private var isLoading: Boolean = false
    private var termsVisible: Boolean = false
    private var isAgree: Boolean = true //false
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private val TAG = SignUpThreeFragment::class.java.simpleName


    object Data {
        var selectedAnswers = SignUpBibleQuizPostRequest()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the fragment_intro_notes for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_three, container, false)
        val myView: View = binding.root
        viewModel = SignUpThreeViewModel()
        firebaseAnalytics = FirebaseAnalytics.getInstance(activity!!)
        binding.viewModel = viewModel
        mUtilLoader = UtilLoader(activity!!)
        setTextSizes()
        setLayout()
        onCLickListeners()
        getBibleQuizList()
        return myView
    }

    /**
     * to setup all text sizes
     */
    private fun setTextSizes() {
        val size30 = getFontSize(activity!!, 30)
        val size25 = getFontSize(activity!!, 25)
        val size18 = getFontSize(activity!!, 18)
        val size13 = getFontSize(activity!!, 13)

        binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        binding.tvAgree.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvTermsAndCond.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.btnSubmit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)

        //terms and conditions
        binding.llTermsCond.tv_hebru_text_t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
        binding.llTermsCond.tv_hebru_quotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
        binding.llTermsCond.tv_hebru_text_dt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)

        binding.llTermsCond.tvTNCtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.llTermsCond.tvTNCMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.llTermsCond.visibility = View.GONE
        termsVisible = false

        try {
            val size20 = getFontSize(activity!!, 20)
            val ivHeightWidth = dpToPxs(size20.toInt())
            binding.backArrow.layoutParams.height = ivHeightWidth
            binding.backArrow.layoutParams.width = ivHeightWidth
            binding.llTermsCond.imageViewCancel.layoutParams.height = ivHeightWidth
            binding.llTermsCond.imageViewCancel.layoutParams.width = ivHeightWidth
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * set list fragment_intro_notes
     */
    private fun setLayout() {
        binding.termscondLayout.visibility = View.GONE
        val mLayoutManager = LinearLayoutManager(context)
        binding.quizList.layoutManager = mLayoutManager
    }

    /**
     * all click listeners
     */
    private fun onCLickListeners() {

        binding.backView.setOnClickListener {
            if (!termsVisible) {
                activity!!.onBackPressed()
            }
        }

        binding.ivCheck.setOnClickListener {
            if (isAgree) {
                isAgree = false
                binding.ivCheck.setImageResource(R.drawable.ic_check_box)
            } else {
                isAgree = true
                binding.ivCheck.setImageResource(R.drawable.ic_check_box_selected)
            }
        }

        //click on submit button
        binding.btnSubmit.setOnClickListener {
            try {
                if (!termsVisible) {
                    if (isNetworkAvailable(activity!!)) {
                        var isValid: Boolean = true
                        for (i in 0 until Data.selectedAnswers.answer!!.size) {
                            if (Data.selectedAnswers.answer!![i].answerText!!.trim().isEmpty()) {
                                isValid = false
                            }
                        }
                        if (isValid) {
                            if (isAgree) {
                                //webservice call
                                submitAnswers()
                            } else {
                                //binding.nsView.fullScroll(View.FOCUS_DOWN)
                                showDialogNoInternet(activity!!, getString(R.string.please_agree_terms_and_conditions), "", R.drawable.ic_alert_icon)
                            }
                        } else {
                            showDialogNoInternet(activity!!, getString(R.string.provide_all_answers), "", R.drawable.ic_alert_icon)
                        }
                    } else {
                        showDialogNoInternet(activity!!, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showDialogNoInternet(activity!!, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
            }
        }

        binding.tvTermsAndCond.setOnClickListener {
            if (!termsVisible) {
                termsAndCondition()
            }
        }

    }

    /**
     * add data to list
     */
    fun addData() {
        try {
            Data.selectedAnswers.userId = AppInstance.userObj!!.getId()!!
            Data.selectedAnswers.termsAndConditions = true
            val answers = ArrayList<SignUpBibleQuizPostRequest.Answer>()
            for (i in 0 until quizListData.size) {
                val ans = SignUpBibleQuizPostRequest.Answer()
                ans.qId = quizListData.get(i).getId()
                ans.answerText = ""
                answers.add(ans)
            }
            Data.selectedAnswers.answer!!.addAll(answers)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //get quiz questions api call
    fun getBibleQuizList() {
        try {
            if (isNetworkAvailable(activity!!)) {
                if (!isLoading) {
                    isLoading = true
                    showLoadingDialog(isLoading)
                    val token = AppInstance.userObj!!.getToken()!!

                    val p = RequestParams();
                    p.add("userid", AppInstance.userObj!!.getId()!!)
                    val rest = RestClient(this!!, RestMethod.POST, p)
                    rest.setToken(token);
                    rest.execute(WebConstants.GET_BIBLE_QUESTIONS_WS);
                }
            } else {
                showNoInternetErrorDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("getBibleQuizList", "Error Message: " + e.message)

        }
    }

    //submitting answers
    private fun submitAnswers() {
        try {
            val request = Data.selectedAnswers
            if (isNetworkAvailable(activity!!)) {
                if (!isLoading) {
                    isLoading = true
                    showLoadingDialog(isLoading)

                    val manager = NetworkManager()
                    val token = AppInstance.userObj!!.getToken()!!

                    manager.createApiRequest(ApiUtilities.getAPIService().postBibleQuestions(token, request), object : ServiceListener<SignUpResponse> {
                        override fun getServerResponse(response: SignUpResponse, requestcode: Int) {
                            try {
                                isLoading = false
                                showLoadingDialog(isLoading)
                                if (response.getCode() == 200) {
                                    //addData(activity!!, AppInstance.userObj!!.getLoginType()!!, response.getData()!!.getId()!!, response.getData()!!.getFirstName()!!, response.getData()!!.getLastName()!!, response.getData()!!.getEmail()!!, response.getData()!!.getGender()!!, response.getData()!!.getDOB()!!, response.getData()!!.getToken()!!, response.getData()!!.getIsNormalQuizComplete()!!, response.getData()!!.getIsBibleQuizComplete()!!, response.getData()!!.getIsActive()!!, response.getData()!!.getFacebookSocialId()!!, response.getData()!!.getGoogleSocialId()!!, AppInstance.userObj!!.getPassword()!!, response.getData()!!.getIsSignUpPerformed()!!, response.getData()!!.getIsBlocked()!!, response.getData()!!.getSubscriptionStatus()!!, response.getData()!!.getSubscriptionStatus()!!, response.getData()!!.getPhone()!!)
                                    savePrefBoolean(activity!!, PreferenceConnector.IS_LOCATION_ALARM_ENABLED, response.getData()!!.getLocationAlarmEnabled())
                                    savePrefBoolean(activity!!, PreferenceConnector.IS_GENDER_ALARM_ENABLED, response.getData()!!.getGenderAlarmEnabled())

                                    if (response.getData()!!.getIsBlocked()!!) {
                                        signUpFailed(SIGNUP_FAILED)
                                    } else if (!response.getData()!!.getSubscriptionStatus()!!) {
                                        signUpSuccessSubscription(SIGNUP_SUCCESS)
                                    } else {
                                        signUpSuccess(SIGNUP_SUCCESS)
                                    }
                                } else if (response.getCode() == ResponseCodes.ACCESS_TOKEN_EXPIRED) {
                                    expireAccessToken(activity!!)
                                } else {
                                    showDialogNoInternet(activity!!, response.getMessage()!!, "", R.drawable.ic_alert_icon)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                isLoading = false
                                showLoadingDialog(isLoading)
                                showDialogNoInternet(activity!!, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                            }
                        }

                        override fun getError(error: ErrorModel, requestcode: Int) {
                            isLoading = false
                            logFirebaseEvents("submitAnswers", "Error Message: " + error.error_message)
                            showLoadingDialog(isLoading)
                            showDialogNoInternet(activity!!, error.error_message, "", R.drawable.ic_alert_icon)
                        }
                    })
                }
            } else {
                showDialogNoInternet(activity!!, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            logFirebaseEvents("submitAnswers", "Error Message: " + e.message)

        }
    }

    private fun showLoadingDialog(show: Boolean) {
        if (show) mUtilLoader.startLoader(activity!!) else mUtilLoader.stopLoader()
    }

    /**
     * show and hide terms and condition
     */
    private fun termsAndCondition() {
        try {
            binding.llTermsCond.visibility = View.VISIBLE
            termsVisible = true
            binding.llTermsCond.closeView.setOnClickListener {
                binding.llTermsCond.visibility = View.GONE
                termsVisible = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * to show error dialog
     */
    private fun showErrorDialog(message: String) {
        SwapdroidAlertDialog.Builder(activity!!)
                .setTitle(message)
                .isMessageVisible(false)
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
                .setIcon(R.drawable.ic_alert_icon, SwapdroidIcon.Visible)  //ic_star_border_black_24dp
                .OnPositiveClicked {
                    goBack()
                }.OnCancelClicked {
                    goBack()
                }
                .build()
    }

    /**
     * to show no internet connection
     */
    private fun showNoInternetErrorDialog() {
        SwapdroidAlertDialog.Builder(activity!!)
                .setTitle(getString(R.string.ooops))
                .setMessage(getString(R.string.check_internet))
                .isMessageVisible(false)
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
                .setIcon(R.drawable.ic_nointernet_icon, SwapdroidIcon.Visible)  //ic_star_border_black_24dp
                .OnPositiveClicked {
                    goBack()
                }.OnCancelClicked {
                    goBack()
                }
                .build()
    }

    /**
     * sign up failed and navigate to user blocked account screen
     */
    private fun signUpFailed(source: String) {
        val intent = Intent(activity!!, OtherScreensActivity::class.java)
        intent.putExtra(SOURCE, source)
        activity!!.startActivity(intent)
        activity!!.finish()
        activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * sign up success and navigate for welcome
     */
    private fun signUpSuccess(source: String) {
        val intent = Intent(activity!!, WelcomeActivity::class.java)
     //   val intent = Intent(activity!!, OtherScreensActivity::class.java)
        intent.putExtra(SOURCE, source)
        activity!!.startActivity(intent)
        activity!!.finish()
        activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * sign up success and navigate for payment/subscription
     */
    private fun signUpSuccessSubscription(source: String) {
        //val intent = Intent(activity!!, SubscriptionActivity::class.java)
        //val intent = Intent(activity!!, OtherScreensActivity::class.java)
        val intent = Intent(activity!!, WelcomeActivity::class.java)
        intent.putExtra(SOURCE, source)
        activity!!.startActivity(intent)
        activity!!.finish()
        activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * go back to sign in or common login
     */
    private fun goBack() {
        try {
            val extras = activity!!.intent.extras
            var source: String = ""
            if (extras != null) {
                source = extras.getString(SOURCE, "")
            }
            val intent = Intent(activity!!, CommonLoginActivity::class.java)
            startActivity(intent)
            activity!!.finish()
            activity!!.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
        } catch (e: Exception) {
            e.printStackTrace()
            activity!!.finish()
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
            WebConstants.GET_BIBLE_QUESTIONS_WS -> {
                try {
                    val response = gson.fromJson(responseData, SignUpBibleQuizResponse::class.java);
                    isLoading = false
                    showLoadingDialog(isLoading)
                    when (response.getCode()) {
                        ResponseCodes.Success -> try {
                            quizListData = response.getData()!!
                            //set fragment_intro_notes
                            signUpThreeQuizAdapter = SignUpThreeQuizAdapter(quizListData, activity!!)
                            binding.quizList.adapter = signUpThreeQuizAdapter
                            signUpThreeQuizAdapter.notifyDataSetChanged()
                            addData()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showErrorDialog(getString(R.string.failure_response))
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> expireAccessToken(activity!!)
                        else -> showErrorDialog(response.getMessage()!! + "\n" + getString(R.string.please_signin_again))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logFirebaseEvents("getBibleQuizList", "Error Message: " + e.message)
                    isLoading = false
                    showLoadingDialog(isLoading)
                    showErrorDialog(getString(R.string.failure_response))
                }

            }
            WebConstants.POST_BIBLE_QUESTIONS_WS -> {
                try {
                    val response = gson.fromJson(responseData, SignUpResponse::class.java);
                    isLoading = false
                    showLoadingDialog(isLoading)
                    if (response.getCode() == 200) {
                        //addData(activity!!, AppInstance.userObj!!.getLoginType()!!, response.getData()!!.getId()!!, response.getData()!!.getFirstName()!!, response.getData()!!.getLastName()!!, response.getData()!!.getEmail()!!, response.getData()!!.getGender()!!, response.getData()!!.getDOB()!!, response.getData()!!.getToken()!!, response.getData()!!.getIsNormalQuizComplete()!!, response.getData()!!.getIsBibleQuizComplete()!!, response.getData()!!.getIsActive()!!, response.getData()!!.getFacebookSocialId()!!, response.getData()!!.getGoogleSocialId()!!, AppInstance.userObj!!.getPassword()!!, response.getData()!!.getIsSignUpPerformed()!!, response.getData()!!.getIsBlocked()!!, response.getData()!!.getSubscriptionStatus()!!, response.getData()!!.getSubscriptionStatus()!!, response.getData()!!.getPhone()!!)
                        savePrefBoolean(activity!!, PreferenceConnector.IS_LOCATION_ALARM_ENABLED, response.getData()!!.getLocationAlarmEnabled())
                        savePrefBoolean(activity!!, PreferenceConnector.IS_GENDER_ALARM_ENABLED, response.getData()!!.getGenderAlarmEnabled())


                        if (response.getData()!!.getIsBlocked()!!) {
                            signUpFailed(SIGNUP_FAILED)
                        } else if (!response.getData()!!.getSubscriptionStatus()!!) {
                            signUpSuccessSubscription(SIGNUP_SUCCESS)
                        } else {
                            signUpSuccess(SIGNUP_SUCCESS)
                        }
                    } else if (response.getCode() == ResponseCodes.ACCESS_TOKEN_EXPIRED) {
                        expireAccessToken(activity!!)
                    } else {
                        showDialogNoInternet(activity!!, response.getMessage()!!, "", R.drawable.ic_alert_icon)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    isLoading = false
                    showLoadingDialog(isLoading)
                    showDialogNoInternet(activity!!, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }

            }
        }
    }

    override fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?) {
        when (label) {
            WebConstants.GET_BIBLE_QUESTIONS_WS -> {
                isLoading = false
                showLoadingDialog(isLoading)
                showErrorDialog(getString(R.string.failure_response))
            }
            WebConstants.POST_BIBLE_QUESTIONS_WS -> {
                isLoading = false
                logFirebaseEvents("submitAnswers", "Error Message: " + responseData!!.error_message)
                showLoadingDialog(isLoading)
                showDialogNoInternet(activity!!, responseData!!.error_message, "", R.drawable.ic_alert_icon)
            }
        }
    }

}
