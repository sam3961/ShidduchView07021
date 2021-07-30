package shiddush.view.com.mmvsd.ui.subscription

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson

import kotlinx.android.synthetic.main.dialog_subscription.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivitySubscriptionBinding
import shiddush.view.com.mmvsd.inappbilling.IabHelper
import shiddush.view.com.mmvsd.inappbilling.IabResult
import shiddush.view.com.mmvsd.inappbilling.Purchase
import shiddush.view.com.mmvsd.model.MainModel
import shiddush.view.com.mmvsd.model.login.LoginResponse
import shiddush.view.com.mmvsd.repository.ErrorModel
import shiddush.view.com.mmvsd.repository.ResponseCodes
import shiddush.view.com.mmvsd.repository.WebConstants
import shiddush.view.com.mmvsd.rest.RequestParams
import shiddush.view.com.mmvsd.rest.RestClient
import shiddush.view.com.mmvsd.rest.RestMethod
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.ui.commonloginscreen.CommonLoginActivity
import shiddush.view.com.mmvsd.ui.waitingscreen.WaitingActivity
import shiddush.view.com.mmvsd.ui.welcome.WelcomeActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.widget.loader.UtilLoader
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidColorConstants
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon

/**
 * this is the In App Purchase (G-Pay) screen for Subscription
 * https://www.techotopia.com/index.php/An_Android_Studio_Google_Play_In-app_Billing_Tutorial
 **/

class SubscriptionActivity : AppCompatActivity(), PurchasesUpdatedListener,
        RestClient.OnAsyncRequestComplete, RestClient.OnAsyncRequestError {

    private lateinit var dialog: Dialog
    lateinit var binding: ActivitySubscriptionBinding
    lateinit var viewModel: SubscriptionViewModel
    private var screenLock: PowerManager.WakeLock? = null
    private lateinit var mUtilLoader: UtilLoader
    private var paidCost = ""
    private var isPurchaseDone = false
    private lateinit var mPurchase: Purchase
    private var isClickable: Boolean = false
    private var firebaseAnalytics: FirebaseAnalytics? = null

    companion object {
        private val TAG = SubscriptionActivity::class.java.simpleName
    }

    // (arbitrary) request code for the purchase flow
    val RC_REQUEST = 10001

    // The helper object
    var mHelper: IabHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {

            firebaseAnalytics = FirebaseAnalytics.getInstance(this)
            firebaseAnalytics?.setUserId(getUserObject(this).getEmail());
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_subscription)
        viewModel = SubscriptionViewModel()
        binding.viewModel = viewModel
        mUtilLoader = UtilLoader(this)
        changeNavBack()
        setTextSizes()
        onClickFunctions()
        initBilling()
        checkBilling()
        showSubscriptionDialog()

        /*val highlightManager = HighlightManager(this)
//        highlightManager.reshowAllHighlights()        //  for enabled every launch time highlights.
        val subs = highlightManager.addView(R.id.subscriptionBtn)
                .setDescriptionId(R.string.highlight_subscription_1)
        highlightManager.addView(R.id.llDotTwo).setTitle(R.string.refresh_menu_text)
                .setDescriptionId(R.string.highlight_subscription_2)
        highlightManager.addView(R.id.tv18Doller).setTitle(R.string.refresh_menu_text)
                .setDescriptionId(R.string.highlight_subscription_3)*/


    }

    private fun initBilling() {

        try {
            //++Initialize In App Billing
            // Create the helper, passing it our context and the public key to verify signatures with
            Log.e(TAG, "Creating IAB helper.")
            mHelper = IabHelper(this, base64EncodedPublicKey)
            mHelper?.startSetup { result ->
                if (!result.isSuccess) {
                    Log.e(TAG, "In-app Billing setup failed: $result")
                } else {
                    Log.e(TAG, "In-app Billing is set up OK")
                }
            }
            // enable debug logging (for a production application, you should set this to false).
            mHelper?.enableDebugLogging(false)

            //mHelper?.consumeAsync()
            // Start setup. This is asynchronous and the specified listener
            // will be called once setup completes.
            Log.e(TAG, "Starting setup.")
            //--Initialize In App Billing end
        } catch (e: Exception) {
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
        val size30 = getFontSize(this, 30)
        val size22 = getFontSize(this, 22)
        val size20 = getFontSize(this, 20)
        val size18 = getFontSize(this, 18)
        val size16 = getFontSize(this, 16)
        val size14 = getFontSize(this, 14)
        val size13 = getFontSize(this, 13)

        binding.txtThePlatForm.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.txtShidduchView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30)
        binding.txtDateApp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size22)
        binding.txtLookingForword.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
        binding.txtAppTour.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
        binding.txtAppTour.setPaintFlags(binding.txtAppTour.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        binding.subscriptionBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
        binding.tvAccessIncludes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size16)
        binding.tvDotOne.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size14)
        binding.tvDotTwo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size14)
        binding.tvDotThree.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size14)
        binding.tvWeOffer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvSeven.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
        binding.tvDays.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvFree.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
        binding.tvTrial.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvTheCostIs.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tv18Doller.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
        binding.tvPerMonth.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvYouMay.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)
        binding.tvCancel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
        binding.tvAtAnyTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13)

        try {
            //view Height
            val card80Height = dpToPxs(size80.toInt())
            val card20Height = dpToPxs(size20.toInt())
            binding.viewLeft.layoutParams.height = card80Height
            binding.viewRight.layoutParams.height = card80Height

            binding.ivDotOne.layoutParams.height = card20Height
            binding.ivDotOne.layoutParams.width = card20Height
            binding.ivDotTwo.layoutParams.height = card20Height
            binding.ivDotTwo.layoutParams.width = card20Height
            binding.ivDotThree.layoutParams.height = card20Height
            binding.ivDotThree.layoutParams.width = card20Height
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * all click listeners
     */
    private fun onClickFunctions() {
        binding.cancelIconClick.setOnClickListener(View.OnClickListener {
            showCommonDialog(true, getString(R.string.sure_to_logout))
        })

        binding.txtAppTour.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.putExtra(SOURCE, SHOW_TUTORIALS)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
        })

        binding.subscriptionBtn.setOnClickListener {
            //binding.subscriptionBtn.isEnabled = false

            //val intent = Intent(this, OtherScreensActivity::class.java)

            if (isNetworkAvailable(this)) {
                try {
                    // launch the gas purchase UI flow.
                    // We will be notified of completion via mPurchaseFinishedListener
                    // setWaitScreen(true)
                    if (!isPurchaseDone) {
                        Log.e(TAG, "Launching purchase flow for premium plan.")
                        val payload = "mypurchasetoken_" + AppInstance.userObj!!.getId()!!
                        try {
                            mHelper?.launchSubscriptionPurchaseFlow(
                                    this, ITEM_SKU, RC_REQUEST,
                                    mPurchaseFinishedListener, payload
                            )
                        } catch (e: IabHelper.IabAsyncInProgressException) {
                            showToast(this, getString(R.string.error_launching_purchase_flow), Toast.LENGTH_SHORT)
                        }
                    } else {
                        makeSubscriptionServiceCall(mPurchase)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                showDialogNoInternet(this, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        }
    }

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
                    .setIcon(R.drawable.ic_exit_icon, SwapdroidIcon.Visible)  //ic_star_border_black_24dp
                    .OnNegativeClicked {
                        //do nothing
                    }
                    .OnPositiveClicked {
                        if (forLogout) {
                            //User Logout Stop UXCam Session
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

    private fun finishApplication() {
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
        Log.e("SWAPVID", "finish app")
        Handler().postDelayed({
            this@SubscriptionActivity.finish()
        }, 500)
    }

    //navigate to common login
    private fun openCommonLogin() {
        //clearCallData(applicationContext)
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
        Handler().postDelayed({
            if (!isClickable) {
                isClickable = true
                addData(this, 0, "", "", "", "", 0,
                        "", "", false, false, false, "",
                        "", "", false, false, false,
                        false, "")
                PreferenceConnector.writeBoolean(this, PreferenceConnector.isRemember, false)
                val intent = Intent(this, CommonLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                this@SubscriptionActivity.finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
            }
        }, 500)
    }

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
            logFirebaseEvents("logoutWSCall", "Error Message: " + e.message)

        }
    }

    // Callback for when a purchase is finished
    private var mPurchaseFinishedListener: IabHelper.OnIabPurchaseFinishedListener =
            object : IabHelper.OnIabPurchaseFinishedListener {
                override fun onIabPurchaseFinished(
                        result: IabResult,
                        purchase: Purchase
                ) {
                    if (result.isFailure) {
                        // Handle error
                        return
                    } else if (purchase.sku == ITEM_SKU) {
                        consumeItem()
//                    binding.btnUpgrade.isEnabled = false
                    }
                }
            }

    fun consumeItem() {
        mHelper?.queryInventoryAsync(mReceivedInventoryListener)
    }

    private var mReceivedInventoryListener: IabHelper.QueryInventoryFinishedListener =
            IabHelper.QueryInventoryFinishedListener { result, inventory ->
                try {
                    Log.e(TAG, "mReceivedInventoryListener = Query inventory For Purchase finished.")
                    val payload = "" // Your developer payload
                    if (mHelper == null) return@QueryInventoryFinishedListener

                    if (result.isFailure) {
                        // Handle failure
                        Log.e(TAG, "mReceivedInventoryListener = result.isFailure")
                    } else {
                        val purchase: Purchase? = inventory.getPurchase(ITEM_SKU)
                        if (purchase == null) {
                            Log.e(TAG, "NO Available Purchase for this user")
                            return@QueryInventoryFinishedListener
                        }

                        Log.e(TAG, "SKU Details ::: ${purchase.sku} and ITEM_SKU = ${ITEM_SKU}")

                        if (verifyDeveloperPayload(purchase)) {
                            if (purchase.sku == ITEM_SKU) {
                                Log.e(TAG, "SKU match ::: PAyment Done :")
                                //call web service from here
                            } else {
                                Log.e(TAG, "SKU mismatch ::: ")
                            }
                        } else {
                            //showToast(this, getString(R.string.purchase_fail), Toast.LENGTH_SHORT)
                            Log.e(TAG, "Developer payload error:: Wrong Purchase")
                        }

                        if (purchase.sku == ITEM_SKU) {
                            Log.e(TAG, "SKU match ::: Payment Done : ${purchase.sku} and ITEM_SKU = ${ITEM_SKU}")
                            isPurchaseDone = true
                            mPurchase = purchase
                            makeSubscriptionServiceCall(purchase)
                        } else {
                            Log.e(TAG, "SKU mismatch ::: ")
                            mHelper?.consumeAsync(
                                    inventory.getPurchase(ITEM_SKU),
                                    mConsumeFinishedListener
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

    // Called when consumption is complete
    private var mConsumeFinishedListener: IabHelper.OnConsumeFinishedListener =
            IabHelper.OnConsumeFinishedListener { purchase, result ->
                try {
                    if (result.isSuccess) {
                        Log.e(TAG, "Purchase success mConsumeFinishedListener")
                        isPurchaseDone = true
                        mPurchase = purchase
                        makeSubscriptionServiceCall(purchase)
                    } else {
                        showToast(this, getString(R.string.purchase_fail), Toast.LENGTH_SHORT)
                        Log.e(TAG, "Purchase fail mConsumeFinishedListener")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast(this, getString(R.string.purchase_fail), Toast.LENGTH_SHORT)
                }
            }

    /**
     * Verifies the developer payload of a purchase.
     */
    private fun verifyDeveloperPayload(p: Purchase): Boolean {
        val payload = p.developerPayload
        Log.e(TAG, "verifyDeveloperPayload : payload = $payload")
        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */
        return true
    }

    override fun onActivityResult(
            requestCode: Int, resultCode: Int,
            data: Intent?
    ) {
        try {
            if (requestCode == RC_REQUEST) {
                if (mHelper == null) return
                if (data != null) {
                    // Pass on the activity result to the helper for handling
                    if (!mHelper?.handleActivityResult(requestCode, resultCode, data)!!) {
                        // not handled, so handle it ourselves (here's where you'd
                        // perform any handling of activity results not related to in-app
                        // billing...
                        super.onActivityResult(requestCode, resultCode, data)
                    } else {
                        Log.e(TAG, "onActivityResult handled by IABUtil.")
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //++Check Product
    private fun checkBilling() {
        try {
            //P1W = One Week
            //P1M = One Month
            //P3M = Three Month
            val billingClient = BillingClient.newBuilder(this).setListener(this).build()
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(@BillingClient.BillingResponse responseCode: Int) {
                    Log.e("SWAPLOG", "onBillingSetupFinished = $responseCode")
                    if (responseCode == BillingClient.BillingResponse.OK) {
                        Log.e("SWAPLOG", "Response Code OK")
                        val skuList = ArrayList<String>()
                        skuList.add(ITEM_SKU)
                        val params = SkuDetailsParams.newBuilder()
                        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
                        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
                            // Process the result.
                            Log.e("SWAPLOG", "querySkuDetailsAsync billingResult = $billingResult")
                            if (billingResult == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                                for (skuDetails in skuDetailsList) {
                                    val sku = skuDetails.sku
                                    val price = skuDetails.price
                                    val freeTrialPeriod = skuDetails.freeTrialPeriod
                                    val subscriptionPeriod = skuDetails.subscriptionPeriod
                                    val type = skuDetails.type

                                    if (ITEM_SKU == sku) {
                                        Log.e("SWAPLOG", "Item Found")
                                        Log.e("SWAPLOG", "price = $price, freeTrialPeriod = $freeTrialPeriod")
                                        binding.tv18Doller.text = price.replace(".00", "")
                                        break
                                    } else {
                                        Log.e("SWAPLOG", "Item Not Found")
                                    }
                                }
                            }
                        }

                    } else {
                        Log.e("SWAPLOG", "Response Code error")
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.e("SWAPLOG", "onBillingServiceDisconnected")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<com.android.billingclient.api.Purchase>?) {
        Log.e("SWAPLOG", "onPurchasesUpdated = $responseCode ,, $purchases")
    }
    //--Check Product

    //++Start Webservice
    private fun makeSubscriptionServiceCall(purchase: Purchase) {
        try {
            if (isNetworkAvailable(this)) {
                mUtilLoader.startLoader(this)
                val token = AppInstance.userObj!!.getToken()!!

                val p = RequestParams();
                p.add("userid", AppInstance.userObj!!.getId()!!)
                p.add("amount", binding.tv18Doller.text.toString())
                p.add("purchasedDate", purchase.purchaseTime)
                p.add("orderId", purchase.orderId)
                p.add("purchaseToken", purchase.token)
                p.add("autoRenewing", purchase.isAutoRenewing)
                p.add("productId", purchase.sku)
                p.add("packageName", purchase.packageName)
                p.add("device_type", DEVICE_TYPE)

                val rest = RestClient(this, RestMethod.POST, p)
                rest.setToken(token);
                rest.execute(WebConstants.APP_SUBSCRIPTION_DONE_WS);


            } else {
                mUtilLoader.stopLoader()
                showDialogNoInternet(this@SubscriptionActivity, getString(R.string.ooops), getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mUtilLoader.stopLoader()
            Log.e("SWAPLOG", e.message!!)
            showDialogNoInternet(this@SubscriptionActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
            logFirebaseEvents("makeSubscriptionServiceCall", "Error Message: " + e.message)
        }
    }
    //--End Webservice

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
        try {
            @Suppress("DEPRECATION")
            screenLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, SubscriptionActivity::class.java.simpleName)
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
            if (mHelper != null) {
                mHelper?.disposeWhenFinished()
                mHelper = null
            }
            screenLock!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        //no need to go back
    }

    private fun showSubscriptionDialog() {
        try {
            dialog = Dialog(this, R.style.myDialog)
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
            dialog.setContentView(R.layout.dialog_subscription)

            dialog.btn_ok.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
            })

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
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
                    logFirebaseEvents("logoutWSCall", "Error Message: " + e.message)
                }

            }
            WebConstants.APP_SUBSCRIPTION_DONE_WS -> {
                println("check_for_subscription_done***$responseData");
                try {
                    val response = gson.fromJson(responseData, LoginResponse::class.java);
                    mUtilLoader.stopLoader()
                    when (response.getCode()) {
                        ResponseCodes.Success -> {
                            Log.e("SWAPLOG", response.getMessage()!!)
                            //save data and navigate to welcome
//                            addData(this@SubscriptionActivity, 0, response.getData()!!.getId()!!,
//                                    response.getData()!!.getFirstName()!!, response.getData()!!.getLastName()!!, response.getData()!!.getEmail()!!, response.getData()!!.getGender()!!, response.getData()!!.getDOB()!!, response.getData()!!.getToken()!!, response.getData()!!.getIsNormalQuizComplete()!!, response.getData()!!.getIsBibleQuizComplete()!!, response.getData()!!.getIsActive()!!, "", "", "", response.getData()!!.getIsSignUpPerformed()!!, response.getData()!!.getIsBlocked()!!, response.getData()!!.getSubscriptionDone()!!, response.getData()!!.getSubscriptionFree()!!, response.getData()!!.getPhoneNo()!!)
                            //binding.viewModel!!.openOtherScreen(this@SubscriptionActivity)
                            val intent = Intent(this@SubscriptionActivity, WaitingActivity::class.java)
                            intent.putExtra(SOURCE, SIGNUP_SUCCESS)
                            intent.putExtra(SHOW_TUTORIALS, false)
                            startActivity(intent)
                            finish()
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                        ResponseCodes.ACCESS_TOKEN_EXPIRED -> expireAccessToken(this@SubscriptionActivity)
                        else -> {
                            Log.e("SWAP.LOG", response.getMessage()!!)
                            logFirebaseEvents("makeSubscriptionServiceCall", response.getMessage()!!)
                            showDialogNoInternet(this@SubscriptionActivity, response.getMessage()!!, "", R.drawable.ic_alert_icon)
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    mUtilLoader.stopLoader()
                    Log.e("SWAPLOG", e.message!!)
                    logFirebaseEvents("makeSubscriptionServiceCall", "Error Message: " + e.message)
                    showDialogNoInternet(this@SubscriptionActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                }

            }
        }
    }

    override fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?) {
        when (label) {
            WebConstants.POST_LOGOUT_WS -> {
                logFirebaseEvents("logoutWSCall", responseData!!.error_message)
            }
            WebConstants.APP_SUBSCRIPTION_DONE_WS -> {
                mUtilLoader.stopLoader()
                Log.e("SWAPLOG", responseData!!.error_message)
                showDialogNoInternet(this@SubscriptionActivity, getString(R.string.failure_response), "", R.drawable.ic_alert_icon)
                logFirebaseEvents("makeSubscriptionServiceCall", responseData!!.error_message)
            }

        }

    }
}
