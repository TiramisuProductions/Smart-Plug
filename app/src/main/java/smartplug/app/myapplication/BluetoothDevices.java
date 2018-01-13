package smartplug.app.myapplication;

import android.*;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.jar.*;

import smartplug.app.myapplication.Adapters.FoundDeviceAdapter;
import smartplug.app.myapplication.Adapters.PairedDevicesAdapter;
import smartplug.app.myapplication.Models.FoundDevices;
import smartplug.app.myapplication.Models.PairedDevices;

public class BluetoothDevices extends AppCompatActivity {
    //ListView devicelist;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDeviceset;
    private ArrayList<PairedDevices> pairedDevicesList1 = new ArrayList<>();
    private ArrayList<FoundDevices> foundDevicesList1 = new ArrayList<>();

    private RecyclerView recyclerViewPairedDevices, recyclerViewFoundDevices;
    private PairedDevicesAdapter pairedDevicesAdapter;
    private FoundDeviceAdapter foundDeviceAdapter;
    Context context;
    FoundDevices fd;

    String tempDevices;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

        bluetoothPermission();

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        } else {
            if (myBluetooth.isEnabled()) {
            } else {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
            }
        }
        pairedDevicesList();
        foundDevices();

        context = getApplicationContext();

        //Paired Recycler and adapter
        recyclerViewPairedDevices = (RecyclerView) findViewById(R.id.recycler_view_paired_devices);
        pairedDevicesAdapter = new PairedDevicesAdapter(context, pairedDevicesList1);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerViewPairedDevices.setLayoutManager(mLayoutManager);
        recyclerViewPairedDevices.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPairedDevices.setAdapter(pairedDevicesAdapter);

        //Found Recycler and Adapter


    }

    private void pairedDevicesList() {
        pairedDeviceset = myBluetooth.getBondedDevices();
        //ArrayList pairedList = new ArrayList();
        PairedDevices pd;

        if (pairedDeviceset.size() > 0) {
            for (BluetoothDevice bt : pairedDeviceset) {

                pd = new PairedDevices(bt.getName(), bt.getAddress());
                pairedDevicesList1.add(pd);
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
    }

    private void foundDevices() {


        Toast.makeText(getApplicationContext(), "insidefound", Toast.LENGTH_SHORT).show();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


        final BroadcastReceiver mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "insidefound2", Toast.LENGTH_SHORT).show();
                String action = intent.getAction();


                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    Toast.makeText(getApplicationContext(), "Device found", Toast.LENGTH_SHORT).show();
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);


//                    fd.setFoundDeviceName(device.getName());
                    Log.d("croco", "" + device.getName());
                    //fd.setFoundDeviceAddress(device.getAddress());

                    fd = new FoundDevices(device.getName(), device.getAddress());
                    foundDevicesList1.add(fd);

                    tempDevices = device.getName() + "\n" + device.getAddress();

                    Toast.makeText(getApplicationContext(), "" + tempDevices + "\n" + rssi, Toast.LENGTH_SHORT).show();

                    recyclerViewFoundDevices = (RecyclerView) findViewById(R.id.recycler_view_found_devices);
                    foundDeviceAdapter = new FoundDeviceAdapter(context, foundDevicesList1);
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
                    recyclerViewFoundDevices.setLayoutManager(manager);
                    recyclerViewFoundDevices.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewFoundDevices.setAdapter(foundDeviceAdapter);

                }
            }
        };


        registerReceiver(mReceiver, filter);
        myBluetooth.startDiscovery();
        if (myBluetooth.isDiscovering()) {

            myBluetooth.cancelDiscovery();
            myBluetooth.startDiscovery();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void bluetoothPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //myBluetooth.cancelDiscovery();
    }
}
