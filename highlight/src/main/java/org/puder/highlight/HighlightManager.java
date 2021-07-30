package org.puder.highlight;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.puder.highlight.internal.HighlightContentViewItem;
import org.puder.highlight.internal.HighlightDialogFragment;
import org.puder.highlight.internal.HighlightItem;
import org.puder.highlight.internal.HighlightMenuItem;
import org.puder.highlight.internal.callback.TutorialClickListener;

import java.util.ArrayList;
import java.util.List;

public class HighlightManager implements HighlightDialogFragment.HighlightDismissedListener {

    final private static String TAG_FRAGMENT = "HIGHLIGHT_FRAG";
    final private static String SHARE_PREF = "HIGHLIGHT_PREFS";

    private String TAG = getClass().getSimpleName();
    private FragmentActivity activity;
    private SharedPreferences prefs;
    private List<HighlightItem> items;
    private int numItemsToShow = 0;
    private int leftRightSpace = 15;
    private int topSpace = 80;
    private int bottomSpace = 65;

    private TutorialClickListener tutorialClickListener;


    public void nextClick(TutorialClickListener tutorialClickListener) {

        this.tutorialClickListener = tutorialClickListener;
    }

    public HighlightManager(FragmentActivity activity) {
        this.activity = activity;
        prefs = activity.getSharedPreferences(SHARE_PREF, Context.MODE_PRIVATE);
        items = new ArrayList<>();
    }

    public HighlightContentViewItem addView(int id) {
        final HighlightContentViewItem item = new HighlightContentViewItem(id);
        items.add(item);
        activity.findViewById(id).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                    return;
                }
                Log.e("onLayoutChange", "left:-" + left + "\ntop:-" + top +
                        "\nright:-" + right + "\nbottom:-" + bottom);
                v.removeOnLayoutChangeListener(this);
                setScreenPosition(v, item, false);
            }
        });
        return item;
    }

    public HighlightMenuItem addMenuItem(Menu menu, int menuItemId) {
        final HighlightMenuItem item = new HighlightMenuItem(menu, menuItemId);
        items.add(item);
        final MenuItem it = item.getMenuItem();

        ImageView button = new ImageView(activity, null, android.R.attr.actionButtonStyle);
        button.setImageDrawable(it.getIcon());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onMenuItemSelected(0, it);
            }
        });

        button.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                    return;
                }
                setScreenPosition(v, item, false);
            }
        });
        it.setActionView(button);
        return item;
    }

    private void setScreenPosition(View v, HighlightItem item, boolean isUpdate) {
        Log.e(TAG, "" + isUpdate);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        // v.setBackgroundColor(Color.RED);
        int screenLeft = location[0] - leftRightSpace;
        int screenTop = location[1] - topSpace;
        int screenRight = location[0] + v.getMeasuredWidth() + leftRightSpace;
        int screenBottom = location[1] + v.getMeasuredHeight() - bottomSpace;
        item.setScreenPosition(screenLeft, screenTop, screenRight, screenBottom);
        Log.e(TAG, activity.getString(item.getDescriptionId()) + "\nscreenLeft:-" + screenLeft + " screenTop:-" + screenTop +
                " screenRight:-" + screenRight + " screenBottom:-" + screenBottom);
        numItemsToShow++;
        if (numItemsToShow == items.size()) {
            show();
        }
    }

    private boolean isFirstTime(HighlightItem item) {
        String key = "";
        if (item instanceof HighlightContentViewItem) {
            key = "view-" + ((HighlightContentViewItem) item).getContentViewId();
        }
        if (item instanceof HighlightMenuItem) {
            key = "menu-" + ((HighlightMenuItem) item).getMenuItem();
        }

        if (prefs.getBoolean(key, true)) {
            Editor editor = prefs.edit();
            editor.putBoolean(key, false);
            editor.apply();
            return true;
        }
        return false;
    }

    public void reshowAllHighlights() {
        Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    private void show() {
        if (items.isEmpty()) {
            return;
        }

        final HighlightContentViewItem item = (HighlightContentViewItem) items.remove(0);

        if (tutorialClickListener != null)
            tutorialClickListener.clickListener(true, item.getContentViewId());
        if (!isFirstTime(item)) {
            /*
             * Item was already shown. Recurse to show() to give other items the
             * chance to be shown.
             */
            numItemsToShow--;
            show();
            return;
        }

        updatePosition(item);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                launchFragment(item);
            }
        }, 1000);
    }

    private void launchFragment(HighlightContentViewItem item) {


        FragmentManager fm = activity.getSupportFragmentManager();
        HighlightDialogFragment fragment = (HighlightDialogFragment) fm
                .findFragmentByTag(TAG_FRAGMENT);

        FragmentTransaction transaction = fm.beginTransaction();
        if (fragment != null) {
            transaction.remove(fragment);
        }
        fragment = new HighlightDialogFragment();
        fragment.setListener(this);

//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        fragment.setHighlightItem(item);
//        fragment.clickListener(true);
        fragment.setTutorialClickListener(tutorialClickListener);
        transaction.add(fragment, TAG_FRAGMENT);
        transaction.commit();
    }


    @Override
    public void onHighlightDismissed() {
        numItemsToShow--;
        show();
    }

    private void updatePosition(HighlightContentViewItem item) {
        setScreenPosition(activity.findViewById(item.getContentViewId()), item, true);
    }

}