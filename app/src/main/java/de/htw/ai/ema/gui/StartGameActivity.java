package de.htw.ai.ema.gui;

import androidx.appcompat.app.AppCompatActivity;
import de.htw.ai.ema.R;
import de.htw.ai.ema.network.ConnectionProperties;
import de.htw.ai.ema.network.bluetooth.BluetoothConnector;
import de.htw.ai.ema.network.bluetooth.BluetoothConnector4Player;
import de.htw.ai.ema.network.bluetooth.BluetoothProperties;
import de.htw.ai.ema.network.service.handler.ConnectionHandler;
import de.htw.ai.ema.network.service.nToM.NToMConnectionHandler;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.util.Map;

public class StartGameActivity extends AppCompatActivity {

    //TODO enable User to cancel in case not enough devices can connect
    private final String TAG = "StartGameActivity";
    ConnectionHandler conHandler;
    BluetoothConnector4Player btConnector;
    BluetoothProperties btProps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);
        this.btConnector = new BluetoothConnector4Player(true);
        BluetoothProperties.setDeviceName(btConnector.getDeviceName());
        this.btProps = BluetoothProperties.getInstance();
        this.conHandler = ConnectionProperties.getInstance().getConHandler();
    }

    public void hostGame(View view){
        String playersName = ((EditText) findViewById(R.id.enter_player_name_start)).getText().toString();
        btConnector.enableDiscoverability(this);
        btConnector.accept();
        //this doesnt work
        TextView waitTextViewStart = (TextView) findViewById(R.id.wait_text_view_start);
        waitTextViewStart.setVisibility(View.VISIBLE);
        try {
            btConnector.getAcceptThread().join();
        } catch (InterruptedException e) {
            Log.e(TAG, "Could not join accept Thread", e);
        }
        Map<String, BluetoothSocket> sockets = this.btProps.getSockets();
        if(sockets != null && sockets.size() == 3){
            //notify the other players
            String connectedMessage = "connectingDone";
            for(BluetoothSocket socket: sockets.values()){
                try{
                    this.conHandler.handleConnection(socket.getInputStream(), socket.getOutputStream());
                } catch (IOException e) {
                    Log.e(TAG, "Error getting input or output stream", e);
                }
            }
            this.conHandler.sendMessage(connectedMessage);
            Log.println(Log.INFO, TAG, this.btConnector.getDeviceName()+": I informed everyone that game can start.");
            Intent intent = new Intent(this, PlayGameActivity.class);
            intent.putExtra("playersName", playersName);
            intent.putExtra("host", true);
            startActivity(intent);
        } else {
            Log.println(Log.INFO, TAG, "sockets are null or not 3");
        }
    }
}
