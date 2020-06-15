package de.htw.ai.ema.persistence.database;

import android.provider.BaseColumns;

public class GameRoundContract {

    private GameRoundContract(){}

    public static class GameRoundEntry implements BaseColumns{
        public static final String TABLE_NAME = "game_round";
        public static final String COLUMN_NAME_CYCLE = "cycle";
        public static final String COLUMN_NAME_ROUND_NUMBER = "round_number";
    }
}
