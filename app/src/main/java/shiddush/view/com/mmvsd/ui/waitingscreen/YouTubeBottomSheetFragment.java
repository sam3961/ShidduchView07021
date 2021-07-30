package shiddush.view.com.mmvsd.ui.waitingscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import shiddush.view.com.mmvsd.R;

public class YouTubeBottomSheetFragment extends BottomSheetDialogFragment {
    public YouTubeBottomSheetFragment() {
        // Required empty public constructor
    }
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment_intro_notes for this fragment
        return inflater.inflate(R.layout.bottom_sheet_player, container, false);
    }
}