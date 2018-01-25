package smartplug.app.myapplication.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import smartplug.app.myapplication.Models.PairedDevices;
import smartplug.app.myapplication.R;

/**
 * Created by Siddhant Naique on 09-01-2018.
 */

public class PairedDevicesAdapter extends RecyclerView.Adapter<PairedDevicesAdapter.MyViewHolder> {

    Context context;
    ArrayList <PairedDevices> pairedDevicelist = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Dname, Daddress;

        public MyViewHolder(View itemView) {
            super(itemView);

            Dname = (TextView)itemView.findViewById(R.id.device_name);
            Daddress = (TextView)itemView.findViewById(R.id.device_address);
        }
    }

    public PairedDevicesAdapter(Context context1, ArrayList<PairedDevices> list1){
        this.context = context1;
        this.pairedDevicelist = list1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.paired_devices_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final PairedDevices pd = pairedDevicelist.get(position);
        holder.Dname.setText(pd.getPairedDeviceName());
        holder.Daddress.setText(pd.getPairedDeviceAddress());
        holder.Dname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+pd.getPairedDeviceName(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pairedDevicelist.size();
    }
}
