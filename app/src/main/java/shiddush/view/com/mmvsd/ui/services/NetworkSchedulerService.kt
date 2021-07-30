package shiddush.view.com.mmvsd.ui.services

import android.app.Activity
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.tapadoo.alerter.Alerter
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.ui.recievers.ConnectivityReceiver
import shiddush.view.com.mmvsd.ui.recievers.ConnectivityReceiver.ConnectivityReceiverListener
import shiddush.view.com.mmvsd.utill.IS_INTERNET_CONNECTED

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NetworkSchedulerService : JobService(), ConnectivityReceiverListener {
    private var mConnectivityReceiver: ConnectivityReceiver? = null
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service created")
        mConnectivityReceiver = ConnectivityReceiver(this)
    }

    /**
     * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")
        return Service.START_NOT_STICKY
    }

    override fun onStartJob(params: JobParameters): Boolean {
        Log.i(TAG, "onStartJob$mConnectivityReceiver")
        registerReceiver(
            mConnectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.i(TAG, "onStopJob")
        unregisterReceiver(mConnectivityReceiver)
        return true
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        IS_INTERNET_CONNECTED = isConnected

        val message =
            if (isConnected) "Good! Connected to Internet" else "Sorry! Not connected to internet"
        if (activity != null)
            onNetworkConnectionListener.onNetworkListener(isConnected)
        if (!isConnected)
            noInternetMessage()
    }

    private fun noInternetMessage() {
        if (activity != null) {
            Alerter.create(activity)
                .setTitle(getString(R.string.no_internet))
                .setText(getString(R.string.please_check_internet))
                .setBackgroundColorRes(R.color.colorRedLight)
                .setDuration(5000)
                .show()
        }
    }

    interface onNetworkConnectionListener {

        fun onNetworkListener(isConnected: Boolean)
    }


    companion object {
        fun registerForNetworkService(
            activity: Activity,
            onNetworkConnectionListener: onNetworkConnectionListener
        ) {
            this.activity = activity
            this.onNetworkConnectionListener = onNetworkConnectionListener
        }

        private lateinit var onNetworkConnectionListener: onNetworkConnectionListener
        private var activity: Activity? = null
        private val TAG = NetworkSchedulerService::class.java.simpleName
    }
}