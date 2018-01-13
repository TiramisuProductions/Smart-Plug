package smartplug.app.myapplication.Models;

/**
 * Created by Siddhant Naique on 09-01-2018.
 */

public class PairedDevices {
    private String pairedDeviceName, pairedDeviceAddress;

    public PairedDevices(){}

    public PairedDevices(String pairedDeviceName, String pairedDeviceAddress) {
        this.pairedDeviceName = pairedDeviceName;
        this.pairedDeviceAddress = pairedDeviceAddress;
    }

    public void setPairedDeviceName(String pairedDeviceName) {
        this.pairedDeviceName = pairedDeviceName;
    }

    public void setPairedDeviceAddress(String pairedDeviceAddress) {
        this.pairedDeviceAddress = pairedDeviceAddress;
    }

    public String getPairedDeviceName() {
        return pairedDeviceName;
    }

    public String getPairedDeviceAddress() {
        return pairedDeviceAddress;
    }
}
