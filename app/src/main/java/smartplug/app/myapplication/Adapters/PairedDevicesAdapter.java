package smartplug.app.myapplication.Adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import smartplug.app.myapplication.BluetoothManager;
import smartplug.app.myapplication.EventBusEvent;
import smartplug.app.myapplication.HomeActivity;
import smartplug.app.myapplication.MainActivity;
import smartplug.app.myapplication.Models.PairedDevices;
import smartplug.app.myapplication.R;
import smartplug.app.myapplication.TurnOnDeviceActivity;

/**
 * Created by Siddhant Naique on 09-01-2018.
 */

public class PairedDevicesAdapter extends RecyclerView.Adapter<PairedDevicesAdapter.MyViewHolder> {

    Context context;
    ArrayList <BluetoothDevice> pairedDevicelist = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean deviceExist = false;
    Context rootViewContext;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Dname, Daddress;

        public MyViewHolder(View itemView) {
            super(itemView);

            Dname = (TextView)itemView.findViewById(R.id.device_name);
            Daddress = (TextView)itemView.findViewById(R.id.device_address);
        }
    }

    public PairedDevicesAdapter(Context context1, ArrayList<BluetoothDevice> list1){
        this.context = context1;
        this.pairedDevicelist = list1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.paired_devices_row, parent, false);
    rootViewContext = itemView.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final BluetoothDevice pd = pairedDevicelist.get(position);
        holder.Dname.setText(pd.getName());
        holder.Daddress.setText(pd.getAddress());
        holder.Dname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+pd.getName(), Toast.LENGTH_LONG).show();
                String uid = "102";


                db.collection("flash").document(FirebaseAuth.getInstance().getUid()).collection("devices")
                        .whereEqualTo("deviceId", uid)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("working","working");
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Log.d("devices", document.getId() + " => " + document.getData());
                                        deviceExist = true;
                                    }
                                    if(deviceExist == true)
                                    {
                                        Toast.makeText(context,"Device Exist",Toast.LENGTH_LONG).show();
                                        deviceExist = false;
                                        EventBus.getDefault().post(new EventBusEvent("Hello EventBus!",pairedDevicelist.get(position)));
                                    }
                                    else{


                                      /*  AlertDialog.Builder builder = new AlertDialog.Builder(rootViewContext);

                                        builder.setTitle("You want to delete");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                // Do nothing but close the dialog

                                            }
                                        });

                                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                // Do nothing
                                                dialog.dismiss();
                                            }
                                        });

                                        AlertDialog alert = builder.create();
                                        alert.show();*/
                                    }



                                } else {
                                    Log.d("devices", "Error getting documents: ", task.getException());
                                }
                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return pairedDevicelist.size();
    }
}
