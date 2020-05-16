package de.htw.ai.ema.gui;

import androidx.appcompat.app.AppCompatActivity;
import de.htw.ai.ema.R;
import de.htw.ai.ema.network.bluetooth.BluetoothConnector;
import de.htw.ai.ema.network.bluetooth.BluetoothProperties;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class StartGameActivity extends AppCompatActivity {

    //TODO enable User to cancel in case not enough devices can connect
    private boolean cancel;
    private final String TAG = "StartGameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);
        this.cancel = false;
    }

    public void hostGame(View view){
        BluetoothConnector btConnector = new BluetoothConnector(true);
        btConnector.enableDiscoverability(this);
        btConnector.accept();
        boolean connectingDone = false;
        showWaiting();
        //TODO geht das irgendwie besser?
        while (!connectingDone && !cancel){
            if(BluetoothProperties.getInstance().getSockets() != null
                    && BluetoothProperties.getInstance().getSockets().size() == 4){
                connectingDone = true;
            }
        }
        //TODO inform other players that game can start
        goToGameActivity();
    }

    public TextView showWaiting(){
        Log.println(Log.INFO, TAG, "trying to inflate text view");
        return (TextView) LayoutInflater.from(StartGameActivity.this)
                .inflate(R.layout.wait_for_connections_text_view, null);
    }

    public void goToGameActivity(){
        Intent intent = new Intent(this, PlayGameActivity.class);
        startActivity(intent);
    }
}
