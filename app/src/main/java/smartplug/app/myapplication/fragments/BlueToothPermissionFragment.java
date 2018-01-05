package smartplug.app.myapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import smartplug.app.myapplication.R;

/**
 * Created by Sarvesh Palav on 1/5/2018.
 */

public class BlueToothPermissionFragment  extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intro_permission,
                container, false);
        return view;
    }
}
