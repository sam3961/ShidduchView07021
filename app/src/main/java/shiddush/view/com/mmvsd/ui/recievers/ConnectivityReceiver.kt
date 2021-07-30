package shiddush.view.com.mmvsd.ui.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class ConnectivityReceiver(private val mConnectivityReceiverListener: ConnectivityReceiverListener) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        mConnectivityReceiverListener.onNetworkConnectionChanged(isConnected(context))
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        fun isConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
    }

}