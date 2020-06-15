package de.htw.ai.ema.persistence.database;

import android.provider.BaseColumns;

public final class GameContract {

    private GameContract(){}

    public static class GameEntry implements BaseColumns{
        public static final String TABLE_NAME = "game";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_PLAYER0 = "player0";
        public static final String COLUMN_NAME_PLAYER1 = "player1";
        public static final String COLUMN_NAME_PLAYER2 = "player2";
        public static final String COLUMN_NAME_PLAYER3 = "player3";
        public static final String COLUMN_NAME_OVER = "over";
        public static final String COLUMN_NAME_GAMEROUND = "gameround";
    }
}
