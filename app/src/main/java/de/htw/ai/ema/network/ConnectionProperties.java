package de.htw.ai.ema.network;

import de.htw.ai.ema.network.bluetooth.BluetoothProperties;
import de.htw.ai.ema.network.service.handler.ConnectionHandler;
import de.htw.ai.ema.network.service.nToM.NToMConnectionHandler;

public class ConnectionProperties {

    private static ConnectionProperties instance = null;
    private ConnectionHandler conHandler = null;

    private ConnectionProperties(String name){
        this.conHandler = new NToMConnectionHandler(name);
    }

    public static synchronized ConnectionProperties getInstance(){
        if(instance == null){
            instance = new ConnectionProperties(BluetoothProperties.getInstance().getDeviceName());
        }
        return instance;
    }

    public ConnectionHandler getConHandler() {
        return this.conHandler;
    }
}
