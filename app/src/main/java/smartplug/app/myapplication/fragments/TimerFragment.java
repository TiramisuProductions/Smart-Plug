package smartplug.app.myapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import smartplug.app.myapplication.R;

/**
 * Created by Sarvesh Palav on 1/11/2018.
 */

public class TimerFragment  extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer,
                container, false);
        return view;
    }
}
