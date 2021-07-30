package shiddush.view.com.mmvsd.ui.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TimeChangeReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.e("BroadcastReceiver", "================ TimeChangeReceiver")

    }


}