package smartplug.app.myapplication;

import android.*;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
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
import android.support.v7.widget.Toolbar;
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
    Button refes;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDeviceset;
    private ArrayList<PairedDevices> pairedDevicesList1 = new ArrayList<>();
    private ArrayList<FoundDevices> foundDevicesList1 = new ArrayList<>();

    private RecyclerView recyclerViewPairedDevices, recyclerViewFoundDevices;
    private PairedDevicesAdapter pairedDevicesAdapter;
    private FoundDeviceAdapter foundDeviceAdapter;
    Context context;
    FoundDevices fd;
    BluetoothDevice device;
    int signal;

    String tempDevices;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Available Devices");
        setSupportActionBar(toolbar);

        refes = (Button) findViewById(R.id.refresh);

        refes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myBluetooth.isDiscovering()){
                    myBluetooth.cancelDiscovery();
                }
                foundDevices();
            }
        });


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
        recyclerViewFoundDevices = (RecyclerView) findViewById(R.id.recycler_view_found_devices);


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

    @TargetApi(Build.VERSION_CODES.M)
    private void foundDevices() {
        bluetoothPermission();
        foundDevicesList1.clear();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


        final BroadcastReceiver mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                signal = 0;

                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {


                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                    {checkDuplicateBtDevice();}

                    BluetoothClass bluetoothClass = device.getBluetoothClass();
                    int deviceClass = bluetoothClass.getMajorDeviceClass();

                    String btClass = findBtClass(deviceClass);

                    tempDevices = device.getName() + "\n" + device.getAddress() + "\n" + btClass + "\n" + device.getType() + "\n" + device.getBondState();
                    Toast.makeText(getApplicationContext(), "run : " + tempDevices, Toast.LENGTH_SHORT).show();
                }

                foundDeviceAdapter = new FoundDeviceAdapter(context, foundDevicesList1);
                RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
                recyclerViewFoundDevices.setLayoutManager(manager);
                recyclerViewFoundDevices.setItemAnimator(new DefaultItemAnimator());
                recyclerViewFoundDevices.removeAllViews();
                recyclerViewFoundDevices.setAdapter(foundDeviceAdapter);
            }
        };

        registerReceiver(mReceiver, filter);
        myBluetooth.startDiscovery();
    }

    public void checkDuplicateBtDevice(){
        if (foundDevicesList1.size() == 0) {
            fd = new FoundDevices(device.getName(), device.getAddress());
            foundDevicesList1.add(fd);

        } else {
            try {

                for (FoundDevices foundD : foundDevicesList1) {

                    if (foundD.getFoundDeviceAddress().equals(device.getAddress())) {

                        signal = 1;
                    }
                }
                if (signal == 0) {
                    fd = new FoundDevices(device.getName(), device.getAddress());
                    foundDevicesList1.add(fd);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void bluetoothPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);

            }
        }
    }

    //to check the (class)type of device
    private String findBtClass(int major) {
        switch (major) {
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return "AUDIO_VIDEO";
            case BluetoothClass.Device.Major.COMPUTER:
                return "COMPUTER";
            case BluetoothClass.Device.Major.HEALTH:
                return "HEALTH";
            case BluetoothClass.Device.Major.IMAGING:
                return "IMAGING";
            case BluetoothClass.Device.Major.MISC:
                return "MISC";
            case BluetoothClass.Device.Major.NETWORKING:
                return "NETWORKING";
            case BluetoothClass.Device.Major.PERIPHERAL:
                return "PERIPHERAL";
            case BluetoothClass.Device.Major.PHONE:
                return "PHONE";
            case BluetoothClass.Device.Major.TOY:
                return "TOY";
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return "UNCATEGORIZED";
            case BluetoothClass.Device.Major.WEARABLE:
                return "WEARABLE";
            default:
                return "unknown";
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        myBluetooth.cancelDiscovery();
    }
    /*
    * fefhewufhwuef
    * efiuwbfdsfius
    * nsfsndfjksdnfkjsd
    * sfsnfkjsnbdf
    * dfosdfjdsnkfds
    * dfodsnfkjdsnfkj*/


}
/*
    * fefhewufhwuef
    * efiuwbfdsfius
    * nsfsndfjksdnfkjsd
    * sfsnfkjsnbdf
    * dfosdfjdsnkfds
    * dfodsnfkjdsnfkj*/