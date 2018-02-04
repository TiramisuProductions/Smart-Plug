package smartplug.app.myapplication.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import java.util.ArrayList;

import smartplug.app.myapplication.FlashApplication;
import smartplug.app.myapplication.Models.Device;
import smartplug.app.myapplication.Models.FoundDevices;
import smartplug.app.myapplication.R;

/**
 * Created by Sarvesh Palav on 1/26/2018.
 */

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.MyViewHolderF> {
    Context context;
    ArrayList<Device> devicesList = new ArrayList<>();
    PubNub pubnub = new PubNub(FlashApplication.pnConfiguration);

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
    public void onBindViewHolder(final MyViewHolderF holder,final  int position) {

holder.deviceName.setText(devicesList.get(position).getName());

if(devicesList.get(position).isStatus())
{
    holder.deviceStatus.setChecked(true);
    holder.deviceStatus.setText("On");
    JsonObject values = new JsonObject();
    values.addProperty("status", 1);




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
            JsonObject values = new JsonObject();
            values.addProperty("status", 1);


            pubnub.publish()
                    .message(values)
                    .channel(devicesList.get(position).getDeviceId())
                    .async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            // handle publish result, status always present, result if successful
                            // status.isError() to see if error happened
                            if(!status.isError()) {
                                System.out.println("pub timetoken: " + result.getTimetoken());
                            }
                            System.out.println("pub status code: " + status.getStatusCode());
                        }
                    });
        }
        else {
            holder.deviceStatus.setText("Off");
            JsonObject values = new JsonObject();
            values.addProperty("status", 0);


            pubnub.publish()
                    .message(values)
                    .channel(devicesList.get(position).getDeviceId())
                    .async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            // handle publish result, status always present, result if successful
                            // status.isError() to see if error happened
                            if(!status.isError()) {
                                System.out.println("pub timetoken: " + result.getTimetoken());
                            }
                            System.out.println("pub status code: " + status.getStatusCode());
                        }
                    });
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
