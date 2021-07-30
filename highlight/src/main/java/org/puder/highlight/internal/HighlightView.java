package org.puder.highlight.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.puder.highlight.R;
import org.puder.highlight.internal.callback.TutorialClickListener;


public class HighlightView extends RelativeLayout {

    private static final int ALPHA_60_PERCENT = 60;
    private final float innerRadiusScaleMultiplier = 1.2f;
    private final float outerRadiusScaleMultiplier = 1.8f;

    private Paint eraserPaint;
    private Paint basicPaint;
    private String TAG = getClass().getSimpleName();
    private HighlightItem item;

    private TutorialClickListener tutorialClickListener;

    public void nextClick(TutorialClickListener tutorialClickListener) {

        this.tutorialClickListener = tutorialClickListener;
    }

    public HighlightView(Context context) {
        super(context);
    }

    public HighlightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        basicPaint = new Paint();
        eraserPaint = new Paint();
        eraserPaint.setColor(Color.RED);
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(xfermode);
        eraserPaint.setAntiAlias(true);

    }

    public void setHighlightItem(HighlightItem item) {
        this.item = item;
        if (item.titleId != -1) {
            TextView title = findViewById(R.id.highlight_title);
//            String styledText = "This is <font color='#ffa008'>simple</font>.";
//            title.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            title.setText(item.titleId);
        }

        if (item.descriptionId != -1) {
            TextView descr = findViewById(R.id.highlight_description);
            descr.setText(item.descriptionId);
        }
        invalidate();
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {

        Log.d(TAG, "setOnClickListener() called with: listener = [" + listener + "]");
        // Delegate the click listener to the button
        findViewById(R.id.highlight_button).setOnClickListener(listener);
//if(tutorialClickListener!=null)
//         tutorialClickListener.clickListener(true);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        int[] location = new int[2];
        int a = 0xf48f;
        getLocationOnScreen(location);
        int width = item.screenRight - item.screenLeft;
        int height = item.screenBottom - item.screenTop;
        int cx = item.screenLeft + width / 2 - location[0];
        int cy = item.screenTop + height / 2 - location[1];
        float radius = width > height ? width / 2 : height / 2;
        Bitmap overlay = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas overlayCanvas = new Canvas(overlay);
        overlayCanvas.drawColor(0xbc000000);// 0x3333B5E5);
        eraserPaint.setAlpha(ALPHA_60_PERCENT);
        /* @param left The left side of the rectangle to be drawn
         * @param top The top side of the rectangle to be drawn
         * @param right The right side of the rectangle to be drawn
         * @param bottom The bottom side of the rectangle to be drawn*/
        overlayCanvas.drawRect(item.screenLeft, item.screenTop, item.screenRight, item.screenBottom, eraserPaint);
        // overlayCanvas.drawCircle(cx, cy, radius * outerRadiusScaleMultiplier, eraserPaint);
        eraserPaint.setAlpha(0);
        //overlayCanvas.drawCircle(cx, cy, radius * innerRadiusScaleMultiplier, eraserPaint);
        canvas.drawBitmap(overlay, 0, 0, basicPaint);
        super.dispatchDraw(canvas);
    }
}
