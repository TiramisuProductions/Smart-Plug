package smartplug.app.myapplication;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Sarvesh Palav on 1/26/2018.
 */

public class EventBusEvent {
    private final String message;
    private final BluetoothDevice device;


    public EventBusEvent(String message, BluetoothDevice device) {
        this.message = message;
        this.device = device;
    }

    public String getMessage() {
        return message;
    }

    public BluetoothDevice getDevice() {
        return device;
    }
}
