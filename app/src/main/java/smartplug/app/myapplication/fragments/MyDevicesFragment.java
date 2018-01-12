package smartplug.app.myapplication.fragments;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import smartplug.app.myapplication.BluetoothManager;
import smartplug.app.myapplication.HomeActivity;
import smartplug.app.myapplication.R;
import smartplug.app.myapplication.TurnOnDeviceActivity;

/**
 * Created by Sarvesh Palav on 1/11/2018.
 */

public class MyDevicesFragment extends Fragment {

    @BindView(R.id.fab_add_device)
    FloatingActionButton adddeviceFab;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mydevices,
                container, false);

        ButterKnife.bind(this,view);

        adddeviceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((BluetoothManager.isBluetoothSupported())
                        && (!BluetoothManager.isBluetoothEnabled())) {

                    turnOnBluetoothDialog();
                } else {
                    startActivity(new Intent(getActivity(), TurnOnDeviceActivity.class));
                }
            }
        });

        return view;
    }

    private void turnOnBluetoothDialog() {
        new MaterialDialog.Builder(getActivity()).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                getActivity().startActivityForResult(enableBtIntent, 1);
            }
        }).cancelable(false)
                .title("Your Bluetooth seems to be off")
                .content("Lets turn it On")
                .positiveText("Turn It on")
                .negativeText("No")
                .show();
    }
}
