package de.htw.ai.ema.gui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import de.htw.ai.ema.R;
import de.htw.ai.ema.network.bluetooth.BluetoothEnabler;

public class MainActivity extends AppCompatActivity {

    public BluetoothEnabler btEnabler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btEnabler = new BluetoothEnabler();
        this.btEnabler.enableBluetooth(this);
    }

    public void startGame(View view){
        Intent intent = new Intent(this, StartGameActivity.class);
        startActivity(intent);
    }

    public void joinGame(View view){
        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
