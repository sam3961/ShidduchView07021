package shiddush.view.com.mmvsd.utill

import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field

class ViewPageScroller : Scroller {

    var fixedDuration = 1500 //time to scroll in milliseconds

    constructor(context: Context) : super(context)

    constructor(context: Context, interpolator: Interpolator) : super(context, interpolator)

    constructor(context: Context, interpolator: Interpolator, flywheel: Boolean) : super(context, interpolator, flywheel)


    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, fixedDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, fixedDuration)
    }
}

fun ViewPager.setViewPageScroller(viewPageScroller: ViewPageScroller) {
    try {
        val mScroller: Field = ViewPager::class.java.getDeclaredField("mScroller")
        mScroller.isAccessible = true
        mScroller.set(this, viewPageScroller)
    } catch (e: NoSuchFieldException) {
    } catch (e: IllegalArgumentException) {
    } catch (e: IllegalAccessException) {
    }

}