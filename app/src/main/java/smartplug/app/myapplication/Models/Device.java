package smartplug.app.myapplication.Models;

/**
 * Created by Sarvesh Palav on 1/26/2018.
 */

public class Device {

    String deviceId;
    String name;
    boolean status;

    public Device() {
    }

    public Device(String deviceId, String name, boolean status) {
        this.deviceId = deviceId;
        this.name = name;
        this.status = status;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getName() {
        return name;
    }

    public boolean isStatus() {
        return status;
    }
}
