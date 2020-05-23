package de.htw.ai.ema.gui;

import androidx.appcompat.app.AppCompatActivity;
import de.htw.ai.ema.R;
import de.htw.ai.ema.control.Control;

import android.os.Bundle;

public class PlayGameActivity extends AppCompatActivity {

    Control controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
    }
}
