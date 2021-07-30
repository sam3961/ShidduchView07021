package shiddush.view.com.mmvsd.ui.recievers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import shiddush.view.com.mmvsd.ui.AlarmActivity
import shiddush.view.com.mmvsd.utill.PreferenceConnector
import shiddush.view.com.mmvsd.utill.getPrefBoolean
import shiddush.view.com.mmvsd.utill.getPrefString
import shiddush.view.com.mmvsd.utill.savePrefString
import java.util.*
import java.util.concurrent.TimeUnit

class BackgroundReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        //commented alarm code
        //scheduleAlarms(context)

        if (!getPrefBoolean(context, PreferenceConnector.IS_LOCATION_ALARM_ENABLED)) {

//        val localCalendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
            val localCalendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
            val currentDayOfWeek: Int = localCalendar.get(Calendar.DAY_OF_WEEK)
            if (currentDayOfWeek == 1) {
                if (localCalendar.get(Calendar.HOUR_OF_DAY) == 14 && localCalendar.get(Calendar.MINUTE) == 28) {
                    //commented alarm code
                    //ringAlarm(context, localCalendar)
                }
            } else if (currentDayOfWeek == 2 || currentDayOfWeek == 5) {
                if (localCalendar.get(Calendar.HOUR_OF_DAY) == 17 && localCalendar.get(Calendar.MINUTE) == 28) {
                    //commented alarm code
                    // ringAlarm(context, localCalendar)
                }
            } else if (currentDayOfWeek == 3 || currentDayOfWeek == 4) {
                if (localCalendar.get(Calendar.HOUR_OF_DAY) == 21 && localCalendar.get(Calendar.MINUTE) == 28) {
                    //commented alarm code
                    //ringAlarm(context, localCalendar)
                }
            }
        }
    }

    private fun ringAlarm(context: Context, localCalendar: Calendar) {
        if (getPrefString(context, PreferenceConnector.ALARM_DELAY_TIME) != "") {
            val prevTime: Long = getPrefString(context, PreferenceConnector.ALARM_DELAY_TIME).toLong()
            val msDiff: Long = localCalendar.timeInMillis - prevTime
            val daysDiff = TimeUnit.MILLISECONDS.toSeconds(msDiff)
            if (daysDiff > 60 || daysDiff < -1) {
                val intent = Intent(context, AlarmActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                savePrefString(context, PreferenceConnector.ALARM_DELAY_TIME, "" + localCalendar.timeInMillis)
            }
        } else {
            val intent = Intent(context, AlarmActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            savePrefString(context, PreferenceConnector.ALARM_DELAY_TIME, "" + localCalendar.timeInMillis)
        }
    }


    private fun scheduleAlarms(context: Context) {
        val alarmIntent = Intent(context, BackgroundReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, 20)
        calendar.time = Date()
//        mgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
        mgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pi)
    }

}