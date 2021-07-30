package shiddush.view.com.mmvsd.facebook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import shiddush.view.com.mmvsd.BuildConfig
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.model.sociallogin.SocialLoginRequest
import shiddush.view.com.mmvsd.model.sociallogin.SocialLoginResponse
import shiddush.view.com.mmvsd.model.subscription.CheckSubscriptionRequest
import shiddush.view.com.mmvsd.model.subscription.CheckSubscriptionResponse
import shiddush.view.com.mmvsd.model.subscriptionStatus.SubscriptionStatus
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
import java.util.*

/**
 * Created by Sumit Kumar.
 */
class FacebookCustomClass(context: Context) : RestClient.OnAsyncRequestComplete, RestClient.OnAsyncRequestError {

    private var callbackManager: CallbackManager? = null
    private val KEY_EMAIL = "email"
    private val KEY_PROFILE = "public_profile"
    private val KEY_GENDER = "gender"
    private val KEY_BIRTHDAY = "birthday"
    private val KEY_ID = "id"
    private val KEY_FIRST_NAME = "first_name"
    private val KEY_LAST_NAME = "last_name"
    private val KEY_ALBUMS = "albums"
    private var pendingAction = PendingAction.NONE

    private var mContext = context
    private var mUtilLoader: UtilLoader = UtilLoader(context)

    /**
     * perform facebook login
     */
    fun facebookLogin() {

        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(mContext as Activity, listOf(KEY_EMAIL, KEY_PROFILE))  //, "user_birthday", "user_photos"
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val accessToken = result?.accessToken
                updateUI(accessToken)
            }

            override fun onCancel() {
                if (pendingAction != PendingAction.NONE) {
                    pendingAction = PendingAction.NONE
                }
                LoginManager.getInstance().logOut()
                updateUI(null)
            }

            override fun onError(error: FacebookException?) {
                if (pendingAction != PendingAction.NONE && error is FacebookAuthorizationException) {
                    pendingAction = PendingAction.NONE
                }
                LoginManager.getInstance().logOut()
                updateUI(null)
            }
        })
    }

    /**
     * retrieve information and call social signin webservice
     */
    private fun updateUI(loginResult: AccessToken?) {
        if (loginResult != null) {
            mUtilLoader.startLoader(mContext)
            val facebookLoginRequest = FacebookLoginRequest()
            var request = GraphRequest.newMeRequest(loginResult
            ) { `object`, response ->
                var albumId: String? = null
                val facebookRequest = FacebookRequest()

                try {
                    if (`object`.has(KEY_EMAIL)) {
                        facebookLoginRequest.email = `object`.getString(KEY_EMAIL)
                    }
                    if (`object`.has(KEY_GENDER)) {
                        facebookLoginRequest.gender = `object`.getString(KEY_GENDER)
                    }
                    if (`object`.has(KEY_BIRTHDAY)) {
                        facebookLoginRequest.DOB = `object`.getString(KEY_BIRTHDAY)
                    }
                    if (`object`.has(KEY_ID)) {
                        facebookLoginRequest.facebook = `object`.getString(KEY_ID)
                        facebookLoginRequest.profilePic = "https://graph.facebook.com/" + `object`.getString(KEY_ID) + "/picture?type=large"
                    }
                    if (`object`.has(KEY_LAST_NAME)) {
                        facebookLoginRequest.lastName = `object`.getString(KEY_LAST_NAME)
                    }
                    if (`object`.has(KEY_FIRST_NAME)) {
                        facebookLoginRequest.firstName = `object`.getString(KEY_FIRST_NAME)
                    }

                    if (`object`.has(KEY_ALBUMS)) {
                        val getFacebookAlbums = Gson().fromJson<GetFacebookAlbums>(
                                `object`.getString(KEY_ALBUMS),
                                GetFacebookAlbums::class.java
                        )
                        for (al in getFacebookAlbums.facebookAlbums!!) {
                            if (al.name.equals("Profile Pictures", true)) {
                                albumId = al.id
                            }
                        }
                    }

                    if (albumId != null) {
                        val parameters = Bundle()
                        parameters.putString("fields", "id, name, link, photos,albums, picture,source")
                        GraphRequest(
                                AccessToken.getCurrentAccessToken(),
                                "/$albumId/photos",
                                parameters,
                                HttpMethod.GET,
                                GraphRequest.Callback { response ->
                                    var rawPhotosData: JSONArray? = null
                                    val userProfileImage =
                                            "https://graph.facebook.com/" + facebookRequest.facebook_id + "/picture?type=large"
                                    var profileImages = ProfileImages()
                                    profileImages.name = userProfileImage
                                    facebookRequest.userImages.add(profileImages)
                                    try {
                                        rawPhotosData = response?.jsonObject?.getJSONArray("data")
                                        for (j in 0 until rawPhotosData?.length()!!) {
                                            val url = (rawPhotosData.get(j) as JSONObject).get("source").toString()
                                            profileImages = ProfileImages()
                                            profileImages.name = url
                                            facebookRequest.userImages.add(profileImages)
                                            facebookLoginRequest.profilePic = url
                                        }
                                        LoginManager.getInstance().logOut()

                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }).executeAsync()

                    }
                    if (facebookLoginRequest.email!!.trim().isEmpty()) {
                        mUtilLoader.stopLoader()
                        showDialogNoInternet(mContext, mContext.getString(R.string.email_not_found_via_fb), "", R.drawable.ic_alert_icon)
                    } else {
                        socialLogin(facebookLoginRequest, mContext)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    mUtilLoader.stopLoader()
                }
            }

            request = newRequest(request)
            request.executeAsync()
        } else {
            showToast(mContext, mContext.getString(R.string.fb_login_failed), Toast.LENGTH_SHORT)
        }
    }

    private fun newRequest(request: GraphRequest): GraphRequest {
        val fields = HashSet<String>()
        val requestFields =
                arrayOf("id, name, link, gender, birthday, email, first_name, last_name, work, education, about, photos,albums, picture")
        fields.addAll(listOf(*requestFields))

        val parameters = request.parameters
        parameters.putString("fields", TextUtils.join(",", fields))
        request.parameters = parameters

        return request
    }

    private enum class PendingAction {
        NONE
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * logout from facebook
     */
    fun logoutFacebook() {
        LoginManager.getInstance().logOut()
    }

    /**
     *  perform social login via facebook
     */
    private fun socialLogin(facebookLoginRequest: FacebookLoginRequest, context: Context) {
        try {
            val manager = NetworkManager()
            val request = SocialLoginRequest()
            request.firstName = facebookLoginRequest.firstName
            request.lastName = facebookLoginRequest.lastName
            request.email = facebookLoginRequest.email
            if (facebookLoginRequest.gender.equals("male")) {
                request.gender = 0
            } else {
                request.gender = 1
            }
            request.dOB = facebookLoginRequest.DOB
            request.loginType = 1 //facebook
            request.facebookSocialId = facebookLoginRequest.facebook
            request.googleSocialId = ""
            request.isTandC = false
            request.deviceType = WebConstants.DEVICE_TYPE
            request.deviceToken = getPrefString(context, PreferenceConnector.DEVICE_TOKEN)
            request.appVersion = BuildConfig.VERSION_NAME!!
            request.deviceInfo = getDeviceInfo()
            request.lat = "" + getPrefString(context, PreferenceConnector.LAT)
            request.lng = "" + getPrefString(context, PreferenceConnector.LNG)
            request.city = "" + getPrefString(context, PreferenceConnector.CITY)
            request.country = "" + getPrefString(context, PreferenceConnector.COUNTRY)
            request.countryCode = "" + getPrefString(context, PreferenceConnector.COUNTRYCODE)

            manager.createApiRequest(ApiUtilities.getAPIService().socialLogin(request), object : ServiceListener<SocialLoginResponse> {
                override fun getServerResponse(response: SocialLoginResponse, requestcode: Int) {
                    try {
                        logoutFacebook()
                        when (response.getCode()) {
                            ResponseCodes.Success -> //positive response
                                try {
                                    AppInstance.sociallogObj = response
                                    addData(context, 1, response.getData()!!.getId()!!, response.getData()!!.getFirstName()!!, response.getData()!!.getLastName()!!, response.getData()!!.getEmail()!!, response.getData()!!.getGender()!!, response.getData()!!.getDOB()!!, response.getData()!!.getToken()!!, response.getData()!!.getIsNormalQuizComplete()!!, response.getData()!!.getIsBibleQuizComplete()!!, response.getData()!!.getIsActive()!!, response.getData()!!.getFacebookSocialId()!!, response.getData()!!.getGoogleSocialId()!!, "", response.getData()!!.getIsSignUpPerformed()!!, response.getData()!!.getIsBlocked()!!, response.getData()!!.getSubscriptionDone()!!, response.getData()!!.getSubscriptionFree()!!, "")
                                    when {
                                        response.getData()!!.getIsBlocked()!! -> //check for blocked
                                            signUpSuccessOrFailed(context, SIGNUP_FAILED)
                                        !response.getData()!!.getIsSignUpPerformed()!! || !response.getData()!!.getIsNormalQuizComplete()!! || !response.getData()!!.getIsBibleQuizComplete()!! -> goForSignUP(context)
                                        !response.getData()!!.getSubscriptionFree()!! && !response.getData()!!.getSubscriptionDone()!! -> checkAppSubscriptionDetail(context)
                                        !response.getData()!!.getSubscriptionFree()!! && response.getData()!!.getSubscriptionDone()!! -> checkAppSubscriptionDetail(context)
                                        else -> goForHome(context)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    showDialogNoInternet(context, context.getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                                }
                            ResponseCodes.NoUserFound -> //first time user
                                try {
                                    addData(context, 1, "", request.firstName!!, request.lastName!!, request.email!!, request.gender!!, request.dOB!!, "", false, false, false, request.facebookSocialId!!, request.googleSocialId!!, "", false, false, false, false, "")
                                    goForSignUP(context)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    goForSignUP(context)
                                }
                            ResponseCodes.ReviewNotDone -> { //review not done
                                AppInstance.sociallogObj = response
                                addData(context, 1, response.getData()!!.getId()!!, response.getData()!!.getFirstName()!!, response.getData()!!.getLastName()!!, response.getData()!!.getEmail()!!, response.getData()!!.getGender()!!, response.getData()!!.getDOB()!!, response.getData()!!.getToken()!!, response.getData()!!.getIsNormalQuizComplete()!!, response.getData()!!.getIsBibleQuizComplete()!!, response.getData()!!.getIsActive()!!, response.getData()!!.getFacebookSocialId()!!, response.getData()!!.getGoogleSocialId()!!, "", response.getData()!!.getIsSignUpPerformed()!!, response.getData()!!.getIsBlocked()!!, response.getData()!!.getSubscriptionDone()!!, response.getData()!!.getSubscriptionFree()!!, "")
                                navigateToReviewUser(context, response.getData()!!.getReviewFor()!!)
                            }
                            else -> showDialogNoInternet(context, response.getMessage()!!, "", R.drawable.ic_alert_icon)
                        }
                        mUtilLoader.stopLoader()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mUtilLoader.stopLoader()
                        showDialogNoInternet(context, context.getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                    }
                }

                override fun getError(error: ErrorModel, requestcode: Int) {
                    logoutFacebook()
                    System.out.println("Login error Response ==> " + error.error_message)
                    showDialogNoInternet(context, context.getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                    mUtilLoader.stopLoader()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *  check app subscription details
     */
    fun checkAppSubscriptionDetail(context: Context) {
        try {
            if (isNetworkAvailable(context)) {
                mUtilLoader.startLoader(mContext)
                val request = CheckSubscriptionRequest()
                request.userId = AppInstance.userObj!!.getId()!!
                request.packageName = BuildConfig.APPLICATION_ID
                request.productId = ITEM_SKU
                request.deviceType = DEVICE_TYPE
                val token = AppInstance.userObj!!.getToken()!!
                val manager = NetworkManager()
                manager.createApiRequest(ApiUtilities.getAPIService().appSubscriptionDetail(token, request), object : ServiceListener<CheckSubscriptionResponse> {
                    override fun getServerResponse(response: CheckSubscriptionResponse, requestcode: Int) {
                        try {
                            when (response.code) {
                                ResponseCodes.Success -> //success
                                {
                                    if (response.data.payload.cancelReason == -1 && response.data.payload.paymentState == 2) { // not cancel and free
                                        goForHome(context as Activity)
                                    } else if (response.data.payload.cancelReason != -1 && response.data.payload.paymentState == 2) { // cancel and free
                                        goForHome(context as Activity)
                                    } else if (response.data.payload.cancelReason != -1) { // cancel and not free
                                        showSubscriptionCancelDialog(context as Activity, context.getString(R.string.renew_cancellation))
                                    } else { //not cancelled
                                        when (response.data.payload.paymentState) {
                                            0 -> showSubscriptionCancelDialog(context as Activity, context.getString(R.string.payment_pending))
                                            1, 2 -> goForHome(context as Activity)
                                            3 -> showSubscriptionCancelDialog(context as Activity, context.getString(R.string.price_changed))
                                        }
                                    }
                                }
                                else -> {
                                    Log.e("SWAPLOG", response.message!!)
                                    checkAppSubscriptionStatus()
                                }
                            }
                            mUtilLoader.stopLoader()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            checkAppSubscriptionStatus()
                        }
                    }

                    override fun getError(error: ErrorModel, requestcode: Int) {
                        Log.e("SWAPLOG", error.error_message)
                        goForHome(context as Activity)
                        mUtilLoader.stopLoader()
                    }
                })
            } else {
                showDialogNoInternet(context as Activity, context.getString(R.string.ooops), context.getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            goForHome(context as Activity)
        }
    }

    private fun checkAppSubscriptionStatus() {
        val token = AppInstance.userObj!!.getToken()!!
        val userid = AppInstance.userObj!!.getId()!!
        val p = RequestParams();
        p.add("userid", userid)

        val rest = RestClient(mContext as Activity, RestMethod.GET, p)
        rest.setToken(token);
        rest.execute(WebConstants.APP_SUBSCRIPTION_STATUS_WS);
    }

    override fun asyncResponse(responseData: String?, label: String?, `object`: Any?) {
        val gson = Gson()
        when (label) {
            WebConstants.APP_SUBSCRIPTION_STATUS_WS -> {
                println("subscription_status ***" + responseData);
                val response = gson.fromJson(responseData, SubscriptionStatus::class.java);
                //commenting subscription code

//                if (!response.data?.subscriptionStatus!!)
//                    gotoSubscriptionActivity(mContext)
//                else {
//                    AppInstance.userObj!!.setIsSubscriptionDone(response.data?.subscriptionStatus!!)
//                    AppInstance.userObj!!.setIsSubscriptionFree(response.data?.subscriptionStatus!!)
//                    goForHome(mContext as Activity)
//                }
                AppInstance.userObj!!.setIsSubscriptionDone(response.data?.subscriptionStatus!!)
                AppInstance.userObj!!.setIsSubscriptionFree(response.data?.subscriptionStatus!!)
                goForHome(mContext as Activity)
            }
        }

    }

    override fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?) {
    }

    /**
     *  navigation to Signup activity
     */
    private fun goForSignUP(context: Context) {
        val intent = Intent(context, TermsAndConActivity::class.java)
        intent.putExtra(SOURCE, COMMON_LOGIN)
        intent.putExtra(VIA, FACEBOOK)
        context.startActivity(intent)
        (context as Activity).finish()
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     *  navigation to Waiting screen
     */
    private fun goForHome(context: Context) {
        val intent = Intent(context, WaitingActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     * navigate to SubscriptionActivity
     */
    private fun gotoSubscriptionActivity(context: Context) {
        val intent = Intent(context, SubscriptionActivity::class.java)
        intent.putExtra(SOURCE, COMMON_LOGIN)
        intent.putExtra(VIA, NORMAL)
        (context as Activity).startActivity(intent)
        (context as Activity).finish()
        (context as Activity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     *  navigation to Signup failed
     */
    private fun signUpSuccessOrFailed(context: Context, tag: String) {
        val intent = Intent(context, OtherScreensActivity::class.java)
        intent.putExtra(SOURCE, tag)
        context.startActivity(intent)
        (context as Activity).finish()
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    /**
     *  navigation to Review User
     */
    private fun navigateToReviewUser(context: Context, to_userid: String) {
        val intent = Intent(context, ReviewActivity::class.java)
        intent.putExtra(TOID, to_userid)
        context.startActivity(intent)
        (context as Activity).finish()
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

}