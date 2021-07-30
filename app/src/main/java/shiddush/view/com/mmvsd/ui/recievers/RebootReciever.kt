package shiddush.view.com.mmvsd.ui.recievers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import shiddush.view.com.mmvsd.utill.PreferenceConnector
import shiddush.view.com.mmvsd.utill.getPrefBoolean
import java.text.SimpleDateFormat
import java.util.*

class RebootReciever : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.e("alarmCalled", "================ set alarms")

        if (p0?.let { getPrefBoolean(it, PreferenceConnector.IS_LOCATION_ALARM_ENABLED) }!!) {
            scheduleAlarms(p0)
        }
    }

    fun scheduleAlarms(p0: Context?) {

        val localCalendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
        val currentDayOfWeek: Int = localCalendar.get(Calendar.DAY_OF_WEEK)

        if (currentDayOfWeek == 1) {
            setTimings(p0, 1, 0)
            setTimings(p0, 2, 1)
            setTimings(p0, 3, 2)
            setTimings(p0, 4, 3)
            setTimings(p0, 5, 4)
        } else if (currentDayOfWeek == 2) {
            setTimings(p0, 1, 6)
            setTimings(p0, 2, 0)
            setTimings(p0, 3, 1)
            setTimings(p0, 4, 2)
            setTimings(p0, 5, 3)
        } else if (currentDayOfWeek == 3) {
            setTimings(p0, 1, 5)
            setTimings(p0, 2, 6)
            setTimings(p0, 3, 0)
            setTimings(p0, 4, 1)
            setTimings(p0, 5, 2)
        } else if (currentDayOfWeek == 4) {
            setTimings(p0, 1, 4)
            setTimings(p0, 2, 5)
            setTimings(p0, 3, 6)
            setTimings(p0, 4, 0)
            setTimings(p0, 5, 1)
        } else if (currentDayOfWeek == 5) {
            setTimings(p0, 1, 3)
            setTimings(p0, 2, 4)
            setTimings(p0, 3, 5)
            setTimings(p0, 4, 6)
            setTimings(p0, 5, 0)
        } else if (currentDayOfWeek == 6) {
            setTimings(p0,1, 2)
            setTimings(p0,2, 3)
            setTimings(p0,3, 4)
            setTimings(p0,4, 5)
            setTimings(p0,5, 6)
        } else if (currentDayOfWeek == 7) {
            setTimings(p0,1, 1)
            setTimings(p0,2, 2)
            setTimings(p0,3, 3)
            setTimings(p0,4, 4)
            setTimings(p0,5, 5)
        }
    }

    fun setTimings(p0: Context?, reqCode: Int, day: Int) {

        var hour = 2

        if (reqCode == 1) {
            hour = 2
        } else if (reqCode == 2 ||  reqCode == 5) {
            hour = 5
        } else if (reqCode == 3 || reqCode == 4) {
            hour = 9
        }

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 28)
        calendar.set(Calendar.SECOND, 0)

        val istDf: SimpleDateFormat =  SimpleDateFormat("MM dd yyyy HH mm");
        istDf.setTimeZone( TimeZone.getDefault() )
        val newDate:String = istDf.format(calendar.getTimeInMillis());
        System.out.println("india  "+istDf.format(calendar.getTimeInMillis()));

        val calendar2 = Calendar.getInstance()

        val dates = newDate.split(" ")
        calendar2.timeZone = TimeZone.getDefault()
        calendar2.add(Calendar.DAY_OF_MONTH, dates[1].toInt())
        calendar2.set(Calendar.HOUR_OF_DAY, dates[3].toInt())
        calendar2.set(Calendar.MINUTE, dates[4].toInt())
        calendar2.set(Calendar.SECOND, 0)

        val timestamp: Long = calendar2.timeInMillis

        //val timestamp: Long = calendar.timeInMillis
        //commented set alarm code
        //setAlarm(p0, reqCode, timestamp)
    }

    fun setAlarm(p0: Context?, reqCode: Int, time: Long) {

        val alarmIntent = Intent(p0,
                AlarmReciever::class.java)
        val pi = PendingIntent.getBroadcast(p0, reqCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mgr = p0?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent.putExtra("time", time)
        alarmIntent.putExtra("requestCode", reqCode)

        // mgr.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pi);
        mgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, time,
                86400000 * 7, pi)
    }
}