package shiddush.view.com.mmvsd.ui.signup

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.utill.getFontSize

class CustomGenderSpinnerAdapter(context: Context, private val items: List<String>)
    : ArrayAdapter<String?>(context, R.layout.spinner_list_view, items) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return if (position == 0) {
            initialSelection(true)
        } else getCustomView(position, convertView, parent)
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return if (position == 0) {
            initialSelection(false)
        } else getCustomView(position, convertView, parent)
    }

    override fun getCount(): Int {
        return super.getCount() + 1 // Adjust for initial selection item
    }

    private fun initialSelection(dropdown: Boolean): View { // Just an example using a simple TextView. Create whatever default view
// to suit your needs, inflating a separate fragment_intro_notes if it's cleaner.
        val view = TextView(getContext())
        view.text = context.getString(R.string.select)
        val size17 = getFontSize(context, 17)
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size17)
        val typeface: Typeface = ResourcesCompat.getFont(context, R.font.avenirnextltpro_demi)!!
        view.setTypeface(typeface)
        view.gravity = Gravity.CENTER_VERTICAL or Gravity.BOTTOM
        val spacing = getContext().resources.getDimensionPixelSize(R.dimen.space_1)
        view.setPadding(0, spacing, 0, spacing)
        if (dropdown) { // Hidden when the dropdown is opened
            view.height = 0
        }
        return view
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View { // Distinguish "real" spinner items (that can be reused) from initial selection item
        var position = position
        val row = if (convertView != null && convertView !is TextView) convertView else LayoutInflater.from(getContext()).inflate(R.layout.spinner_list_view, parent, false)
        val tvTitle = row.findViewById<TextView>(android.R.id.text1)
        position = position - 1 // Adjust for initial selection item
        val size17 = getFontSize(context, 17)
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size17)
        tvTitle.text = getItem(position)
        val typeface: Typeface = ResourcesCompat.getFont(context, R.font.avenirnextltpro_demi)!!
        tvTitle.setTypeface(typeface)
        tvTitle.gravity = Gravity.CENTER_VERTICAL or Gravity.BOTTOM
        tvTitle.setPadding(3, 6, 0, 6)
        return row
    }

}