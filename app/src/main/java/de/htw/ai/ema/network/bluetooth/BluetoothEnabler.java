package de.htw.ai.ema.network.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BluetoothEnabler {

    private static final int REQUEST_ENABLE_BT = 1;

    public void enableBluetooth(Activity activity){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        //TODO Ergebnis bearbeiten in onActivityResult
    }

}
