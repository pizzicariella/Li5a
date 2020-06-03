package de.htw.ai.ema.network.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import de.htw.ai.ema.gui.JoinGameActivity;


public class BluetoothConnector4Player implements BluetoothConnector{

    BluetoothAdapter btAdapter;
    private final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    private final UUID LI5A_UUID = UUID.fromString("46e94e5f-e660-4fd0-a179-43f525bc4d78");
    private final String TAG = "BluetoothAdapter";
    private final boolean host;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private String deviceName;

    public BluetoothConnector4Player(boolean host){
        this.host = host;
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            //TODO User Ausgabe bauen
            System.out.println("Device doesn't support Bluetooth");
            Log.println(Log.INFO, TAG, "Device doesn't support bluetooth");
        }
        this.deviceName = btAdapter.getName();
        this.acceptThread = null;
        this.connectThread = null;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public AcceptThread getAcceptThread() {
        return acceptThread;
    }

    public ConnectThread getConnectThread() {
        return connectThread;
    }

    @Override
    public List<BluetoothDevice> getKnownDevices(){
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        List<BluetoothDevice> deviceList = new ArrayList<>(devices);
        return deviceList;
    }

    @Override
    public BroadcastReceiver getBluetoothDeviceReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device.getName() != null) {
                        JoinGameActivity.addDevice(device);
                        Log.println(Log.INFO, TAG, "Found device and added it to list, device name: " + device.getName());
                    }
                }
            }
        };
    }

    @Override
    public void discoverDevices(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]
                        {Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_LOCATION);
        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
            Log.println(Log.INFO, TAG, "already discovering stopped");
        }
        this.btAdapter.startDiscovery();
        Log.println(Log.INFO, TAG, "started Discovery");
    }

    @Override
    public void enableDiscoverability(Activity activity){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //device is discoverable for 5 minutes
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        activity.startActivity(discoverableIntent);
        //TODO Ergebnis bearbeiten in onActivityResult
    }

    @Override
    public void accept(){
        BluetoothConnector4Player.this.acceptThread = new AcceptThread();
        if(BluetoothConnector4Player.this.acceptThread.getBtServerSocket() != null){
           BluetoothConnector4Player.this.acceptThread.start();
        }
    }

    //opens server socket and listens to incoming connections
    public class AcceptThread extends Thread{

        private final BluetoothServerSocket btServerSocket;
        private final String NAME = "Li5a";
        private final String TAG = "AcceptThread";

        public AcceptThread(){
            BluetoothServerSocket tmpServerSocket = null;
            try{
                tmpServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(NAME, LI5A_UUID);
            } catch(IOException e) {
                //TODO what is tag
                Log.e(TAG, "Listen method of socket failed", e);
            }
            btServerSocket = tmpServerSocket;
        }

        public BluetoothServerSocket getBtServerSocket() {
            return btServerSocket;
        }

        public void run(){
            //BluetoothSocket btSocket = null;
            boolean accept = true;
            Map<String, BluetoothSocket> sockets = new HashMap<>();
            while (accept){
                BluetoothSocket btSocket = null;
                try {
                    btSocket = btServerSocket.accept();
                    Log.println(Log.INFO, TAG, "successfully opened socket");
                } catch (IOException e) {
                    Log.e(TAG, "Accept method of socket failed",e);
                    accept = false;
                }

                if(btSocket!=null){
                    String conName = BluetoothConnector4Player.this.deviceName+"To"+btSocket.getRemoteDevice().getName();
                    // TODO or do i have to handle connections from here??
                    // btServerSocket.close(); unless I want more connections??
                    sockets.put(conName, btSocket);
                    Log.println(Log.INFO, TAG, "A connection has been established: "+conName);
                    if(sockets.size() == 3) {
                        accept = false;
                        Log.println(Log.INFO, TAG, "all players connected");
                        try {
                            BluetoothProperties.setSockets(sockets);
                            btServerSocket.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Could not close server socket",e);
                        }
                    }
                }
            }
        }

        public void cancel(){
            try {
                btServerSocket.close();
            } catch(IOException e){
                Log.e(TAG, "could not close socket", e);
            }
        }
    }

    @Override
    public void connect(BluetoothDevice device){

        BluetoothConnector4Player.this.connectThread = new ConnectThread(device);
        if(BluetoothConnector4Player.this.connectThread.getBtSocket() != null){
            BluetoothConnector4Player.this.connectThread.start();
        } else{
            Log.e(TAG, "Bluetooth socket could not be created.");
        }
    }

    public class ConnectThread extends Thread{

        private final BluetoothSocket btSocket;
        private final BluetoothDevice btDevice;
        private final String TAG = "ConnectThread";

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmpBtSocket = null;
            btDevice = device;

            try {
                tmpBtSocket = device.createRfcommSocketToServiceRecord(LI5A_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Create method of socket failed", e);
            }

            btSocket = tmpBtSocket;
        }

        public BluetoothSocket getBtSocket() {
            return btSocket;
        }

        public void run(){

            //ggf. vorher checken mit isDiscovering()
            btAdapter.cancelDiscovery();

            try {
                if(btDevice == null){
                    Log.e(TAG, "Device is null");
                }
                if(!btSocket.isConnected()) {
                    btSocket.connect();
                }
            } catch (IOException e) {
                Log.e(TAG, "Could not connect", e);
                try {
                    btSocket.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Could not close socket", ex);
                }
                //TODO hier sollte noch etwas anderes passieren
                return;
            }
            String conName = btAdapter.getName()+"To"+btSocket.getRemoteDevice().getName();
            Log.println(Log.INFO, TAG, "successfully established connection: "+conName);
            Map<String, BluetoothSocket> sockets = new HashMap<>();
            sockets.put(conName, btSocket);
            BluetoothProperties.setSockets(sockets);
        }

        public void cancel(){
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close socket", e);
            }
        }
    }
}
