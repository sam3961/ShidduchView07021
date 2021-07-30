package org.puder.highlight.internal;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.DialogFragment;

import org.puder.highlight.R;
import org.puder.highlight.internal.callback.TutorialClickListener;

public class HighlightDialogFragment extends DialogFragment {

    public interface HighlightDismissedListener {
        void onHighlightDismissed();
    }

    private TutorialClickListener tutorialClickListener;
    private HighlightDismissedListener listener;

    private HighlightItem item;
    private String TAG = getClass().getSimpleName();


    public void setTutorialClickListener(TutorialClickListener tutorialClickListener) {
        this.tutorialClickListener = tutorialClickListener;
    }

    public void setHighlightItem(HighlightItem item) {
        this.item = item;
    }

    public void setListener(HighlightDismissedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog overlayInfo = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
        // Making dialog content transparent.
        overlayInfo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Removing window dim normally visible when dialog are shown.
        overlayInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        if (item == null) {
            /*
             * Although this fragment is not retained, it seems that Android
             * will re-create it during an orientation change. In this case item
             * == null. Just return the empty dialog here and dismiss the dialog
             * immediately. It will be immediately disposed once the fragment is
             * removed after the orientation change.
             */
            dismissAllowingStateLoss();
            return overlayInfo;
        }
        WindowManager wm = getActivity().getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Log.e(TAG, "PointSize: " + size);
        int cy = item.screenTop + (item.screenBottom - item.screenTop) / 2;
        overlayInfo.setContentView(/*size.y / 2 > cy ?*/ R.layout.highlight_top
                /*: R.layout.highlight_bottom*/);
        Log.e(TAG, "cy: " + cy);
        LinearLayout layout = overlayInfo.findViewById(R.id.ll_highlight);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();

        if ((size.y / 2) > cy) {
            System.out.println(TAG + " cy " + cy + " --- ALIGN_PARENT_BOTTOM");
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            System.out.println(TAG + " cy " + cy + " --- ALIGN_PARENT_TOP");
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }

        layout.setLayoutParams(params);
        HighlightView highlightView = overlayInfo.findViewById(R.id.highlight_view);
        highlightView.setHighlightItem(item);
        highlightView.nextClick(tutorialClickListener);
        highlightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                if (listener != null) {
                    listener.onHighlightDismissed();
                }
            }
        });
        return overlayInfo;
    }

    @Override
    public void onDestroyView() {
        /*
         * https://code.google.com/p/android/issues/detail?id=17423
         */
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
        setHasOptionsMenu(false);
    }
}