import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.provider.Settings
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import shiddush.view.com.mmvsd.R

object CommonFunctions {
    var mDialogProgress: AlertDialog? = null




    fun showFeedbackMessage(view: View, message: String) {
        val snakbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snakbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            .setTextColor(Color.WHITE)
        snakbar.view.setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                R.color.purple
            )
        )
        if (snakbar.isShown) {
            snakbar.dismiss()
        }
        snakbar.show()
    }


    fun getDeviceId(context: Context): String? {
        var deviceId: String? = ""
        try {
            deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return deviceId
    }



    fun hideKeyBoard(activity: Context, view: View) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /* fun loadCircularImage(context:Context,imageUrl:String,circularImageView:CircleImageView){
         var requestOptions = RequestOptions()
         requestOptions.placeholder( R.drawable.ic_user_placeholder)
         Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(circularImageView)
     }*/
}