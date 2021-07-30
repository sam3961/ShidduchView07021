package shiddush.view.com.mmvsd.ui.schedule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_schedule.*
import shiddush.view.com.mmvsd.R

class ScheduleFormActivity : AppCompatActivity() {
    val entries: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        addEntries()
        onCLickListeners()
        week_days_recyclerview.layoutManager = LinearLayoutManager(this)
        week_days_recyclerview.adapter = ScheduleAdapter(entries, this)
    }

    private fun onCLickListeners() {
        all_times.setOnClickListener {
            all_times.setBackgroundResource(R.drawable.curved_shape_orange_filled)
            all_times.setTextColor(getColor(R.color.white))
            early_times.setBackgroundResource(R.drawable.curved_shape_orange_border)
            early_times.setTextColor(getColor(R.color.colorDarkGrayText))
            specific_times.setBackgroundResource(R.drawable.curved_shape_orange_border)
            specific_times.setTextColor(getColor(R.color.colorDarkGrayText))
        }
        early_times.setOnClickListener {
            early_times.setBackgroundResource(R.drawable.curved_shape_orange_filled)
            early_times.setTextColor(getColor(R.color.white))
            all_times.setBackgroundResource(R.drawable.curved_shape_orange_border)
            all_times.setTextColor(getColor(R.color.colorDarkGrayText))
            specific_times.setBackgroundResource(R.drawable.curved_shape_orange_border)
            specific_times.setTextColor(getColor(R.color.colorDarkGrayText))
        }
        specific_times.setOnClickListener {
            specific_times.setBackgroundResource(R.drawable.curved_shape_orange_filled)
            specific_times.setTextColor(getColor(R.color.white))
            early_times.setBackgroundResource(R.drawable.curved_shape_orange_border)
            early_times.setTextColor(getColor(R.color.colorDarkGrayText))
            all_times.setBackgroundResource(R.drawable.curved_shape_orange_border)
            all_times.setTextColor(getColor(R.color.colorDarkGrayText))
        }

        this_week.setOnClickListener {
            this_week.setBackgroundResource(R.drawable.curved_shape_orange_filled)
            this_week.setTextColor(getColor(R.color.white))
            next_week.setBackgroundResource(R.drawable.curved_shape_orange_border)
            next_week.setTextColor(getColor(R.color.colorDarkGrayText))

        }
        next_week.setOnClickListener {
            next_week.setBackgroundResource(R.drawable.curved_shape_orange_filled)
            next_week.setTextColor(getColor(R.color.white))
            this_week.setBackgroundResource(R.drawable.curved_shape_orange_border)
            this_week.setTextColor(getColor(R.color.colorDarkGrayText))

        }

        btnSubmit.setOnClickListener {
            /* val intent = Intent(this,ScheduleSuccessfulActivity::class.java)
             startActivity(intent)*/
        }
    }

    fun addEntries() {
        entries.add("Sunday")
        entries.add("Monday")
        entries.add("Tuesday")
        entries.add("Wednesday")
        entries.add("Thursday")
        entries.add("Friday")
        entries.add("Saturday")
    }
}
