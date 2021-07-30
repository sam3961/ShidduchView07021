package shiddush.view.com.mmvsd.notification

/**
 * Created by Sumit Kumar
 */

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import shiddush.view.com.mmvsd.BuildConfig
import shiddush.view.com.mmvsd.MyApplication
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.ui.Splash
import shiddush.view.com.mmvsd.ui.chat.ChatActivity
import shiddush.view.com.mmvsd.ui.waitingscreen.WaitingActivity
import shiddush.view.com.mmvsd.utill.*
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     * https://github.com/firebase/quickstart-android
     * https://android.jlelse.eu/android-push-notification-using-firebase-and-advanced-rest-client-3858daff2f50
     * http://pushtry.com/
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        var notificationCall = false

        try {
            // TODO(developer): Handle FCM messages here.
            // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
            Log.e(TAG, "From: ${remoteMessage?.from}")


            val dataBody: Map<String, String> = remoteMessage.data

            System.out.println("Message Received DATA ====> $dataBody")

            //{notificationFor=MESSAGE, notifyType=You have received a new message,
            // user_id=5f100453b7c5cc226ef2970c, sound=default.caf,
            // title=INCOMING MESSAGE, user_name=Scarlett}

            //{notificationFor=Alert, notifyType=Testing message,
            // time=8:10 PM, sound=ssm.caf, title=Testing title, alert_message=Alert box message}
            var body = ""
            var timeSlot = ""
            var user_id = ""
            var user_name = ""
            var rejoin_type = ""
            var title = dataBody.get("title") as String
            val type = dataBody.get("notificationFor") as String
            if (dataBody.containsKey("alert_message"))
                body = dataBody.get("alert_message") as String
            if (dataBody.containsKey("notifyType"))
                body = dataBody.get("notifyType") as String
            if (dataBody.containsKey("type"))
                rejoin_type = dataBody.get("type") as String
            if (dataBody.containsKey("notifyType"))
                body = dataBody.get("notifyType") as String
            if (dataBody.containsKey("time"))
                timeSlot = dataBody.get("time") as String
            if (dataBody.containsKey("user_name"))
                user_name = dataBody.get("user_name") as String
            if (dataBody.containsKey("user_id"))
                user_id = dataBody.get("user_id") as String

            val notificationSound = dataBody.get("sound") as String
            val sound = notificationSound.substringBefore(".")
            val channelId = notificationSound.substringBefore(".")



            System.out.println("Message Received Title ====> $title")
            System.out.println("Message Received Body ====> $body")
            System.out.println("Message Received Sound ====> $sound")
            System.out.println("Message Received ChannelId ====> $channelId")

            title = if (title.isEmpty()) {
                getString(R.string.app_name)
            } else {
                title
            }
            body = if (body.isEmpty()) {
                getString(R.string.sub_title)
            } else {
                body
            }

            //check app instance
            var userid = ""
            if (AppInstance.userObj != null && AppInstance.userObj!!.getId() != null && !AppInstance.userObj!!.getId()
                    ?.isEmpty()!!
            )
                userid = AppInstance.userObj!!.getId()!!
            if (userid.length > 1) {
                notificationCall = true
                sendNotification(
                    title,
                    body,
                    sound,
                    channelId,
                    type,
                    timeSlot,
                    user_name,
                    user_id,
                    rejoin_type
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            if (!notificationCall) {
                var user_name = ""
                var user_id = ""
                var timeSlot = ""
                var message = ""
                var rejoin_type = ""
                val dataBody: Map<String, String> = remoteMessage.data
                var title = dataBody.get("title") as String
                val type = dataBody.get("notificationFor") as String

                if (dataBody.containsKey("alert_message"))
                    message = dataBody.get("alert_message") as String

                if (dataBody.containsKey("notifyType"))
                    message = dataBody.get("notifyType") as String

                if (dataBody.containsKey("type"))
                    rejoin_type = dataBody.get("type") as String

                if (dataBody.containsKey("user_name"))
                    user_name = dataBody.get("user_name") as String

                if (dataBody.containsKey("user_id"))
                    user_id = dataBody.get("user_id") as String

                if (dataBody.containsKey("time"))
                    timeSlot = dataBody.get("time") as String

                val notiSound = dataBody.get("sound") as String
                val sound = notiSound.substringBefore(".")
                val channelId = notiSound.substringBefore(".")

                title = if (title!!.isEmpty()) {
                    getString(R.string.app_name)
                } else {
                    title!!
                }
                message = if (message!!.isEmpty()) {
                    getString(R.string.sub_title)
                } else {
                    message!!
                }
                Log.e(TAG, "Message Notification Data:" + notiSound + " ")

                // Check if message contains a notification payload.
                AppInstance.userObj = getUserObject(this)
                val userid = AppInstance.userObj!!.getId()!!
                if (userid.length > 1) {
                    sendNotification(
                        title,
                        message,
                        sound,
                        channelId,
                        type,
                        timeSlot,
                        user_name,
                        user_id,
                        rejoin_type
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        //sendNotification(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody(), text);


    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.e(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    /*private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }*/

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.e(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param title FCM message title received.
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(
        title: String,
        messageBody: String,
        sound: String?,
        notiChannelId:
        String?,
        notificationType: String,
        timeSlot: String,
        userName: String,
        userId: String,
        rejoin_type: String
    ) {
        try {
            var intent: Intent? = null
            //check is call ended by other user

            //unique notification id and get current step
            val notificationID = Random().nextInt(500 - 1 + 1) + 1
            Log.e(TAG, "sendNotification() = $notificationID")
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //check app is in background or not
            var pendingIntent: PendingIntent
            try {
                // val isAppOpen = getPrefBoolean(this, PreferenceConnector.IS_APP_OPEN)
                pendingIntent =
                    if (!MyApplication.isAppInBackground) {  //!isAppIsInBackground(this)
                        if (notificationType == ALERT && !messageBody.isEmpty()) {
                            intent = Intent(this, WaitingActivity::class.java)
                            intent.putExtra(ALERT, messageBody)
                            intent.putExtra(TIMESLOT, timeSlot)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            PendingIntent.getActivity(
                                this, notificationID, intent,
                                PendingIntent.FLAG_ONE_SHOT
                            )
                        } else if (notificationType == REJOIN) {
                            intent = Intent(this, Splash::class.java)
                            intent.putExtra(REJOIN_USERNAME, userName)
                            intent.putExtra(REJOIN_USERID, userId)
                            intent.putExtra(REJOIN_TYPE, rejoin_type)
                            intent.putExtra(NOTIFICATION_TYPE, notificationType)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            PendingIntent.getActivity(
                                this, notificationID, intent,
                                PendingIntent.FLAG_ONE_SHOT
                            )
                        } else
                            NotificationActivity.getDismissIntent(notificationID, this)
                    } else if (notificationType == MESSAGE) {
                        if (!MyApplication.isAppInBackground)
                            intent = Intent(this, ChatActivity::class.java)
                        else
                            intent = Intent(this, Splash::class.java)
                        intent?.putExtra(REJOIN_USERID, userId)
                        intent.putExtra(REJOIN_TYPE, rejoin_type)
                        intent.putExtra(REJOIN_USERNAME, userName)
                        intent.putExtra(CHAT_USER_TYPE, REJOIN)
                        intent.putExtra(NOTIFICATION_TYPE, notificationType)

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        PendingIntent.getActivity(
                            this, notificationID, intent,
                            PendingIntent.FLAG_ONE_SHOT
                        )
                    } else {
                        intent = Intent(this, Splash::class.java)
                        if (notificationType == ALERT && !messageBody.isEmpty()) {
                            intent.putExtra(ALERT, messageBody)
                            intent.putExtra(TIMESLOT, timeSlot)
                            intent.putExtra(NOTIFICATION_TYPE, notificationType)
                        } else if (notificationType == REJOIN) {
                            intent.putExtra(REJOIN_USERNAME, userName)
                            intent.putExtra(REJOIN_USERID, userId)
                            intent.putExtra(REJOIN_TYPE, rejoin_type)
                            intent.putExtra(NOTIFICATION_TYPE, notificationType)
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        PendingIntent.getActivity(
                            this, notificationID, intent,
                            PendingIntent.FLAG_ONE_SHOT
                        )
                    }
            } catch (e: Exception) {
                intent = Intent(this, Splash::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                pendingIntent = PendingIntent.getActivity(
                    this, notificationID, intent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            }

            var channelId = getString(R.string.default_notification_channel_id)


            if (notiChannelId != null && !notiChannelId.isEmpty())
                channelId = notiChannelId

/*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.deleteNotificationChannel("channel_id")
            }
*/


            val defaultSoundUri: Uri
            //val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            //val defaultSoundUri = Uri.parse("android.resource://$packageName/raw/notification.mp3")
            // val defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.notification)
            if (sound != null && !sound.isEmpty())
                defaultSoundUri =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/raw/" + sound)
            else
                defaultSoundUri =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.notification)


            val icon = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_round_icon)

            //        playSound(defaultSoundUri)
            //  enableLights()
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
            notificationBuilder.setGroup(BuildConfig.APPLICATION_ID)
            notificationBuilder.setGroupSummary (true)
            notificationBuilder.setSmallIcon(R.drawable.notification_icon)
            notificationBuilder.setLargeIcon(icon)
            notificationBuilder.setContentTitle(title)
            notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            notificationBuilder.setContentText(messageBody)
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS)
            notificationBuilder.setLights(Color.BLUE, 300, 100)
            //.setOngoing(true)
            notificationBuilder.color = ContextCompat.getColor(this, R.color.purple)
            notificationBuilder.setSound(defaultSoundUri)
            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
            notificationBuilder.setContentIntent(pendingIntent)
            if (notificationType == REJOIN)
                notificationBuilder.setFullScreenIntent(getFullScreenIntent(intent!!), true)


            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e(TAG, "Notification for above and equals Oreo..")

                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()

                val mChannel = NotificationChannel(
                    channelId,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH
                )

                // Configure the notification channel.
                mChannel.description = messageBody
                mChannel.enableLights(true)
                mChannel.enableVibration(true)
                mChannel.setSound(defaultSoundUri, attributes); // This is IMPORTANT
                mChannel.lightColor = Color.GREEN
                mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                mChannel.setSound(defaultSoundUri, attributes) // This is IMPORTANT

                notificationManager.createNotificationChannel(mChannel)
                notificationBuilder.setChannelId(channelId)
            }

            //build notification
            notificationManager.notify(notificationID, notificationBuilder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun enableLights() {
        val pm: PowerManager = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        val isScreenOn: Boolean =
            if (Build.VERSION.SDK_INT >= 20) pm.isInteractive() else pm.isScreenOn() // check if screen is on
        if (!isScreenOn) {
            val wl: PowerManager.WakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "myApp:notificationLock"
            )
            wl.acquire(3000) //set your time in milliseconds
        }
    }

    private fun playSound(defaultSoundUri: Uri) {
        var mediaPlayer = MediaPlayer.create(this, defaultSoundUri).start()
    }

    private fun Context.getFullScreenIntent(notificationIntent: Intent): PendingIntent {
        // flags and request code are 0 for the purpose of demonstration
        return PendingIntent.getActivity(this, 0, notificationIntent, 0)
    }

    /**
     * static objects
     */
    companion object {
        private const val TAG = "MyFirebaseMessagingService"
    }


}
