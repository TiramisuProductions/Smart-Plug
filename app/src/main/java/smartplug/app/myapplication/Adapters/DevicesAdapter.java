package smartplug.app.myapplication.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import smartplug.app.myapplication.Models.Device;
import smartplug.app.myapplication.Models.FoundDevices;
import smartplug.app.myapplication.R;

/**
 * Created by Sarvesh Palav on 1/26/2018.
 */

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.MyViewHolderF> {
    Context context;
    ArrayList<Device> devicesList = new ArrayList<>();

    public DevicesAdapter(Context context, ArrayList<Device> devicesList) {
        this.context = context;
        this.devicesList = devicesList;
    }

    @Override
    public MyViewHolderF onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_devices, parent, false);

        return new DevicesAdapter.MyViewHolderF(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolderF holder, int position) {

holder.deviceName.setText(devicesList.get(position).getName());

if(devicesList.get(position).isStatus())
{
    holder.deviceStatus.setChecked(true);
    holder.deviceStatus.setText("On");
}
else{
    holder.deviceStatus.setChecked(false);
    holder.deviceStatus.setText("Off");
}
holder.deviceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b)
        {
           holder.deviceStatus.setText("On");
        }
        else {
            holder.deviceStatus.setText("Off");
        }
    }
});
    }

    @Override
    public int getItemCount() {
        return devicesList.size();
    }

    public class MyViewHolderF extends RecyclerView.ViewHolder {
        TextView deviceName;
        SwitchCompat deviceStatus;

        public MyViewHolderF(View itemView) {
            super(itemView);

           deviceName = (TextView)itemView.findViewById(R.id.deviceName);
           deviceStatus = (SwitchCompat)itemView.findViewById(R.id.deviceStatus);
        }
    }
}
