package smartplug.app.myapplication;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by Sarvesh Palav on 1/9/2018.
 */

public class BluetoothManager
{

    /**
     * Whether current Android device support Bluetooth.
     *
     * @return true：Support Bluetooth false：not support Bluetooth
     */
    public static boolean isBluetoothSupported()
    {
        return BluetoothAdapter.getDefaultAdapter() != null ? true : false;
    }

    /**
     * Whether current Android device Bluetooth is enabled.
     *
     * @return true：Bluetooth is enabled false：Bluetooth not enabled
     */
    public static boolean isBluetoothEnabled()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();

        if (bluetoothAdapter != null)
        {
            return bluetoothAdapter.isEnabled();
        }

        return false;
    }

    /**
     * Force to turn on Bluetooth on Android device.
     *
     * @return true：force to turn on Bluetooth　success　
     * false：force to turn on Bluetooth failure
     */
    public static boolean turnOnBluetooth()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();

        if (bluetoothAdapter != null)
        {
            return bluetoothAdapter.enable();
        }

        return false;
    }
}
