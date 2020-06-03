package de.htw.ai.ema.network.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import java.util.List;

public interface BluetoothConnector {

    /**
     * Returns a List of Bluetooth Devices that the device already knows.
     * @return List<BluetoothDevice>
     */
    public List<BluetoothDevice> getKnownDevices();

    /**
     * Returns a BroadcastReceiver used that can be used for device discovery.
     * @return BroadcastReceiver
     */
    public BroadcastReceiver getBluetoothDeviceReceiver();

    /**
     * This method starts the bluetooth device discovery.
     * @param activity
     */
    public void discoverDevices(Activity activity);

    /**
     * Enables the discoverability of the device.
     * @param activity
     */
    public void enableDiscoverability(Activity activity);

    /**
     * Starts an accept Thread for Host device.
     */
    public void accept();

    /**
     * Starts a connect Thread for Client device.
     * @param device
     */
    public void connect(BluetoothDevice device);
}
