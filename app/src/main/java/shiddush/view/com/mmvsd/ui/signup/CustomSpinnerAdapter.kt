package shiddush.view.com.mmvsd.ui.signup

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.utill.getFontSize

/**
 * Created by Sumit Kumar.
 */
class CustomSpinAdapter(context: Context, objects: ArrayList<String>) : ArrayAdapter<String>(context, R.layout.spinner_list_view, objects) {

    //don't override if you don't want the default spinner to be a two line view
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initDropDownView(position, convertView)
    }

    @SuppressLint("ResourceAsColor")
    private fun initView(position: Int, convertView: View?): View {
        var conView: View
        try {
            conView = View.inflate(context, R.layout.spinner_list_view, null)
            val tvTitle = conView!!.findViewById<View>(android.R.id.text1) as TextView
            val size20 = getFontSize(context, 20)
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
            tvTitle.text = getItem(position)
        } catch (e: Exception) {
            e.printStackTrace()
            conView = convertView!!
        }
        return conView
    }

    private fun initDropDownView(position: Int, convertView: View?): View {
        var conView: View
        try {
            conView = View.inflate(context, R.layout.simple_spinner_dropdown_list_item, null)
            val tvTitle = conView!!.findViewById<View>(android.R.id.text1) as TextView

            val size20 = getFontSize(context, 20)
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size20)
            tvTitle.text = getItem(position)
        } catch (e: Exception) {
            e.printStackTrace()
            conView = convertView!!
        }
        return conView
    }
}