package shiddush.view.com.mmvsd.widget.swapdroid

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ListPopupWindow
import android.widget.Spinner
import shiddush.view.com.mmvsd.R

class SwapdroidCustomSpinner : Spinner {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int, mode: Int) : super(context, attrs, defStyle, mode) {}

    constructor(context: Context, mode: Int) : super(context, mode) {}

    override fun performClick(): Boolean {
        val bClicked = super.performClick()

        try {
            val mPopupField = Spinner::class.java.getDeclaredField("mPopup")
            mPopupField.isAccessible = true
            val pop = mPopupField.get(this) as ListPopupWindow
            val listview = pop.listView

            /*try{
                // Set popupWindow height to 100dp
                pop.setHeight(dpToPxs(100))
            }catch (e:Exception){
                e.printStackTrace()
            }*/

            val mScrollCacheField = View::class.java.getDeclaredField("mScrollCache")
            mScrollCacheField.isAccessible = true
            val mScrollCache = mScrollCacheField.get(listview)
            val scrollBarField = mScrollCache!!.javaClass.getDeclaredField("scrollBar")
            scrollBarField.isAccessible = true
            val scrollBar = scrollBarField.get(mScrollCache)
            val method = scrollBar!!.javaClass.getDeclaredMethod("setVerticalThumbDrawable", Drawable::class.java)
            method.isAccessible = true
            method.invoke(scrollBar, resources.getDrawable(R.drawable.scrollbar))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                val mVerticalScrollbarPositionField = View::class.java.getDeclaredField("mVerticalScrollbarPosition")
                mVerticalScrollbarPositionField.isAccessible = true
                mVerticalScrollbarPositionField.set(listview, View.SCROLLBAR_POSITION_RIGHT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bClicked
    }
}
