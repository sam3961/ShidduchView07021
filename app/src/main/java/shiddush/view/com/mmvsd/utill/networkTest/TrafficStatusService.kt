package shiddush.view.com.mmvsd.utill.networkTest

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.tapadoo.alerter.Alerter
import com.tapadoo.alerter.OnHideAlertListener
import com.tapadoo.alerter.OnShowAlertListener
import java.text.SimpleDateFormat
import java.util.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.ui.Splash
import shiddush.view.com.mmvsd.ui.services.NetworkSchedulerService

class TrafficStatusService : IntentService("TrafficStatusService"), LifecycleOwner {

    private var timer: Timer = Timer()
    private val DELAY = 0L
    private val DURATION = 1000L
    lateinit var notificationBuilder: Notification.Builder
    lateinit var notificationManager: NotificationManager
    private var isMessageShowing = false

    val registry: LifecycleRegistry = LifecycleRegistry(this)

    lateinit var notificationLayout: RemoteViews

    override fun onHandleIntent(intent: Intent?) {

    }

    override fun onCreate() {
        super.onCreate()

        registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            createNotification()
        }

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                var downloadSpeed = TrafficUtils.getNetworkSpeed()

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    showNotificationIfEnabled(downloadSpeed)
                }
            }
        }, DELAY, DURATION)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showNotificationIfEnabled(downloadSpeed: String) {
        val speed: String = (downloadSpeed.subSequence(0, downloadSpeed.indexOf(" ") + 1)).toString()

        if (speed.toFloat() != 0F) {
            startForeground(NOTIFICATION_ID, notificationBuilder.build())
            updateNotification(downloadSpeed)
        } else {
            stopForeground(true)
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateNotification(downloadSpeed: String) {
        val speed = downloadSpeed.subSequence(0, downloadSpeed.indexOf(" ") + 1)
        val units = downloadSpeed.subSequence(downloadSpeed.indexOf(" ") + 1, downloadSpeed.length)

        val bitmap = ImageUtils.createBitmapFromString(speed.toString(), units.toString())
        val icon = Icon.createWithBitmap(bitmap)
        notificationBuilder.setSmallIcon(icon)

        // set notification details : Speed, mobile and wifi usage
        notificationLayout.setTextViewText(R.id.custom_notification_speed_tv, "$downloadSpeed/s")


        if (!isMessageShowing && units == "KB" && speed.toString().trim().toInt() < 30 && activity != null) {
            Alerter.create(activity)
                    .setTitle(getString(R.string.slow_internet))
                    .setText(getString(R.string.please_check_slow_internet))
                    .setBackgroundColorRes(R.color.colorRedLight)
                    .setDuration(10000)
                    .setOnHideListener(OnHideAlertListener {
                        isMessageShowing = false
                    })
                    .setOnShowListener(OnShowAlertListener {
                        isMessageShowing = true
                    })
                    .show()

        }


        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = Notification.Builder(this, "default")
        } else {
            @Suppress("DEPRECATION")
            notificationBuilder = Notification.Builder(this)
        }

        notificationBuilder.setContentTitle("")
        notificationBuilder.setSmallIcon(Icon.createWithBitmap(ImageUtils.createBitmapFromString("0", " KB")))
        notificationBuilder.setVisibility(Notification.VISIBILITY_PUBLIC)
        notificationBuilder.setOngoing(true)
        notificationBuilder.setAutoCancel(true)
        setNotificationContent()
        notificationBuilder.setContentIntent(createPendingIntent())

    }

    private fun setNotificationContent() {
        notificationLayout = RemoteViews(packageName, R.layout.custom_notification_view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder.setCustomContentView(notificationLayout)
        } else {
            notificationBuilder.setContent(notificationLayout)
        }
    }


    private fun createPendingIntent(): PendingIntent? {
        var intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        var pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        return pendingIntent
    }

    companion object {
        fun registerForTrafficService(activity: Activity) {
            this.activity = activity
        }

        val NOTIFICATION_ID = 101
        private var activity: Activity? = null

    }
}