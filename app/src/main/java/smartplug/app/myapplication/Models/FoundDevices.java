package smartplug.app.myapplication.Models;

/**
 * Created by Siddhant Naique on 13-01-2018.
 */

public class FoundDevices {
    private String foundDeviceName, foundDeviceAddress;

    public FoundDevices(){}

    public FoundDevices(String foundDeviceName, String foundDeviceAddress) {
        this.foundDeviceName = foundDeviceName;
        this.foundDeviceAddress = foundDeviceAddress;
    }

    public String getFoundDeviceName() {
        return foundDeviceName;
    }

    public String getFoundDeviceAddress() {
        return foundDeviceAddress;
    }

    public void setFoundDeviceName(String foundDeviceName) {
        this.foundDeviceName = foundDeviceName;
    }

    public void setFoundDeviceAddress(String foundDeviceAddress) {
        this.foundDeviceAddress = foundDeviceAddress;
    }
}
