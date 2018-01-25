package smartplug.app.myapplication.Adapters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import smartplug.app.myapplication.Adapters.FoundDeviceAdapter;
import smartplug.app.myapplication.BluetoothDevices;
import smartplug.app.myapplication.Models.FoundDevices;

import smartplug.app.myapplication.R;

/**
 * Created by Siddhant Naique on 13-01-2018.
 */


public class FoundDeviceAdapter extends
        RecyclerView.Adapter<FoundDeviceAdapter.MyViewHolderF> {


    Context context;
    ArrayList<FoundDevices> foundDevicelist = new ArrayList<>();
    ArrayList<BluetoothDevice> ADlist = new ArrayList<>();



    public class MyViewHolderF extends RecyclerView.ViewHolder {
        TextView Dname, Daddress;

        public MyViewHolderF(View itemView) {
            super(itemView);

            Dname = (TextView) itemView.findViewById(R.id.device_nameF);
            Daddress = (TextView) itemView.findViewById(R.id.device_addressF);
        }
    }

    public FoundDeviceAdapter(Context context1, ArrayList<FoundDevices> list1, ArrayList<BluetoothDevice> ADlist1) {
        this.context = context1;
        this.foundDevicelist = list1;
        this.ADlist = ADlist1;
        context.registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    @Override
    public FoundDeviceAdapter.MyViewHolderF onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.found_device_row, parent, false);

        return new FoundDeviceAdapter.MyViewHolderF(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolderF holder, final int position) {

        final FoundDevices fd = foundDevicelist.get(position);
        holder.Dname.setText(fd.getFoundDeviceName());
        holder.Daddress.setText(fd.getFoundDeviceAddress());
        holder.Dname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, fd.getFoundDeviceName(), Toast.LENGTH_SHORT).show();

                for (BluetoothDevice btd : ADlist) {
                    if (fd.getFoundDeviceAddress().equals(btd.getAddress())) {
                        BluetoothDevice device = ADlist.get(position);

                        try {
                            Method method = device.getClass().getMethod("createBond", (Class[]) null);
                            Toast.makeText(context, "Pairing...", Toast.LENGTH_SHORT).show();
                            method.invoke(device, (Object[]) null);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foundDevicelist.size();
    }

    private BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(context, "Paired", Toast.LENGTH_LONG).show();
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    Toast.makeText(context, "Unpaired", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        context.unregisterReceiver(mPairReceiver);
        mPairReceiver = null;
    }
}
