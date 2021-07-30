package shiddush.view.com.mmvsd.utill

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import shiddush.view.com.mmvsd.R


class TextViewOutline(context: Context?, attrs: AttributeSet?) :
    TextView(context, attrs) {
    // data
    private var mOutlineSize = 0
    private var mOutlineColor = 0
    private var mTextColor = 0
    private var mShadowRadius = 0f
    private var mShadowDx = 0f
    private var mShadowDy = 0f
    private var mShadowColor = 0

    constructor(context: Context?) : this(context, null) {}

    private fun setAttributes(attrs: AttributeSet?) {
        // set defaults
        mOutlineSize = DEFAULT_OUTLINE_SIZE
        mOutlineColor = DEFAULT_OUTLINE_COLOR
        // text color
        mTextColor = getCurrentTextColor()
        if (attrs != null) {
            val a: TypedArray =
                getContext().obtainStyledAttributes(attrs, R.styleable.TextViewOutline)
            // outline size
            if (a.hasValue(R.styleable.TextViewOutline_outlineSize)) {
                mOutlineSize =
                        a.getDimension(R.styleable.TextViewOutline_outlineSize,
                        DEFAULT_OUTLINE_SIZE.toFloat()
                    ).toInt()
            }
            // outline color
            if (a.hasValue(R.styleable.TextViewOutline_outlineColor)) {
                mOutlineColor =
                    a.getColor(R.styleable.TextViewOutline_outlineColor, DEFAULT_OUTLINE_COLOR)
            }
            // shadow (the reason we take shadow from attributes is because we use API level 15 and only from 16 we have the get methods for the shadow attributes)
            if (a.hasValue(R.styleable.TextViewOutline_android_shadowRadius)
                || a.hasValue(R.styleable.TextViewOutline_android_shadowDx)
                || a.hasValue(R.styleable.TextViewOutline_android_shadowDy)
                || a.hasValue(R.styleable.TextViewOutline_android_shadowColor)
            ) {
                mShadowRadius = a.getFloat(R.styleable.TextViewOutline_android_shadowRadius, 0f)
                mShadowDx = a.getFloat(R.styleable.TextViewOutline_android_shadowDx, 0f)
                mShadowDy = a.getFloat(R.styleable.TextViewOutline_android_shadowDy, 0f)
                mShadowColor =
                    a.getColor(R.styleable.TextViewOutline_android_shadowColor, Color.TRANSPARENT)
            }
            a.recycle()
        }

    }

    private fun setPaintToOutline() {
        val paint: Paint = getPaint()
        paint.setStyle(Paint.Style.STROKE)
        paint.setStrokeWidth(mOutlineSize.toFloat())
        super.setTextColor(mOutlineColor)
        super.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor)
    }

    private fun setPaintToRegular() {
        val paint: Paint = getPaint()
        paint.setStyle(Paint.Style.FILL)
        paint.setStrokeWidth(0f)
        super.setTextColor(mTextColor)
        super.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setPaintToOutline()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        mTextColor = color
    }

    override fun setShadowLayer(radius: Float, dx: Float, dy: Float, color: Int) {
        super.setShadowLayer(radius, dx, dy, color)
        mShadowRadius = radius
        mShadowDx = dx
        mShadowDy = dy
        mShadowColor = color
    }

    fun setOutlineSize(size: Int) {
        mOutlineSize = size
    }

    fun setOutlineColor(color: Int) {
        mOutlineColor = color
    }

    protected override fun onDraw(canvas: Canvas?) {
        setPaintToOutline()
        super.onDraw(canvas)
        setPaintToRegular()
        super.onDraw(canvas)
    }

    companion object {
        // constants
        private const val DEFAULT_OUTLINE_SIZE = 0
        private val DEFAULT_OUTLINE_COLOR: Int = Color.TRANSPARENT
    }

    init {
        setAttributes(attrs)
    }
}