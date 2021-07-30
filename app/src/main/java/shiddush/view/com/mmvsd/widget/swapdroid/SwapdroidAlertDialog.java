package shiddush.view.com.mmvsd.widget.swapdroid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import shiddush.view.com.mmvsd.R;

/**
 * Created by Sumit Kumar
 * This class is use to show custom Alert Dialog
 */
public class SwapdroidAlertDialog {

    public static class Builder {
        private String title, message, positiveBtnText, negativeBtnText;
        private Activity activity;
        private int icon;
        private SwapdroidIcon visibility;
        private SwapdroidAnimation animation;
        private SwapdroidAlertDialogListener pListener, nListener, cListener;
        private int pBtnColor, nBtnColor, bgColor;
        private boolean cancel = false, btnPositive = true, btnNegative = true, txtMessage = true,showCancelIcon=false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setBackgroundColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setPositiveBtnText(String positiveBtnText) {
            this.positiveBtnText = positiveBtnText;
            return this;
        }

        public Builder setPositiveBtnBackground(int pBtnColor) {
            this.pBtnColor = pBtnColor;
            return this;
        }

        public Builder setNegativeBtnText(String negativeBtnText) {
            this.negativeBtnText = negativeBtnText;
            return this;
        }

        public Builder setNegativeBtnBackground(int nBtnColor) {
            this.nBtnColor = nBtnColor;
            return this;
        }


        //setIcon
        public Builder setIcon(int icon, SwapdroidIcon visibility) {
            this.icon = icon;
            this.visibility = visibility;
            return this;
        }

        public Builder setAnimation(SwapdroidAnimation animation) {
            this.animation = animation;
            return this;
        }

        //set Positive listener
        public Builder OnPositiveClicked(SwapdroidAlertDialogListener pListener) {
            this.pListener = pListener;
            return this;
        }

        //set Negative listener
        public Builder OnNegativeClicked(SwapdroidAlertDialogListener nListener) {
            this.nListener = nListener;
            return this;
        }

        //set Cancel listener
        public Builder OnCancelClicked(SwapdroidAlertDialogListener cListener) {
            this.cListener = cListener;
            return this;
        }

        public Builder isCancellable(boolean cancel) {
            this.cancel = cancel;
            return this;
        }
        public Builder showCancelIcon(boolean cancel) {
            this.showCancelIcon = cancel;
            return this;
        }

        public Builder isPositiveVisible(boolean btnPositive) {
            this.btnPositive = btnPositive;
            return this;
        }

        public Builder isNegativeVisible(boolean btnNegative) {
            this.btnNegative = btnNegative;
            return this;
        }

        public Builder isMessageVisible(boolean txtMessage) {
            this.txtMessage = txtMessage;
            return this;
        }

        public void build() {
            try {
                TextView message1, title1, tvHebruTextT, tvHebruQuotes, tvHebruTextDt;
                ImageView iconImg, cancel_icon, cancel_icon_click;
                TextView nBtn, pBtn;
                View view;
                final Dialog dialog;
                if (animation == SwapdroidAnimation.POP)
                    dialog = new Dialog(activity, R.style.myDialog); //, R.style.PopTheme);
                else if (animation == SwapdroidAnimation.SIDE)
                    dialog = new Dialog(activity, R.style.myDialog); //, R.style.SideTheme);
                else if (animation == SwapdroidAnimation.SLIDE)
                    dialog = new Dialog(activity, R.style.myDialog); //, R.style.SlideTheme);
                else
                    dialog = new Dialog(activity, R.style.myDialog);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                try {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.setCancelable(cancel);
                dialog.setContentView(R.layout.swapdroid_alert_dialog);

                //getting resources
                view = dialog.findViewById(R.id.background);
                tvHebruTextT = dialog.findViewById(R.id.tv_hebru_text_t);
                tvHebruQuotes = dialog.findViewById(R.id.tv_hebru_quotes);
                tvHebruTextDt = dialog.findViewById(R.id.tv_hebru_text_dt);
                title1 = dialog.findViewById(R.id.title);
                message1 = dialog.findViewById(R.id.message);
                iconImg = dialog.findViewById(R.id.icon);
                cancel_icon = dialog.findViewById(R.id.imageViewCancel);
                cancel_icon_click = dialog.findViewById(R.id.cancel_icon_click);
                nBtn = dialog.findViewById(R.id.negativeBtn);
                pBtn = dialog.findViewById(R.id.positiveBtn);

                //text sizes
                try {
                    Float size30 = getFontSize(activity, 30);
                    Float size18 = getFontSize(activity, 19);
                    Float size16 = getFontSize(activity, 17);
                    Float size13 = getFontSize(activity, 14);

                    tvHebruTextT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30);
                    tvHebruQuotes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30);
                    tvHebruTextDt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size30);
                    title1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18);
                    message1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size13);
                    nBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size16);
                    pBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size16);

                    try {

                        Integer imgSize = getDynamicSize(activity, 0.10F);
                        Integer btnHeight = getDynamicSize(activity, 0.05F);
                        Integer btnWidth = getDynamicSize(activity, 0.16F);
                        Integer crossWH = getDynamicSize(activity, 0.025F);
                        Integer crossHWH = getDynamicSize(activity, 0.06F);

                        iconImg.getLayoutParams().height = imgSize;
                        iconImg.getLayoutParams().width = imgSize;

                        nBtn.getLayoutParams().height = btnHeight;
                        nBtn.getLayoutParams().width = btnWidth;
                        pBtn.getLayoutParams().height = btnHeight;
                        pBtn.getLayoutParams().width = btnWidth;

                        cancel_icon.getLayoutParams().height = crossWH;
                        cancel_icon.getLayoutParams().width = crossWH;

                        cancel_icon_click.getLayoutParams().height = crossHWH;
                        cancel_icon_click.getLayoutParams().width = crossHWH;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                title1.setText(title);
                message1.setText(message);
                if (!txtMessage) message1.setVisibility(View.GONE);
                if (!btnNegative) nBtn.setVisibility(View.GONE);
                if (!btnPositive) pBtn.setVisibility(View.GONE);

                if (positiveBtnText != null)
                    pBtn.setText(positiveBtnText);
                if (negativeBtnText != null)
                    nBtn.setText(negativeBtnText);

                iconImg.setImageResource(icon);
                if (visibility == SwapdroidIcon.Visible)
                    iconImg.setVisibility(View.VISIBLE);
                else
                    iconImg.setVisibility(View.GONE);
                if (bgColor != 0)
                    view.setBackgroundColor(bgColor);

                if (pListener != null) {
                    pBtn.setOnClickListener(view1 -> {
                        pListener.OnClick();
                        dialog.dismiss();
                    });
                } else {
                    pBtn.setOnClickListener(view12 -> dialog.dismiss());
                }

                if (nListener != null) {
                    nBtn.setVisibility(View.VISIBLE);
                    nBtn.setOnClickListener(view13 -> {
                        nListener.OnClick();

                        dialog.dismiss();
                    });
                } else {
                    nBtn.setOnClickListener(view14 -> dialog.dismiss());
                }

                if (showCancelIcon) {
                    cancel_icon.setVisibility(View.VISIBLE);
                    cancel_icon_click.setVisibility(View.VISIBLE);
                }else{
                    cancel_icon.setVisibility(View.GONE);
                    cancel_icon_click.setVisibility(View.GONE);
                }
                cancel_icon_click.setOnClickListener(view15 -> {
                    if (cListener != null) {
                        cListener.OnClick();
                    }
                    dialog.dismiss();
                });

                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Float getFontSize(Activity activity, Integer dp) {  //18
        float size = Float.parseFloat(dp.toString());
        try {
            //Integer density = activity.getResources().getDisplayMetrics().densityDpi;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            float density = displayMetrics.density;
            float ratio = Float.parseFloat(Integer.toString(height)) / 2436;
            float finalRatio = dp * ratio;
            if(ratio < 0.8){  //density == 2.0F &&  //&& (density >= 2.0F && density <= 3.0F)
                finalRatio = dp * 0.8F;
            }
            size = finalRatio;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    private static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * Dynamic view height
     */
    private static Integer getDynamicSize(Activity activity, Float reqHeight) {  //18
        int size = Integer.parseInt(String.valueOf(Math.round(convertDpToPixel(50, activity))));
        try {
            //Integer density = activity.getResources().getDisplayMetrics().densityDpi;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Integer height = displayMetrics.heightPixels;
            size = Integer.parseInt(String.valueOf(Math.round((height * reqHeight))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return size;
    }

}
