package shiddush.view.com.mmvsd.ui.signup


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson

import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.FragmentSignUpTwoBinding
import shiddush.view.com.mmvsd.model.MainModel
import shiddush.view.com.mmvsd.model.signupbiblequiz.SignUpBibleQuizData
import shiddush.view.com.mmvsd.model.signupbiblequiz.SignUpBibleQuizResponse
import shiddush.view.com.mmvsd.model.signupbiblequiz.SignUpNormalQuizPostRequest
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.rest.RequestParams
import shiddush.view.com.mmvsd.rest.RestClient
import shiddush.view.com.mmvsd.rest.RestMethod
import shiddush.view.com.mmvsd.ui.commonloginscreen.CommonLoginActivity
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
class SignUpTwoFragment : Fragment(), RestClient.OnAsyncRequestComplete,
        RestClient.OnAsyncRequestError {

    lateinit var binding: FragmentSignUpTwoBinding

    //Question ID
    private var firstQID: String = ""
    private var secondQID: String = ""
    private var thirdQID: String = ""
    private var forthQID: String = ""

    //Question
    private var firstQuestion: String = ""
    private var secondQuestion: String = ""
    private var thirdQuestion: String = ""
    private var forthQuestion: String = ""

    //Answer
    private var firstAID: String = ""
    private var secondAID: String = ""
    private var thirdAID: String = ""
    private var forthAID: String = ""

    //Answer Options
    private var firstA1op: String = ""
    private var firstA2op: String = ""
    private var secondA1op: String = ""
    private var secondA2op: String = ""
    private var thirdA1op: String = ""
    private var thirdA2op: String = ""
    private var forthA1op: String = ""
    private var forthA2op: String = ""

    private var qType: Int = 0

    private lateinit var mUtilLoader: UtilLoader

    private var isLoading: Boolean = false
    private var quizListData = ArrayList<SignUpBibleQuizData>()
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private val TAG = SignUpTwoFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the fragment_intro_notes for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_two, container, false)
        val myView: View = binding.root
        mUtilLoader = UtilLoader(activity!!)
        firebaseAnalytics = FirebaseAnalytics.getInstance(activity!!)
        setTextSizes()
        onCLickListeners()
        getNormalQuizList()
        return myView
    }

    /**
     * to set all text sizes
     */
    private fun setTextSizes() {
        val size25 = getFontSize(activity!!, 25)
        val size18 = getFontSize(activity!!, 18)
        val size13 = getFontSize(activity!!, 13)

        binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)
        binding.tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size25)

        binding.tvFirstQuestion.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvMqOneFFOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvMqOneFFTOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvMqTwoFFOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvMqTwoFFTOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.tvMqOneSSOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvMqOneSSTOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvMqTwoSSOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvMqTwoSSTOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.tvMqOneTTOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvMqOneTTTOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvMqTwoTTOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvMqTwoTTTOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.tvForthQuestion.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.YesOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.NoOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        binding.btnNext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)

        //set Background Image
        val bgImage = getBackImageSize(activity!!)
        val bgImageLess20 = getBackImage80Size(activity!!)
        binding.firstCardCon.background = ContextCompat.getDrawable(activity!!, bgImage)
        binding.secondCardCon.background = ContextCompat.getDrawable(activity!!, bgImageLess20)

        try {
            val size20 = getFontSize(activity!!, 20)
            val ivHeightWidth = dpToPxs(size20.toInt())
            binding.backArrow.layoutParams.height = ivHeightWidth
            binding.backArrow.layoutParams.width = ivHeightWidth
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * to set up all click listeners
     */
    private fun onCLickListeners() {

        //set question
        try {
            if (AppInstance.userObj!!.getGender()!! == 0) {
                binding.tvForthQuestion.text = getString(R.string.are_you_a_cohen)
                binding.tvMqOneTTTOption.text = getString(R.string.wearing_tefillin)
                binding.tvMqTwoTTTOption.text = getString(R.string.not_wearing_tefillin)
            } else {
                binding.tvForthQuestion.text = getString(R.string.are_you_able_to_marry_cohen)
                binding.tvMqOneTTTOption.text = getString(R.string.men_wearing_tefillin)
                binding.tvMqTwoTTTOption.text = getString(R.string.men_not_wearing_tefillin)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.backView.setOnClickListener {
            activity!!.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            if (isNetworkAvailable(activity!!)) {
                if (quizListData != null && quizListData.size > 1) {
                    if (firstAID.isEmpty() || secondAID.isEmpty() || thirdAID.isEmpty() || forthAID.isEmpty()) {
                        showDialogNoInternet(activity!!, getString(R.string.please_select_options), "", R.drawable.ic_alert_icon)
                    } else {
                        submitAnswers()
                        //navigateToThird()
                    }
                } else {
                    showDialogNoInternet(activity!!, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }
            } else {
                showDialogNoInternet(activity!!, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        }

        //first fragment_intro_notes
        binding.mqOneFirstOption.setOnClickListener {
            binding.mqOneFirstOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_gradient_left_orange)
            binding.mqOneSecondOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_white_right_corner)

            binding.fcFirstOptDivider.visibility = View.INVISIBLE
            binding.tvMqOneFFOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            binding.tvMqOneFFTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            binding.tvMqTwoFFOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))
            binding.tvMqTwoFFTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))

            //setAnswer
            firstAID = firstA1op
        }

        binding.mqOneSecondOption.setOnClickListener {
            binding.mqOneFirstOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_white_left_corner)
            binding.mqOneSecondOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_gradient_right_orange)

            binding.fcFirstOptDivider.visibility = View.INVISIBLE
            binding.tvMqOneFFOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))
            binding.tvMqOneFFTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))
            binding.tvMqTwoFFOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            binding.tvMqTwoFFTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            //setAnswer
            firstAID = firstA2op
        }

        //second fragment_intro_notes
        binding.mqTwoFirstOption.setOnClickListener {
            binding.mqTwoFirstOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_gradient_left_orange)
            binding.mqTwoSecondOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_white_right_corner)

            binding.fcSecondOptDivider.visibility = View.INVISIBLE
            binding.tvMqOneSSOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            binding.tvMqOneSSTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            binding.tvMqTwoSSOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))
            binding.tvMqTwoSSTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))

            //setAnswer
            secondAID = secondA1op
        }

        binding.mqTwoSecondOption.setOnClickListener {
            binding.mqTwoFirstOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_white_left_corner)
            binding.mqTwoSecondOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_gradient_right_orange)

            binding.fcSecondOptDivider.visibility = View.INVISIBLE
            binding.tvMqOneSSOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))
            binding.tvMqOneSSTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))
            binding.tvMqTwoSSOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            binding.tvMqTwoSSTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            //setAnswer
            secondAID = secondA2op
        }

        //third fragment_intro_notes
        binding.mqThreeFirstOption.setOnClickListener {
            binding.mqThreeFirstOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_gradient_left_orange)
            binding.mqThreeSecondOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_white_right_corner)

            binding.fcThirdOptDivider.visibility = View.INVISIBLE
            binding.tvMqOneTTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            binding.tvMqOneTTTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            binding.tvMqTwoTTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))
            binding.tvMqTwoTTTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))

            //setAnswer
            thirdAID = thirdA1op
        }

        binding.mqThreeSecondOption.setOnClickListener {
            binding.mqThreeFirstOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_white_left_corner)
            binding.mqThreeSecondOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_gradient_right_orange)

            binding.fcThirdOptDivider.visibility = View.INVISIBLE
            binding.tvMqOneTTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))
            binding.tvMqOneTTTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))
            binding.tvMqTwoTTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            binding.tvMqTwoTTTOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            //setAnswer
            thirdAID = thirdA2op
        }

        //Forth fragment_intro_notes
        binding.YesOption.setOnClickListener {
            binding.YesOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_shape_orange_filled)
            binding.NoOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_shape_orange_border)
            binding.YesOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            binding.NoOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))

            //setAnswer
            forthAID = forthA1op
        }

        binding.NoOption.setOnClickListener {
            binding.YesOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_shape_orange_border)
            binding.NoOption.background = ContextCompat.getDrawable(activity!!, R.drawable.curved_shape_orange_filled)
            binding.YesOption.setTextColor(ContextCompat.getColor(activity!!, R.color.colorDarkGrayText))
            binding.NoOption.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            //setAnswer
            forthAID = forthA2op
        }

    }

    //set ids of each view
    private fun setIds() {
        if (quizListData != null && quizListData.size == 4) {
            //for ids
            for (i in 0 until quizListData.size) {
                if (quizListData[i].getOption1()!!.contains(getString(R.string.kosher))) {
                    firstQID = quizListData[i].getId()!!
                    firstQuestion = quizListData[i].getQuestions()!!
                    if (quizListData[i].getOption1()!! == getString(R.string.keeping_kosher) || quizListData[i].getOption2()!! == getString(R.string.keeping_kosher)) {
                        firstA1op = quizListData[i].getOption1()!!
                    }
                    if (quizListData[i].getOption1()!! == getString(R.string.not_keeping_kosher) || quizListData[i].getOption2()!! == getString(R.string.not_keeping_kosher)) {
                        firstA2op = quizListData[i].getOption2()!!
                    }
                } else if (quizListData[i].getOption1()!!.contains(getString(R.string.shabbos))) {
                    secondQID = quizListData[i].getId()!!
                    secondQuestion = quizListData[i].getQuestions()!!
                    if (quizListData[i].getOption1()!! == getString(R.string.keeping_shabbos) || quizListData[i].getOption2()!! == getString(R.string.keeping_shabbos)) {
                        secondA1op = quizListData[i].getOption1()!!
                    }
                    if (quizListData[i].getOption1()!! == getString(R.string.not_keeping_shabbos) || quizListData[i].getOption2()!! == getString(R.string.not_keeping_shabbos)) {
                        secondA2op = quizListData[i].getOption2()!!
                    }
                } else if (quizListData[i].getOption1()!!.contains(getString(R.string.tefillin))) {
                    thirdQID = quizListData[i].getId()!!
                    thirdQuestion = quizListData[i].getQuestions()!!
                    if (AppInstance.userObj!!.getGender()!! == 0) {
                        if (quizListData[i].getOption1()!! == getString(R.string.wearing_tefillin) || quizListData[i].getOption2()!! == getString(R.string.wearing_tefillin)) {
                            thirdA1op = quizListData[i].getOption1()!!
                        }
                        if (quizListData[i].getOption1()!! == getString(R.string.not_wearing_tefillin) || quizListData[i].getOption2()!! == getString(R.string.not_wearing_tefillin)) {
                            thirdA2op = quizListData[i].getOption2()!!
                        }
                    } else {
                        if (quizListData[i].getOption1()!! == getString(R.string.men_wearing_tefillin) || quizListData[i].getOption2()!! == getString(R.string.men_wearing_tefillin)) {
                            thirdA1op = quizListData[i].getOption1()!!
                        }
                        if (quizListData[i].getOption1()!! == getString(R.string.men_not_wearing_tefillin) || quizListData[i].getOption2()!! == getString(R.string.men_not_wearing_tefillin)) {
                            thirdA2op = quizListData[i].getOption2()!!
                        }
                    }
                } else if (quizListData[i].getQuestions()!!.contains(getString(R.string.cohen))) {
                    forthQID = quizListData[i].getId()!!
                    forthQuestion = quizListData[i].getQuestions()!!
                    if (quizListData[i].getOption1()!!.contains(getString(R.string.yes_small)) || quizListData[i].getOption2()!!.contains(getString(R.string.yes_small))) {
                        forthA1op = quizListData[i].getOption1()!!
                    }
                    if (quizListData[i].getOption1()!!.contains(getString(R.string.no_small)) || quizListData[i].getOption2()!!.contains(getString(R.string.no_small))) {
                        forthA2op = quizListData[i].getOption2()!!
                    }
                }
            }
        }
    }

    //get quiz questions api call
    fun getNormalQuizList() {
        if (isNetworkAvailable(activity!!)) {
            if (!isLoading) {
                try {
                    isLoading = true
                    showLoadingDialog(isLoading)
                    val token = AppInstance.userObj!!.getToken()!!

                    val p = RequestParams();
                    p.add("id", AppInstance.userObj!!.getId()!!)
                    val rest = RestClient(this!!, RestMethod.POST, p)
                    rest.setToken(token)
                    rest.execute(WebConstants.GET_NORMAL_QUESTIONS_WS);
                } catch (e: Exception) {
                    e.printStackTrace()
                    logFirebaseEvents("getNormalQuizList", "Error Message: " + e.message)
                    isLoading = false
                    showLoadingDialog(isLoading)
                }
            }
        } else {
            showNoInternetErrorDialog()
        }
    }

    //submitting answers and call webservice
    private fun submitAnswers() {
        try {
            var request = SignUpNormalQuizPostRequest()
            request.userId = AppInstance.userObj!!.getId()!!

            var answers = ArrayList<SignUpNormalQuizPostRequest.Answer>()
            var ans1 = SignUpNormalQuizPostRequest.Answer()
            ans1.qId = firstQID
            ans1.question = firstQuestion
            ans1.answerText = firstAID
            answers.add(ans1)
            var ans2 = SignUpNormalQuizPostRequest.Answer()
            ans2.qId = secondQID
            ans2.question = secondQuestion
            ans2.answerText = secondAID
            answers.add(ans2)
            var ans3 = SignUpNormalQuizPostRequest.Answer()
            ans3.qId = thirdQID
            ans3.question = thirdQuestion
            ans3.answerText = thirdAID
            answers.add(ans3)
            var ans4 = SignUpNormalQuizPostRequest.Answer()
            ans4.qId = forthQID
            ans4.question = forthQuestion
            ans4.answerText = forthAID
            answers.add(ans4)
            request.answer = answers

            if (isNetworkAvailable(activity!!)) {
                if (!isLoading) {
                    isLoading = true
                    showLoadingDialog(isLoading)
                    val token = AppInstance.userObj!!.getToken()!!
                    val manager = NetworkManager()
                    manager.createApiRequest(ApiUtilities.getAPIService().postNormalQuestions(token, request), object : ServiceListener<MainModel> {
                        override fun getServerResponse(response: MainModel, requestcode: Int) {
                            try {
                                isLoading = false
                                showLoadingDialog(isLoading)
                                when (response.getCode()) {
                                    ResponseCodes.Success -> {
                                        //change status
                                        try {
                                            AppInstance.userObj = getUserObject(activity!!)
                                            addData(activity!!, 0, AppInstance.userObj!!.getId()!!, AppInstance.userObj!!.getFirstName()!!, AppInstance.userObj!!.getLastName()!!, AppInstance.userObj!!.getEmail()!!, AppInstance.userObj!!.getGender()!!, AppInstance.userObj!!.getDOB()!!, AppInstance.userObj!!.getToken()!!, true, AppInstance.userObj!!.getIsBibleQuizComplete()!!, AppInstance.userObj!!.getIsActive()!!, AppInstance.userObj!!.getFacebookSocialId()!!, AppInstance.userObj!!.getGoogleSocialId()!!, AppInstance.userObj!!.getPassword()!!, AppInstance.userObj!!.getIsSignUpPerformed()!!, AppInstance.userObj!!.getIsBlocked()!!, AppInstance.userObj!!.getSubscriptionDone()!!, AppInstance.userObj!!.getSubscriptionFree()!!,AppInstance.userObj!!.getPhone()!!)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            logFirebaseEvents("submitAnswers", "Error Message: " + e.message)

                                        }
                                        navigateToThird()
                                    }
                                    ResponseCodes.ACCESS_TOKEN_EXPIRED -> expireAccessToken(activity!!)
                                    else -> showDialogNoInternet(activity!!, response.getMessage()!!, "", R.drawable.ic_alert_icon)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                isLoading = false
                                showLoadingDialog(isLoading)
                                logFirebaseEvents("submitAnswers", "Error Message: " + e.message)
                            }
                        }

                        override fun getError(error: ErrorModel, requestcode: Int) {
                            showDialogNoInternet(activity!!, error.error_message, "", R.drawable.ic_alert_icon)
                            isLoading = false
                            showLoadingDialog(isLoading)
                            logFirebaseEvents("submitAnswers", "Error Message: " + error.error_message)

                        }
                    })
                }
            } else {
                showDialogNoInternet(activity!!, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //show error dialog
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

    // show no internet dialog
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

    // show loader
    private fun showLoadingDialog(show: Boolean) {
        if (show) mUtilLoader.startLoader(activity!!) else mUtilLoader.stopLoader()
    }

    //navigation of fragments
    private fun navigateToThird() {
        // Get the text fragment instance
        val textFragment = SignUpThreeFragment()

        // Get the support fragment manager instance
        val manager = fragmentManager

        // Begin the fragment transition using support fragment manager
        val transaction = manager!!.beginTransaction()

        // Replace the fragment on container
        transaction.replace(R.id.signup_container, textFragment, THREE_FRAGMENT)
        transaction.addToBackStack(null)

        // Finishing the transition
        transaction.commit()
    }

    /**
     * navigate to sign in or common login
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
            WebConstants.GET_NORMAL_QUESTIONS_WS -> {
                try {
                    val response = gson.fromJson(responseData, SignUpBibleQuizResponse::class.java);
                    isLoading = false
                    showLoadingDialog(isLoading)
                    when (response.getCode()) {
                        ResponseCodes.Success -> try {
                            quizListData = response.getData()!!
                            //set ids
                            setIds()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showErrorDialog(getString(R.string.failure_response))
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> expireAccessToken(activity!!)
                        else -> showErrorDialog(response.getMessage()!! + " " + getString(R.string.please_signin_again))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    isLoading = false
                    showLoadingDialog(isLoading)
                    showErrorDialog(getString(R.string.failure_response))
                    logFirebaseEvents("getNormalQuizList", "Error Message: " + e.message)

                }

            }
        }
    }

    override fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?) {
        when (label) {
            WebConstants.GET_NORMAL_QUESTIONS_WS -> {
                isLoading = false
                showLoadingDialog(isLoading)
                logFirebaseEvents("getNormalQuizList", "Error Message: " + responseData!!.error_message)
                showErrorDialog(getString(R.string.failure_response))
            }
        }

    }
}
