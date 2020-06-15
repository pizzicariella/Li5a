package de.htw.ai.ema.persistence.database;

import android.provider.BaseColumns;

public class PlayerContract {

    private PlayerContract(){}

    public static class PlayerEntry implements BaseColumns{
        public static final String TABLE_NAME = "player";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_HAND = "hand";
        public static final String COLUMN_NAME_ACCOUNT = "account";
        public static final String COLUMN_NAME_TOTALSCORE = "totalscore";
        public static final String COLUMN_NAME_LAST_COLLECTOR = "last_collector";
    }
}
