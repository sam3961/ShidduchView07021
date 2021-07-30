package shiddush.view.com.mmvsd.ui.recievers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.widget.Toast
import shiddush.view.com.mmvsd.ui.AlarmActivity
import shiddush.view.com.mmvsd.utill.getPrefString
import java.text.SimpleDateFormat
import java.util.*

class AlarmReciever : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.e("alarmCalled", "================" + p1?.getLongExtra("time", 0))
        val requestCode: Int? = p1?.getIntExtra("requestCode", 0)
        //p0?.let { scheduleAlarms(it) }
        Toast.makeText(p0,""+requestCode,Toast.LENGTH_SHORT).show()

        val alarmManager = p0?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val myIntent = Intent(p0, AlarmReciever::class.java)
        val pendingIntent = requestCode?.let {
            PendingIntent.getBroadcast(
                    p0, it, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        alarmManager!!.cancel(pendingIntent)

        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        if (p0?.let { getPrefString(it,"alarm").isNullOrEmpty() }!!){
            p0?.let { alarmDisplay(it) }
        }else if (!currentDate.equals(p0?.let { getPrefString(it,"alarm") })){
            p0?.let { alarmDisplay(it) }
        }
    }

    fun scheduleAlarms(p0: Context) {
        // every day at scheduled time
        val calendar = Calendar.getInstance()
        // if it's after or equal 9 am schedule for next day
        //calendar.add(Calendar.SECOND, 20)

        calendar.set(Calendar.HOUR_OF_DAY, 17)
        calendar.set(Calendar.MINUTE, 45)
        calendar.set(Calendar.SECOND, 0)
        //calendar.time = Date()

        val alarmIntent = Intent(p0,
                AlarmReciever::class.java)
        alarmIntent.putExtra("time", calendar.timeInMillis)
        val pi = PendingIntent.getBroadcast(p0, 0, alarmIntent, 0)
        val mgr = p0.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        mgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pi)
    }

    fun alarmDisplay(p0: Context) {
        /*val dialog = Dialog(p0, R.style.NoTitleDialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.fragment_intro_notes.activity_ala  rm_display)
        //val main_layout = dialog.findViewById<View>(R.id.main_layout) as LinearLayout
        dialog.show()*/

        //commented alarm code
       /* val intent = Intent(p0, AlarmActivity::class.java)
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        p0.startActivity(intent)*/
    }



}