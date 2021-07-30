package shiddush.view.com.mmvsd.socket


import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import shiddush.view.com.mmvsd.radioplayer.RadioPlayer
import shiddush.view.com.mmvsd.utill.PreferenceConnector
import shiddush.view.com.mmvsd.utill.savePrefBoolean

/**
 * Created by Sumit Kumar.
 * this Service to start socket connection and disconnection statuses
 *
 * below code for AndroidManifest.xml file
 *
<service
android:enabled="true"
android:name=".socket.MySocketConnectionService"
android:exported="false"
android:stopWithTask="false" />
 */
class MySocketConnectionService : Service() {

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        //stop service
        this.stopSelf()
        Log.e("SWAPLOGSOCKET", "socket onTaskRemoved")
        //disconnect socket
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

        //stop radio
        try {
            RadioPlayer.stopRadio()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}