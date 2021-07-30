@file:Suppress("DEPRECATION")

package shiddush.view.com.mmvsd.utill


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.BindingAdapter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import org.json.JSONObject
import shiddush.view.com.mmvsd.BuildConfig
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.model.ReferralData
import shiddush.view.com.mmvsd.model.User
import shiddush.view.com.mmvsd.model.chat.Messages
import shiddush.view.com.mmvsd.ui.commonloginscreen.CommonLoginActivity
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidColorConstants
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Sumit Kumar.
 * it contain all the common methods that are uses as per requirement
 */

var emailregex2 = "@[.*\\[]+[0-9]+:[A-Za-z0-9\\s]+[.*\\]]"
val estTimezone = "America/New_York"  ///America/Atikokan  //EST  //America/New_York

@BindingAdapter("errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String) {
    view.error = errorMessage
}

@BindingAdapter("textChangeListener")
fun setTextChangeListener(view: TextInputLayout, enabled: Boolean): Unit? {
    return view.editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.length > 0) {
                view.setErrorEnabled(false)
            } else {
                view.setErrorEnabled(true)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    })
}

@BindingAdapter("passwordChangeListener")
fun setPasswordChangeListener(view: TextInputLayout, enabled: Boolean): Unit? {
    return view.editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.length > 0) {
                view.setErrorTextAppearance(R.style.error_appearance)
                if (isPasswordValid(s.toString())) {
                    view.setErrorEnabled(false)
                } else {
                    view.setErrorEnabled(true)
                    setErrorMessage(view, "Password should contain minimun 8 characters, 1 uppercase letter, 1 lowercase letter, 1 number, 1 special character.")
                }
            } else {
                view.setErrorTextAppearance(R.style.error_appearance_gray)
                view.setErrorEnabled(true)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    })
}

fun isPasswordValid(input: CharSequence): Boolean {
    val p = Pattern.compile("^(?=.{8,})(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#\$&*]).*$", Pattern.CASE_INSENSITIVE)
    val m = p.matcher(input)
    return m.matches()
}

/*fun isValidEmail(input: CharSequence): Boolean {
    return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(input).matches()
}*/

fun isValidEmail(input: CharSequence): Boolean {
    return Pattern.compile("^([0-9a-zA-Z]*[a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z])(\\.([a-zA-Z]{2,4})){0,1}(\\.[a-zA-Z]{2,4}))\$").matcher(input).matches()
}

/*fun isValidName(input: CharSequence): Boolean {
    return Pattern.compile("(\\w+)\\s+(\\w+)").matcher(input).matches()
}*/

fun isValidName(name: String): Boolean {
    var isValid = false

    val expression = "^[a-zA-Z][^@\\n]{1,30}$"

    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(name)
    if (matcher.matches()) {
        isValid = true
    }
    return isValid
}

fun isPasswordlengthValid(input: CharSequence): Boolean {
    var isValid = false

    val expression = "^(?=.*?[A-Z]).{8,}\$"

    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(input)
    if (matcher.matches()) {
        isValid = true
    }
    return isValid
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun showToast(mContext: Context, message: String, duration: Int) {
    try {
        val toast = Toast.makeText(mContext, message, duration)
        val view = toast.view
        val text = view?.findViewById<TextView>(android.R.id.message)
        view?.background?.setColorFilter(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR), PorterDuff.Mode.SRC_IN);
        text?.setTextColor(Color.WHITE)
        text?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_icon, 0, 0, 0)
        text?.compoundDrawablePadding = 10
        toast.setGravity(Gravity.BOTTOM, 40, 40)
        toast.show()
    } catch (e: Exception) {
        e.printStackTrace()
        try {
            val toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.BOTTOM, 40, 40)
            toast.show()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

fun showDialogNoInternet(context: Context, title: String, message: String, icon: Int) {
    try {
        var messageVisible = false
        if (message.length > 1) {
            messageVisible = true
        }
        SwapdroidAlertDialog.Builder(context as Activity)
                .setTitle(title)
                .setMessage(message)
                .isMessageVisible(messageVisible)
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
                .setIcon(icon, SwapdroidIcon.Visible)  //ic_star_border_black_24dp
                .OnPositiveClicked {
                    // nothing
                }
                .build()
    } catch (e: Exception) {
        e.printStackTrace()
        try {
            val alertDialogBuilder = AlertDialog.Builder(context)
            // set message
            alertDialogBuilder.setMessage(title)
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.ok), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        } catch (ex: Exception) {
            ex.printStackTrace()
            showToast(context, title, Toast.LENGTH_LONG)
        }
    }
}

fun expireAccessToken(activity: Activity) {
    try {
        SwapdroidAlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.session_expire))
                .setMessage(activity.getString(R.string.please_login_again))
                .isMessageVisible(true)
                .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                .setNegativeBtnText(activity.getString(R.string.ok))
                .isNegativeVisible(false)
                .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                .setPositiveBtnText(activity.getString(R.string.ok))
                .isPositiveVisible(true)
                .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                .setAnimation(SwapdroidAnimation.POP)
                .isCancellable(false)
                .showCancelIcon(true)
                .setIcon(R.drawable.ic_error_icon, SwapdroidIcon.Visible)
                .OnPositiveClicked {
                    navigateToLogin(activity)
                }
                .OnCancelClicked {
                    navigateToLogin(activity)
                }
                .build()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun navigateToLogin(activity: Activity) {
    try {
        addData(activity, 0, "", "", "", "", 0, "", "", false, false, false, "", "", "", false, false, false, false,"")
        PreferenceConnector.writeBoolean(activity, PreferenceConnector.isRemember, false)
        val intent = Intent(activity, CommonLoginActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun parseError(result: String?): String {
    var errorDescription = ""
    try {
        if (result != null) {
            val jsonObjectResult = JSONObject(result)
            errorDescription = jsonObjectResult.getString("message")
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
        errorDescription = result.toString()
    }

    return errorDescription

}


fun getUserNameInitial(userName: String): String {
    var nameInitial = ""
    if (!userName.isEmpty()) {
        val name = userName.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (name.size > 1) {
            nameInitial = name[0].substring(0, 1) + name[1].substring(0, 1)
        } else if (name.size == 1) {
            nameInitial = name[0].substring(0, 1)
        }
    }
    return nameInitial.toUpperCase()
}


/**
 * This method is used to get formatted date
 *
 * @param smsTimeInMillis contains time in milliseconds
 * @return returns today, yesterday or date
 */
fun getFormattedDate(smsTimeInMillis: Long): String {
    val smsTime = Calendar.getInstance()
    smsTime.timeInMillis = smsTimeInMillis

    val now = Calendar.getInstance()

    if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
        return "Today "
    } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
        return "Yesterday "
    } else {

        val mDate = Date(smsTimeInMillis)
        val sdf = SimpleDateFormat("MM/dd", Locale.US)
        return sdf.format(mDate)
    }
}


fun getDate(date: String): String {
    try {
        val dateL = java.lang.Long.valueOf(date)!!
        val mDate = Date(dateL)
        val sdf = SimpleDateFormat("h:mm a MM/dd", Locale.US)
        return sdf.format(mDate)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return ""
}


fun getDateStemp(date: String): Date {
    val dateL = java.lang.Long.valueOf(date)!!
    return Date(dateL)
}


/*fun dpToPx(dp: Float, context: Context): Float {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
}*/

fun dpToPxs(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun convertDpToPx(dp: Int): Int {
    return (dp * (Resources.getSystem().displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun convertPxToDp(px: Int): Int {
    return (px / (Resources.getSystem().displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}


/**
 * pad -- used to change the date and month from single digit to double
 * digit value(9 to 09)
 *
 * @param c -- int value need to be pad
 * @return padded integer value
 */
fun pad(c: Int): String {
    return if (c >= 10)
        c.toString()
    else
        "0" + c.toString()
}

fun getRealPathFromURI(context: Context, contentUri: Uri): String {
    val cursor = context.contentResolver.query(contentUri, null, null, null, null)
    if (cursor == null) {
        return contentUri.path!!
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }

    return inSampleSize
}


fun setImage(imagePath: String, imageViewPatientImage: ImageView, option: DisplayImageOptions, progressBar: ProgressBar?, context: Context) {
    ImageLoader.getInstance().displayImage(imagePath.trim { it <= ' ' }, imageViewPatientImage, option, object : SimpleImageLoadingListener() {
        override fun onLoadingStarted(imageUri: String, view: View) {
            if (progressBar != null) {
                progressBar.visibility = View.VISIBLE
            }
        }

        override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {}
        override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {
            if (progressBar != null) {
                progressBar.visibility = View.GONE
            }
        }
    })
}


fun isImageFile(imageType: String): Boolean {
    return imageType.equals("image/jpeg", ignoreCase = true) || imageType.equals("image/jpg", ignoreCase = true) || imageType.equals("image/png", ignoreCase = true) || imageType.equals("image/gif", ignoreCase = true)
}


fun openBrowser(link: String, context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(intent)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}


fun hideSoftKeyboard(context: Context, et: EditText) {
    try {
        val imm = context.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }


}

fun hideVirtualKeyboard(mContext: Context) {
    try {
        val binder = (mContext as Activity).window.currentFocus!!
                .windowToken
        if (binder != null) {
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binder, 0)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun storeToken(context: Context) {
    // Get token
    // [START retrieve_current_token]
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result
        savePrefString(context, PreferenceConnector.DEVICE_TOKEN, token)
    })
    // [END retrieve_current_token]
}


/**
 * ++shared preferences
 */
//get String
fun getPrefString(context: Context, prefName: String): String {
    val name: String? = PreferenceConnector.readString(context, prefName, "")
    return name!!
}

//set String
fun savePrefString(context: Context, prefName: String, value: String) {
    PreferenceConnector.writeString(context, prefName, value)
}

//get Boolean
fun getPrefBoolean(context: Context, prefName: String): Boolean {
    val location: Boolean? = PreferenceConnector.readBoolean(context, prefName, false)
    return location!!
}

//set Boolean
fun savePrefBoolean(context: Context, prefName: String, value: Boolean) {
    PreferenceConnector.writeBoolean(context, prefName, value)
}
//get Int
fun getPrefInt(context: Context, prefName: String): Int {
    val location: Int? = PreferenceConnector.readInteger(context, prefName, 0)
    return location!!
}

//set Int
fun savePrefInt(context: Context, prefName: String, value: Int) {
    PreferenceConnector.writeInteger(context, prefName, value)
}

/**
 * --shared preferences
 */

//AddInstance
fun addData(context: Context, logintype: Int, id: String, fname: String, lname: String, email: String, gender: Int, dob: String, token: String, isNQuiz: Boolean, isBibQuiz: Boolean, isActive: Boolean, fbid: String, googleId: String, password: String, isSignUpPerformed: Boolean, isBlocked: Boolean, isSubscriptionDone: Boolean, isSubscriptionFree: Boolean, phoneNo: String) {
    val user = User()
    user.setId(id)
    user.setFirstName(fname)
    user.setLastName(lname)
    user.setEmail(email)
    user.setGender(gender)
    user.setDOB(dob)
    user.setToken(token)
    user.setLoginType(logintype)
    user.setIsNormalQuizComplete(isNQuiz)
    user.setIsBibleQuizComplete(isBibQuiz)
    user.setIsActive(isActive)
    user.setFacebookSocialId(fbid)
    user.setGoogleSocialId(googleId)
    user.setPassword(password)
    user.setIsSignUpPerformed(isSignUpPerformed)
    user.setIsBlocked(isBlocked)
    user.setIsSubscriptionDone(isSubscriptionDone)
    user.setIsSubscriptionFree(isSubscriptionFree)
    user.setPhoneNo(phoneNo)
    AppInstance.userObj = user
    //saving data into shared Preferences
    saveUserObject(context, user)
}

/**
 *  for storing user data
 */
fun saveUserObject(context: Context, list: User) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(list)
    editor.putString(PreferenceConnector.USER_DATA, json)
    editor.apply()     // This line is IMPORTANT !!!
}

/**
 *  for retrieving user data
 */
fun getUserObject(context: Context): User {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val gson = Gson()
    val json = prefs.getString(PreferenceConnector.USER_DATA, null)
    val type = object : TypeToken<User>() {

    }.type
    return gson.fromJson(json, type)
}

/**
 *  for storing user call data
 */
fun saveChatData(context: Context, list: MutableList<Messages>) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(list)
    Log.e("saveChatData",json);
    editor.putString(PreferenceConnector.CHAT_DATA, json)
    editor.apply()     // This line is IMPORTANT !!!
}

/**
 *  for retrieving call data
 */
fun getChatData(context: Context): MutableList<Messages> {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val gson = Gson()
    val json = prefs.getString(PreferenceConnector.CHAT_DATA, null)
    return gson.fromJson(json, object : TypeToken<List<Messages>>() {}.type)
}

/**
 *  for clearing call data
 */
fun clearChatData(context: Context) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    editor.putString(PreferenceConnector.CHAT_DATA, "")
    editor.apply()     // This line is IMPORTANT !!!
}

/**
 *  for storing REFERRAL data
 */
fun saveReferralData(context: Context, list: ReferralData) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(list)
    Log.e("REFERRAL_DATA",json);
    editor.putString(PreferenceConnector.BRANCH_DATA, json)
    editor.apply()     // This line is IMPORTANT !!!
}

/**
 *  for retrieving REFERRAL data
 */
fun getReferralData(context: Context): ReferralData? {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val gson = Gson()
    val json = prefs.getString(PreferenceConnector.BRANCH_DATA, null)

    val type = object : TypeToken<ReferralData>() {
    }.type
    if (json != null)
        return gson.fromJson(json, type)
    else return null
}

/**
 *  for clearing referral data
 */
fun clearReferralData(context: Context) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    editor.putString(PreferenceConnector.BRANCH_DATA, "")
    editor.apply()     // This line is IMPORTANT !!!
}

fun getHoursDifference(startDate: Date, endDate: Date): Long {
    val different = endDate.getTime() - startDate.getTime()
    val secondsInMilli = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    var elapsedHours = different / hoursInMilli
    return elapsedHours
}

/**
 *  Dynamic fonts sizes
 */
fun getFontSize(context: Context, dp: Int): Float {  //18
    //check SawpdroidAlertDialog.java getFontSize()
    var size: Float = dp.toFloat()
    try {
        //val density = context.getResources().getDisplayMetrics().densityDpi
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val density = displayMetrics.density
        val ratio = height.toFloat() / 2436 //2436 iphone X height
        var finalRatio = dp * ratio
        if (ratio < 0.8) {  //density == 2.0F &&  //&& (density >= 2.0F && density <= 3.0F)
            finalRatio = dp * 0.8F
        }
        size = finalRatio.toFloat()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return size
}

/**
 *  Dynamic youtube card view
 */
fun getYoutubeBackImageSize(context: Context): Int {
    var size = R.drawable.youtube_card_shadow_100
    try {
        val density = context.getResources().getDisplayMetrics().densityDpi
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        when (density) {
            DisplayMetrics.DENSITY_140 -> {
                size = R.drawable.youtube_card_shadow_60
            }
            DisplayMetrics.DENSITY_180 -> {
                size = R.drawable.youtube_card_shadow_70
            }
            DisplayMetrics.DENSITY_200 -> {
                size = R.drawable.youtube_card_shadow_70
            }
            DisplayMetrics.DENSITY_220 -> {
                size = R.drawable.youtube_card_shadow_80
            }
            DisplayMetrics.DENSITY_260 -> {
                size = R.drawable.youtube_card_shadow_80
            }
            DisplayMetrics.DENSITY_280 -> {
                size = R.drawable.youtube_card_shadow_90
            }
            DisplayMetrics.DENSITY_300 -> {
                size = R.drawable.youtube_card_shadow_90
            }
            DisplayMetrics.DENSITY_340 -> {
                size = R.drawable.youtube_card_shadow_90
            }
            DisplayMetrics.DENSITY_360 -> {
                size = R.drawable.youtube_card_shadow_100
            }
            DisplayMetrics.DENSITY_400 -> {
                size = R.drawable.youtube_card_shadow_100
            }
            DisplayMetrics.DENSITY_420 -> {
                size = R.drawable.youtube_card_shadow_100
            }
            DisplayMetrics.DENSITY_440 -> {
                size = R.drawable.youtube_card_shadow_100
            }
            DisplayMetrics.DENSITY_560 -> {
                size = R.drawable.youtube_card_shadow_120
            }
            DisplayMetrics.DENSITY_600 -> {
                size = R.drawable.youtube_card_shadow_120
            }

            //regular
            DisplayMetrics.DENSITY_LOW -> {
                size = R.drawable.youtube_card_shadow_60
            }
            DisplayMetrics.DENSITY_MEDIUM -> {
                size = R.drawable.youtube_card_shadow_70
            }
            DisplayMetrics.DENSITY_TV -> {
                size = R.drawable.youtube_card_shadow_80
            }
            DisplayMetrics.DENSITY_HIGH -> {
                size = R.drawable.youtube_card_shadow_90
            }
            DisplayMetrics.DENSITY_XHIGH -> {
                size = R.drawable.youtube_card_shadow_90
            }
            DisplayMetrics.DENSITY_XXHIGH -> {
                size = R.drawable.youtube_card_shadow_100
            }
            DisplayMetrics.DENSITY_XXXHIGH -> {
                size = R.drawable.youtube_card_shadow_120
            }
            else -> {
                size = R.drawable.youtube_card_shadow_100
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return size
}

/**
 *  Dynamic card view for 100 coener width
 */
fun getBackImageSize(context: Context): Int {
    var size = R.drawable.swap_card_shadow_100
    try {
        val density = context.getResources().getDisplayMetrics().densityDpi
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        when (density) {
            DisplayMetrics.DENSITY_140 -> {
                size = R.drawable.swap_card_shadow_60
            }
            DisplayMetrics.DENSITY_180 -> {
                size = R.drawable.swap_card_shadow_70
            }
            DisplayMetrics.DENSITY_200 -> {
                size = R.drawable.swap_card_shadow_70
            }
            DisplayMetrics.DENSITY_220 -> {
                size = R.drawable.swap_card_shadow_80
            }
            DisplayMetrics.DENSITY_260 -> {
                size = R.drawable.swap_card_shadow_80
            }
            DisplayMetrics.DENSITY_280 -> {
                size = R.drawable.swap_card_shadow_90
            }
            DisplayMetrics.DENSITY_300 -> {
                size = R.drawable.swap_card_shadow_90
            }
            DisplayMetrics.DENSITY_340 -> {
                size = R.drawable.swap_card_shadow_90
            }
            DisplayMetrics.DENSITY_360 -> {
                size = R.drawable.swap_card_shadow_100
            }
            DisplayMetrics.DENSITY_400 -> {
                size = R.drawable.swap_card_shadow_100
            }
            DisplayMetrics.DENSITY_420 -> {
                size = R.drawable.swap_card_shadow_100
            }
            DisplayMetrics.DENSITY_440 -> {
                size = R.drawable.swap_card_shadow_100
            }
            DisplayMetrics.DENSITY_560 -> {
                size = R.drawable.swap_card_shadow_120
            }
            DisplayMetrics.DENSITY_600 -> {
                size = R.drawable.swap_card_shadow_120
            }

            //regular
            DisplayMetrics.DENSITY_LOW -> {
                size = R.drawable.swap_card_shadow_60
            }
            DisplayMetrics.DENSITY_MEDIUM -> {
                size = R.drawable.swap_card_shadow_70
            }
            DisplayMetrics.DENSITY_TV -> {
                size = R.drawable.swap_card_shadow_80
            }
            DisplayMetrics.DENSITY_HIGH -> {
                size = R.drawable.swap_card_shadow_90
            }
            DisplayMetrics.DENSITY_XHIGH -> {
                size = R.drawable.swap_card_shadow_90
            }
            DisplayMetrics.DENSITY_XXHIGH -> {
                size = R.drawable.swap_card_shadow_100
            }
            DisplayMetrics.DENSITY_XXXHIGH -> {
                size = R.drawable.swap_card_shadow_120
            }
            else -> {
                size = R.drawable.swap_card_shadow_100
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return size
}

/**
 *  Dynamic card view for 80 coener width
 */
fun getBackImage80Size(context: Context): Int {
    var size = R.drawable.swap_card_shadow_80
    try {
        val density = context.getResources().getDisplayMetrics().densityDpi
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        when (density) {
            DisplayMetrics.DENSITY_140 -> {
                size = R.drawable.swap_card_shadow_40
            }
            DisplayMetrics.DENSITY_180 -> {
                size = R.drawable.swap_card_shadow_50
            }
            DisplayMetrics.DENSITY_200 -> {
                size = R.drawable.swap_card_shadow_50
            }
            DisplayMetrics.DENSITY_220 -> {
                size = R.drawable.swap_card_shadow_60
            }
            DisplayMetrics.DENSITY_260 -> {
                size = R.drawable.swap_card_shadow_60
            }
            DisplayMetrics.DENSITY_280 -> {
                size = R.drawable.swap_card_shadow_70
            }
            DisplayMetrics.DENSITY_300 -> {
                size = R.drawable.swap_card_shadow_70
            }
            DisplayMetrics.DENSITY_340 -> {
                size = R.drawable.swap_card_shadow_70
            }
            DisplayMetrics.DENSITY_360 -> {
                size = R.drawable.swap_card_shadow_80
            }
            DisplayMetrics.DENSITY_400 -> {
                size = R.drawable.swap_card_shadow_80
            }
            DisplayMetrics.DENSITY_420 -> {
                size = R.drawable.swap_card_shadow_80
            }
            DisplayMetrics.DENSITY_440 -> {
                size = R.drawable.swap_card_shadow_80
            }
            DisplayMetrics.DENSITY_560 -> {
                size = R.drawable.swap_card_shadow_100
            }
            DisplayMetrics.DENSITY_600 -> {
                size = R.drawable.swap_card_shadow_100
            }

            //regular
            DisplayMetrics.DENSITY_LOW -> {
                size = R.drawable.swap_card_shadow_40
            }
            DisplayMetrics.DENSITY_MEDIUM -> {
                size = R.drawable.swap_card_shadow_50
            }
            DisplayMetrics.DENSITY_TV -> {
                size = R.drawable.swap_card_shadow_60
            }
            DisplayMetrics.DENSITY_HIGH -> {
                size = R.drawable.swap_card_shadow_70
            }
            DisplayMetrics.DENSITY_XHIGH -> {
                size = R.drawable.swap_card_shadow_70
            }
            DisplayMetrics.DENSITY_XXHIGH -> {
                size = R.drawable.swap_card_shadow_80
            }
            DisplayMetrics.DENSITY_XXXHIGH -> {
                size = R.drawable.swap_card_shadow_100
            }
            else -> {
                size = R.drawable.swap_card_shadow_80
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return size
}

/**
 *  Dynamic card view for 60 coener width
 */
fun getBackImage60Size(context: Context): Int {
    var size = R.drawable.swap_card_shadow_60
    try {
        val density = context.getResources().getDisplayMetrics().densityDpi
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        when (density) {
            DisplayMetrics.DENSITY_140 -> {
                size = R.drawable.swap_card_shadow_20
            }
            DisplayMetrics.DENSITY_180 -> {
                size = R.drawable.swap_card_shadow_30
            }
            DisplayMetrics.DENSITY_200 -> {
                size = R.drawable.swap_card_shadow_30
            }
            DisplayMetrics.DENSITY_220 -> {
                size = R.drawable.swap_card_shadow_40
            }
            DisplayMetrics.DENSITY_260 -> {
                size = R.drawable.swap_card_shadow_40
            }
            DisplayMetrics.DENSITY_280 -> {
                size = R.drawable.swap_card_shadow_50
            }
            DisplayMetrics.DENSITY_300 -> {
                size = R.drawable.swap_card_shadow_50
            }
            DisplayMetrics.DENSITY_340 -> {
                size = R.drawable.swap_card_shadow_50
            }
            DisplayMetrics.DENSITY_360 -> {
                size = R.drawable.swap_card_shadow_60
            }
            DisplayMetrics.DENSITY_400 -> {
                size = R.drawable.swap_card_shadow_60
            }
            DisplayMetrics.DENSITY_420 -> {
                size = R.drawable.swap_card_shadow_60
            }
            DisplayMetrics.DENSITY_440 -> {
                size = R.drawable.swap_card_shadow_60
            }
            DisplayMetrics.DENSITY_560 -> {
                size = R.drawable.swap_card_shadow_80
            }
            DisplayMetrics.DENSITY_600 -> {
                size = R.drawable.swap_card_shadow_80
            }

            //regular
            DisplayMetrics.DENSITY_LOW -> {
                size = R.drawable.swap_card_shadow_20
            }
            DisplayMetrics.DENSITY_MEDIUM -> {
                size = R.drawable.swap_card_shadow_30
            }
            DisplayMetrics.DENSITY_TV -> {
                size = R.drawable.swap_card_shadow_40
            }
            DisplayMetrics.DENSITY_HIGH -> {
                size = R.drawable.swap_card_shadow_50
            }
            DisplayMetrics.DENSITY_XHIGH -> {
                size = R.drawable.swap_card_shadow_50
            }
            DisplayMetrics.DENSITY_XXHIGH -> {
                size = R.drawable.swap_card_shadow_60
            }
            DisplayMetrics.DENSITY_XXXHIGH -> {
                size = R.drawable.swap_card_shadow_80
            }
            else -> {
                size = R.drawable.swap_card_shadow_60
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return size
}

/**
 *  Dynamic card view for 40 coener width
 */
fun getBackImage40Size(context: Context): Int {
    var size = R.drawable.swap_card_shadow_40
    try {
        val density = context.getResources().getDisplayMetrics().densityDpi
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        when (density) {
            DisplayMetrics.DENSITY_140 -> {
                size = R.drawable.swap_card_shadow_10
            }
            DisplayMetrics.DENSITY_180 -> {
                size = R.drawable.swap_card_shadow_10
            }
            DisplayMetrics.DENSITY_200 -> {
                size = R.drawable.swap_card_shadow_10
            }
            DisplayMetrics.DENSITY_220 -> {
                size = R.drawable.swap_card_shadow_20
            }
            DisplayMetrics.DENSITY_260 -> {
                size = R.drawable.swap_card_shadow_20
            }
            DisplayMetrics.DENSITY_280 -> {
                size = R.drawable.swap_card_shadow_30
            }
            DisplayMetrics.DENSITY_300 -> {
                size = R.drawable.swap_card_shadow_30
            }
            DisplayMetrics.DENSITY_340 -> {
                size = R.drawable.swap_card_shadow_30
            }
            DisplayMetrics.DENSITY_360 -> {
                size = R.drawable.swap_card_shadow_40
            }
            DisplayMetrics.DENSITY_400 -> {
                size = R.drawable.swap_card_shadow_40
            }
            DisplayMetrics.DENSITY_420 -> {
                size = R.drawable.swap_card_shadow_40
            }
            DisplayMetrics.DENSITY_440 -> {
                size = R.drawable.swap_card_shadow_40
            }
            DisplayMetrics.DENSITY_560 -> {
                size = R.drawable.swap_card_shadow_60
            }
            DisplayMetrics.DENSITY_600 -> {
                size = R.drawable.swap_card_shadow_60
            }

            //regular
            DisplayMetrics.DENSITY_LOW -> {
                size = R.drawable.swap_card_shadow_10
            }
            DisplayMetrics.DENSITY_MEDIUM -> {
                size = R.drawable.swap_card_shadow_20
            }
            DisplayMetrics.DENSITY_TV -> {
                size = R.drawable.swap_card_shadow_20
            }
            DisplayMetrics.DENSITY_HIGH -> {
                size = R.drawable.swap_card_shadow_30
            }
            DisplayMetrics.DENSITY_XHIGH -> {
                size = R.drawable.swap_card_shadow_30
            }
            DisplayMetrics.DENSITY_XXHIGH -> {
                size = R.drawable.swap_card_shadow_40
            }
            DisplayMetrics.DENSITY_XXXHIGH -> {
                size = R.drawable.swap_card_shadow_60
            }
            else -> {
                size = R.drawable.swap_card_shadow_40
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return size
}



/**
 *  provide waiting screen timer
 */
fun getTimerTime(getDate: String, getTime: String, getDay: String): Long {
    //time in milliseconds
    var timeInMilli: Long = 0
    try {

        //times
        val SundayTime = "14:30:00"
        val MondayTime = "17:30:00"
        val TuesdayTime = "21:30:00"
        val WednesdayTime = "21:30:00"
        val ThursdayTime = "17:30:00"

        System.out.println("SWAPEST current $getDate $getTime $getDay")

        //get current and original date time
        var currentDT: String = ""
        var originalDT: String = ""

        when (getDay) {
            "Sunday" -> {
                System.out.println("SWAPEST original $getDate $SundayTime $getDay")
                if (checkTimeisValid(getTime, SundayTime)) {
                    currentDT = "$getDate $getTime"
                    originalDT = "$getDate $SundayTime"
                } else {
                    currentDT = "$getDate $getTime"
                    originalDT = getNextDate(getDate, 1) + " " + MondayTime
                }
            }
            "Monday" -> {
                System.out.println("SWAPEST original $getDate $MondayTime $getDay")
                if (checkTimeisValid(getTime, MondayTime)) {
                    currentDT = "$getDate $getTime"
                    originalDT = "$getDate $MondayTime"
                } else {
                    currentDT = "$getDate $getTime"
                    originalDT = getNextDate(getDate, 1) + " " + TuesdayTime
                }
            }
            "Tuesday" -> {
                System.out.println("SWAPEST original $getDate $TuesdayTime $getDay")
                if (checkTimeisValid(getTime, TuesdayTime)) {
                    currentDT = "$getDate $getTime"
                    originalDT = "$getDate $TuesdayTime"
                } else {
                    currentDT = "$getDate $getTime"
                    originalDT = getNextDate(getDate, 1) + " " + WednesdayTime
                }
            }
            "Wednesday" -> {
                System.out.println("SWAPEST original $getDate $WednesdayTime $getDay")
                if (checkTimeisValid(getTime, WednesdayTime)) {
                    currentDT = "$getDate $getTime"
                    originalDT = "$getDate $WednesdayTime"
                } else {
                    currentDT = "$getDate $getTime"
                    originalDT = getNextDate(getDate, 1) + " " + ThursdayTime
                }
            }
            "Thursday" -> {
                System.out.println("SWAPEST original $getDate $ThursdayTime $getDay")
                if (checkTimeisValid(getTime, ThursdayTime)) {
                    currentDT = "$getDate $getTime"
                    originalDT = "$getDate $ThursdayTime"
                } else {
                    currentDT = "$getDate $getTime"
                    originalDT = getNextDate(getDate, 3) + " " + SundayTime
                }
            }
            "Friday" -> {
                System.out.println("SWAPEST original $getDate $SundayTime $getDay")
                currentDT = "$getDate $getTime"
                originalDT = getNextDate(getDate, 2) + " " + SundayTime
            }
            "Saturday" -> {
                System.out.println("SWAPEST original $getDate $SundayTime $getDay")
                currentDT = "$getDate $getTime"
                originalDT = getNextDate(getDate, 1) + " " + SundayTime
            }
        }

        //set time in milliseconds
        System.out.println("SWAPEST currentDT $currentDT , orignalDT $originalDT")
        timeInMilli = getDifferenceInTime(currentDT, originalDT)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return timeInMilli
}

/**
 *  provide next date
 */
fun getNextDate(currentDate: String, additionalDate: Int): String {
    var nextDate = currentDate
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone(estTimezone)
        val calendar: Calendar = Calendar.getInstance()
        //val today = calendar.getTime()
        calendar.add(Calendar.DAY_OF_YEAR, additionalDate)
        val tomorrow = calendar.getTime()
        //val todayAsString = dateFormat.format(today)
        nextDate = dateFormat.format(tomorrow)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return nextDate
}

/**
 *  check time is passed or not
 */
fun checkTimeisValid(currTime: String, origTime: String): Boolean {
    try {
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        timeFormat.timeZone = TimeZone.getTimeZone(estTimezone)
        val CTIME = timeFormat.parse(currTime)
        val OTIME = timeFormat.parse(origTime)
        return CTIME.before(OTIME)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

/**
 *  check time difference
 */
fun getDifferenceInTime(currDateTimeTime: String, origDateTime: String): Long {
    //time in milliseconds
    var timeInMilli: Long = 0
    try {
        //date formatter
        val DTFORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        DTFORMAT.timeZone = TimeZone.getTimeZone(estTimezone)
        //convert current date in milliseconds
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val currentDate: Date = DTFORMAT.parse(currDateTimeTime)
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val originalDate: Date = DTFORMAT.parse(origDateTime)
        val currentDateInMili = currentDate.time
        val originalDateInMili = originalDate.time
        System.out.println("current Date in milliseconds: $currentDateInMili")
        System.out.println("original Date in milliseconds: $originalDateInMili")
        timeInMilli = originalDateInMili - currentDateInMili
        System.out.println("Final in milliseconds: $originalDateInMili")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return timeInMilli
}


/**
 *  Return app is in background or not
 *
 *  @param context : context of app
 *  @return isInBackground true or false
 */
@SuppressLint("NewApi")
fun isAppIsInBackground(context: Context): Boolean {
    var isInBackground = true
    try {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity!!
            if (componentInfo!!.packageName == context.packageName) {
                isInBackground = false
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return isInBackground
}

/**
 *  Dynamic view height
 */
fun getPercentHeightOfDevice(context: Context, reqHeight: Float): Int {
    var size: Int = dpToPxs(50)
    try {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        //val width = displayMetrics.widthPixels
        // size in pixels
        size = (height * reqHeight).toInt()
        Log.e("SWAPSIZE", "Cal height Req = $reqHeight in px = $size")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return size
}

/**
 *  this function is use to clear cache memory only from WaitingActivity
 */
fun Context.clearCache() {
    try {
        val dir = this.cacheDir
        dir.deleteCache()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun File.deleteCache(): Boolean {
    try {
        if (this != null && this.isDirectory) {
            val children = this.list()
            for (element in children) {
                val success = File(this, element).deleteCache()
                if (!success) {
                    return false
                }
            }
            return this.delete()
        } else if (this != null && this.isFile) {
            return this.delete()
        } else {
            return false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}


/**
 *  show subscription cancel dialog
 */
fun showSubscriptionCancelDialog(context: Context, message: String) {
    try {
        SwapdroidAlertDialog.Builder(context as Activity)
                .setTitle(context.getString(R.string.ooops))
                .setMessage(message)
                .isMessageVisible(true)
                .setNegativeBtnText(context.getString(R.string.ok))
                .isNegativeVisible(false)
                .setPositiveBtnText(context.getString(R.string.ok))
                .isPositiveVisible(true)
                .setAnimation(SwapdroidAnimation.POP)
                .isCancellable(false)
                .showCancelIcon(false)
                .setIcon(R.drawable.play_store_icon, SwapdroidIcon.Visible)
                .OnPositiveClicked {
                    try {
                        val intent = Intent(
                                "android.intent.action.VIEW",
                                Uri.parse("https://play.google.com/store/account/subscriptions?sku=${ITEM_SKU}&package=${BuildConfig.APPLICATION_ID}")
                        )
                        context.startActivity(intent)
                        context.finish()
                    } catch (e: Exception) {
                        try {
                            val intent = Intent(
                                    "android.intent.action.VIEW",
                                    Uri.parse("https://play.google.com/store/account/subscriptions")
                            )
                            context.startActivity(intent)
                            context.finish()
                        } catch (e: Exception) {
                        }
                    }
                }
                .build()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


/**
 *  provide device information
 */
fun getDeviceInfo(): String {
    var deviceInfo: String = ""
    try {
        deviceInfo = "Model: ${Build.MODEL}, Device: ${Build.DEVICE}, Manufacturer: ${Build.MANUFACTURER}, Board: ${Build.BOARD}, Brand: ${Build.BRAND}, Serial: ${Build.SERIAL}"
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return deviceInfo
}
