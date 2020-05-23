package de.htw.ai.ema.gui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.htw.ai.ema.R;
import de.htw.ai.ema.network.bluetooth.BluetoothConnector;
import de.htw.ai.ema.network.bluetooth.BluetoothProperties;
import de.htw.ai.ema.network.service.handler.ConnectionHandler;
import de.htw.ai.ema.network.service.listener.ReceiveListener;
import de.htw.ai.ema.network.service.nToM.NToMConnectionHandler;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


public class JoinGameActivity extends AppCompatActivity {

    private BluetoothConnector btConnector;
    private RecyclerView recyclerView;
    private static RecyclerView.Adapter deviceAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private BluetoothDevice selected;
    private final String TAG = "Join Game Activity";
    private static List<BluetoothDevice> availableDevices;
    private BroadcastReceiver receiver;
    private NToMConnectionHandler connectionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        this.selected = null;
        this.btConnector = new BluetoothConnector(false);
        this.connectionHandler = new NToMConnectionHandler(this.btConnector.getDeviceName());
        this.availableDevices = btConnector.getKnownDevices();
        this.receiver = btConnector.getBluetoothDeviceReceiver();
        //auslagern nach connector?
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        Log.println(Log.INFO, TAG, "registered receiver");
        //TODO user ausgabe, dass nach geräten gesucht wird
        btConnector.discoverDevices(this);

        recyclerView = (RecyclerView) findViewById(R.id.available_devices);

        //recyclerView.setHasFixedSize(true); ja nein?
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        deviceAdapter = new DeviceAdapter(availableDevices);
        recyclerView.setAdapter(deviceAdapter);
    }

    public static void addDevice(BluetoothDevice device){
        if (!availableDevices.contains(device)) {
            availableDevices.add(device);
            deviceAdapter.notifyItemInserted(availableDevices.size()-1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void connectWithSelectedDevice(View view){
        if(selected != null) {
            System.out.println(selected.getAddress() + " " + selected.getName());
            btConnector.connect(this, selected);
            TextView waitTextView = (TextView) findViewById(R.id.wait_text_view_join);
            waitTextView.setVisibility(View.VISIBLE);
            try {
                btConnector.getConnectThread().join();
            } catch (InterruptedException e) {
                Log.e(TAG, "couldn't join connect thread", e);
            }
            BluetoothProperties btProps = BluetoothProperties.getInstance();
            Map<String, BluetoothSocket> sockets = btProps.getSockets();
            if(sockets != null){
                for(BluetoothSocket socket: sockets.values()){
                    try{
                        connectionHandler.addReceiveListener(received -> {
                            String message = new String(received, StandardCharsets.UTF_8).trim();
                            Log.println(Log.INFO, TAG, "Received the following message: "+ message);
                            if(message.equals("connectingDone")){
                                Log.println(Log.INFO, TAG, "all 4 players are connected and " +
                                        "the game can start. Jippiiiieee");
                                goToGameActivity();
                            }
                        });
                        connectionHandler.handleConnection(socket.getInputStream(), socket.getOutputStream());
                    } catch (IOException e) {
                        Log.e(TAG, "Error getting input or output stream", e);
                    }
                }
            } else {
                Log.println(Log.INFO, TAG, "sockets were null");
            }
        } else {
            //TODO Ausgabe bauen
            Log.println(Log.INFO, TAG, "No Device selected");
        }
    }

    public void goToGameActivity(){
        Intent intent = new Intent(this, PlayGameActivity.class);
        startActivity(intent);
    }

    public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

        private List<BluetoothDevice> devices;
        public int currentDevice;

        public class DeviceViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;
            public DeviceViewHolder(TextView v){
                super(v);
                textView = v;
            }
        }

        public DeviceAdapter(List<BluetoothDevice> devices){
            this.devices = devices;
        }

        @Override
        public DeviceAdapter.DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.device_text_view, parent, false);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView)v;
                    tv.setBackgroundColor(Color.GREEN);
                    JoinGameActivity.this.selected = devices.get(tv.getId());
                }
            };
            v.setOnClickListener(onClickListener);
            DeviceViewHolder vh = new DeviceViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(DeviceViewHolder holder, int position){
            holder.textView.setId(position);
            holder.textView.setText(devices.get(position).getName());
        }

        @Override
        public int getItemCount(){
            return devices.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
