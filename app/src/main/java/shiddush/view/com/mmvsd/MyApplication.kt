package shiddush.view.com.mmvsd

import android.annotation.TargetApi
import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.multidex.MultiDex
import io.branch.referral.Branch
import org.json.JSONObject
import shiddush.view.com.mmvsd.notification.MyFirebaseMessagingService
import shiddush.view.com.mmvsd.notification.NotificationActivity
import shiddush.view.com.mmvsd.socket.MySocketConnectionService
import shiddush.view.com.mmvsd.ui.Splash
import shiddush.view.com.mmvsd.utill.PreferenceConnector
import shiddush.view.com.mmvsd.utill.getPrefBoolean
import java.util.*


/**
 * Created by Sumit Kumar
 * The class will start once the application will start and will set the Splunk Key for handling
 *
 * Remove app from recent apps
 * https://stackoverflow.com/questions/22166282/close-application-and-remove-from-recent-apps
 * https://fabric.io/shidduch-view-llc/android/apps/shiddush.view.com.mmvsd/
 */

class MyApplication : Application(), LifecycleObserver {

    //var mSocket: Socket? = null


    override fun onCreate() {
        super.onCreate()
        mInstance = this;
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        MultiDex.install(this)
        // Branch logging for debugging
        Branch.enableLogging();

        // Branch object initialization
        Branch.getAutoInstance(this);

        try {
            //Service to start socket connection and disconnection statuses
            val intent = Intent(this, MySocketConnectionService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
            startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

           }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    companion object {
        lateinit var mInstance: MyApplication
        var isAppInBackground: Boolean = true
        var mLiveAnyChatNotification = MutableLiveData<String?>()
        fun getContext(): Context? {
            return mInstance.applicationContext
        }
    }
    /*fun getSocket(): Socket {
        try {
            if (mSocket == null) {
                try {
                    mSocket = IO.socket(WebConstants.SOCKET_URL)
                    Log.e("SWAPLOGSOCKET", "socket call")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mSocket!!
    }*/


    override fun onTerminate() {
        super.onTerminate()
        Log.e("SWAPLOGSOCKET", "socket onTerminate")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        isAppInBackground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        isAppInBackground = false

    }
}
