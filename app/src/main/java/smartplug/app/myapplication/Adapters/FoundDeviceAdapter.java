package smartplug.app.myapplication.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import smartplug.app.myapplication.Adapters.FoundDeviceAdapter;
import smartplug.app.myapplication.Models.FoundDevices;

import smartplug.app.myapplication.R;

/**
 * Created by Siddhant Naique on 13-01-2018.
 */

public class FoundDeviceAdapter extends RecyclerView.Adapter<FoundDeviceAdapter.MyViewHolderF> {

    Context context;
    ArrayList<FoundDevices> foundDevicelist = new ArrayList<>();

    public class MyViewHolderF extends RecyclerView.ViewHolder{
        TextView Dname, Daddress;

        public MyViewHolderF(View itemView) {
            super(itemView);

            Dname = (TextView)itemView.findViewById(R.id.device_nameF);
            Daddress = (TextView)itemView.findViewById(R.id.device_addressF);
        }
    }

    public FoundDeviceAdapter(Context context1, ArrayList<FoundDevices> list1){
        this.context = context1;
        this.foundDevicelist = list1;
    }

    @Override
    public FoundDeviceAdapter.MyViewHolderF onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.found_device_row, parent, false);

        return new FoundDeviceAdapter.MyViewHolderF(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolderF holder, int position) {
       // FoundDevices fd = foundDevicelist.get(position);
        FoundDevices fd = foundDevicelist.get(position);
        holder.Dname.setText(fd.getFoundDeviceName());
        holder.Daddress.setText(fd.getFoundDeviceAddress());

    }

    @Override
    public int getItemCount() {
        return foundDevicelist.size();
    }

}
