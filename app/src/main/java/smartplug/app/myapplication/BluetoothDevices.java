package smartplug.app.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothDevices extends AppCompatActivity {
    //ListView devicelist;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayList<PairedDevices> pairedDevicesList1 = new ArrayList<>();

    private RecyclerView recyclerViewPairedDevices;
    private PairedDevicesAdapter pairedDevicesAdapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

        //devicelist = (ListView)findViewById(R.id.listview);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        }
        else
        {
            if (myBluetooth.isEnabled())
            { }
            else
            {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }
        pairedDevicesList(); //method that will be called

        context = getApplicationContext();

        recyclerViewPairedDevices = (RecyclerView) findViewById(R.id.recycler_view_paired_devices);
        pairedDevicesAdapter = new PairedDevicesAdapter(context,pairedDevicesList1);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerViewPairedDevices.setLayoutManager(mLayoutManager);
        recyclerViewPairedDevices.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPairedDevices.setAdapter(pairedDevicesAdapter);

    }

    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        //ArrayList pairedList = new ArrayList();
        PairedDevices pd;

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
               //pairedList.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
                pd = new PairedDevices(bt.getName(),bt.getAddress());
                pairedDevicesList1.add(pd);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
    }
}
