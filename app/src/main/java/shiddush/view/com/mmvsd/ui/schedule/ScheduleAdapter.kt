package shiddush.view.com.mmvsd.ui.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.weekdays_adapter_layout.view.*
import shiddush.view.com.mmvsd.R
import java.util.*

class ScheduleAdapter(val items : ArrayList<String> , val context : Context) : RecyclerView.Adapter<DaysHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.weekdays_adapter_layout,parent,false)
        return DaysHolder(inflatedView)    }

    override fun getItemCount(): Int {
        return items.size}
    override fun onBindViewHolder(holder: DaysHolder, position: Int) {
        holder.tvDays.setText(items[position]);
    }

}
class DaysHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvDays = view.days
}