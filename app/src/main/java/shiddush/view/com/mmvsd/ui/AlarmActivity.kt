package shiddush.view.com.mmvsd.ui

import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.ui.waitingscreen.WaitingActivity


class AlarmActivity : AppCompatActivity() {

    private lateinit var joinBtn: Button
    private lateinit var notAvailable: Button
    lateinit var ringtone: Ringtone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_display)

        joinBtn = findViewById(R.id.join_btn)
        notAvailable = findViewById(R.id.notAvailable)

        var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        ringtone = RingtoneManager.getRingtone(this, alarmUri)
        ringtone.play()

        joinBtn.setOnClickListener {
            ringtone.stop()
            startActivity(Intent(this, WaitingActivity::class.java))
            this@AlarmActivity.finishAffinity()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        notAvailable.setOnClickListener {
            cancelAlarm()
        }
    }

    private fun cancelAlarm() {
        ringtone.stop()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone.stop()
    }
}
