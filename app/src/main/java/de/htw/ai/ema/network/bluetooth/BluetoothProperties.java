package de.htw.ai.ema.network.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.util.HashMap;
import java.util.Map;

public class BluetoothProperties {

    private static BluetoothProperties instance = null;
    private Map<String, BluetoothSocket> sockets = null;
    private String deviceName = null;

    private BluetoothProperties(){}

    public static synchronized BluetoothProperties getInstance(){
        if(instance == null){
            instance = new BluetoothProperties();
        }
        return instance;
    }

    public static void setSockets(Map<String, BluetoothSocket> sockets) {
        if(instance == null){
            instance = new BluetoothProperties();
        }
        instance.sockets = sockets;
    }

    public static void setDeviceName(String deviceName){
        if(instance == null){
            instance = new BluetoothProperties();
        }
        instance.deviceName = deviceName;
    }

    public Map<String, BluetoothSocket> getSockets() {
        if(sockets == null){
            this.sockets = new HashMap<>();
        }
        return sockets;
    }

    public String getDeviceName(){return this.deviceName;}
}
