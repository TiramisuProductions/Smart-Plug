package smartplug.app.myapplication.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import smartplug.app.myapplication.Adapters.FoundDeviceAdapter;
import smartplug.app.myapplication.Models.FoundDevices;

import smartplug.app.myapplication.R;

/**
 * Created by Siddhant Naique on 13-01-2018.
 */


public class FoundDeviceAdapter extends
        RecyclerView.Adapter<FoundDeviceAdapter.MyViewHolderF> {

    Context context;
    ArrayList<FoundDevices> foundDevicelist = new ArrayList<>();

    public class MyViewHolderF extends RecyclerView.ViewHolder implements
    View.OnClickListener, View.OnLongClickListener{
        TextView Dname, Daddress;
        private ItemClickListener clickListener;

        public MyViewHolderF(View itemView) {
            super(itemView);

            Dname = (TextView)itemView.findViewById(R.id.device_nameF);
            Daddress = (TextView)itemView.findViewById(R.id.device_addressF);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        public void setClickListener(ItemClickListener itemClickListener){
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(itemView, getPosition(),false);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onClick(itemView, getPosition(),false);
            return true;
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

        final FoundDevices fd = foundDevicelist.get(position);
        holder.Dname.setText(fd.getFoundDeviceName());
        holder.Daddress.setText(fd.getFoundDeviceAddress());

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                if (isLongClick) {
                    //Toast.makeText(context, "#" + position + " - " + fd.getFoundDeviceName() + " (Long click)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "#" + position + " - " + fd.getFoundDeviceName(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return foundDevicelist.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }
}
