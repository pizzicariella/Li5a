package de.htw.ai.ema.persistence.database;

import android.provider.BaseColumns;

public class Li5aContract {

    private Li5aContract(){}

    public static class AccountEntry implements BaseColumns {

        //TODO / remember: bei max. 4 Spalten in account müssen die punkte der Karten nach jedem cycle totalscore hinzugefügt werden
        // oder doch lieber mehr spalten? oder die account klasse komplett löschen?!
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_NAME_CARD0 = "card0";
        public static final String COLUMN_NAME_CARD1 = "card1";
        public static final String COLUMN_NAME_CARD2 = "card2";
        public static final String COLUMN_NAME_CARD3 = "card3";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + AccountEntry.TABLE_NAME + " (" +
                        AccountEntry._ID + " INTEGER PRIMARY KEY," +
                        AccountEntry.COLUMN_NAME_CARD0 + " INTEGER," +
                        AccountEntry.COLUMN_NAME_CARD1 + " INTEGER," +
                        AccountEntry.COLUMN_NAME_CARD2 + " INTEGER," +
                        AccountEntry.COLUMN_NAME_CARD3 + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DELETE FROM " + AccountEntry.TABLE_NAME;

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME;
    }

    public static class CardEntry implements BaseColumns {
        public static final String TABLE_NAME = "card";
        public static final String COLUMN_NAME_SUIT = "suit";
        public static final String COLUMN_NAME_RANK = "rank";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + CardEntry.TABLE_NAME + " (" +
                        CardEntry._ID + " INTEGER PRIMARY KEY," +
                        CardEntry.COLUMN_NAME_SUIT + " TEXT," +
                        CardEntry.COLUMN_NAME_RANK + " TEXT)";

        public static final String SQL_DELETE_ENTRIES =
                "DELETE FROM " + CardEntry.TABLE_NAME;

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + CardEntry.TABLE_NAME;
    }

    public static class CycleEntry implements BaseColumns{
        public static final String TABLE_NAME = "cycle";
        public static final String COLUMN_NAME_STACK = "stack";
        public static final String COLUMN_NAME_CYCLE_NUMBER = "cycleNumber";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + CycleEntry.TABLE_NAME + " (" +
                        CycleEntry._ID + " INTEGER PRIMARY KEY," +
                        CycleEntry.COLUMN_NAME_STACK + " INTEGER," +
                        CycleEntry.COLUMN_NAME_CYCLE_NUMBER + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DELETE FROM " + CycleEntry.TABLE_NAME;

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + CycleEntry.TABLE_NAME;
    }

    public static class GameEntry implements BaseColumns{
        public static final String TABLE_NAME = "game";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_PLAYER0 = "player0";
        public static final String COLUMN_NAME_PLAYER1 = "player1";
        public static final String COLUMN_NAME_PLAYER2 = "player2";
        public static final String COLUMN_NAME_PLAYER3 = "player3";
        public static final String COLUMN_NAME_OVER = "over";
        public static final String COLUMN_NAME_GAMEROUND = "gameround";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + GameEntry.TABLE_NAME + " (" +
                        GameEntry._ID + " INTEGER PRIMARY KEY," +
                        GameEntry.COLUMN_NAME_NAME + " TEXT," +
                        GameEntry.COLUMN_NAME_DATE + " NUMERIC," +
                        GameEntry.COLUMN_NAME_PLAYER0 + " INTEGER," +
                        GameEntry.COLUMN_NAME_PLAYER1 + " INTEGER," +
                        GameEntry.COLUMN_NAME_PLAYER2 + " INTEGER," +
                        GameEntry.COLUMN_NAME_PLAYER3 + " INTEGER," +
                        GameEntry.COLUMN_NAME_OVER + " INTEGER," +
                        GameEntry.COLUMN_NAME_GAMEROUND + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DELETE FROM " + GameEntry.TABLE_NAME;

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + GameEntry.TABLE_NAME;
    }

    public static class GameRoundEntry implements BaseColumns{
        public static final String TABLE_NAME = "game_round";
        public static final String COLUMN_NAME_CYCLE = "cycle";
        public static final String COLUMN_NAME_ROUND_NUMBER = "round_number";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + GameRoundEntry.TABLE_NAME + " (" +
                        GameRoundEntry._ID + " INTEGER PRIMARY KEY," +
                        GameRoundEntry.COLUMN_NAME_CYCLE + " INTEGER," +
                        GameRoundEntry.COLUMN_NAME_ROUND_NUMBER + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DELETE FROM " + GameRoundEntry.TABLE_NAME;

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + GameRoundEntry.TABLE_NAME;
    }

    public static class HandEntry implements BaseColumns {
        public static final String TABLE_NAME = "hand";
        public static final String COLUMN_NAME_CARD0 = "card0";
        public static final String COLUMN_NAME_CARD1 = "card1";
        public static final String COLUMN_NAME_CARD2 = "card2";
        public static final String COLUMN_NAME_CARD3 = "card3";
        public static final String COLUMN_NAME_CARD4 = "card4";
        public static final String COLUMN_NAME_CARD5 = "card5";
        public static final String COLUMN_NAME_CARD6 = "card6";
        public static final String COLUMN_NAME_CARD7 = "card7";
        public static final String COLUMN_NAME_CARD8 = "card8";
        public static final String COLUMN_NAME_CARD9 = "card9";
        public static final String COLUMN_NAME_CARD10 = "card10";
        public static final String COLUMN_NAME_CARD11 = "card11";
        public static final String COLUMN_NAME_CARD12 = "card12";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + HandEntry.TABLE_NAME + " (" +
                        HandEntry._ID + " INTEGER PRIMARY KEY," +
                        HandEntry.COLUMN_NAME_CARD0 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD1 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD2 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD3 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD4 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD5 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD6 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD7 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD8 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD9 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD10 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD11 + " INTEGER," +
                        HandEntry.COLUMN_NAME_CARD12 + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DELETE FROM " + HandEntry.TABLE_NAME;

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + HandEntry.TABLE_NAME;
    }

    public static class PlayerEntry implements BaseColumns{
        public static final String TABLE_NAME = "player";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_HAND = "hand";
        public static final String COLUMN_NAME_ACCOUNT = "account";
        public static final String COLUMN_NAME_TOTALSCORE = "totalscore";
        public static final String COLUMN_NAME_LAST_COLLECTOR = "last_collector";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + PlayerEntry.TABLE_NAME + " (" +
                        PlayerEntry._ID + " INTEGER PRIMARY KEY," +
                        PlayerEntry.COLUMN_NAME_NAME + " TEXT," +
                        PlayerEntry.COLUMN_NAME_HAND + " INTEGER," +
                        PlayerEntry.COLUMN_NAME_ACCOUNT + " INTEGER," +
                        PlayerEntry.COLUMN_NAME_TOTALSCORE + " INTEGER," +
                        PlayerEntry.COLUMN_NAME_LAST_COLLECTOR + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DELETE FROM " + PlayerEntry.TABLE_NAME;

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + PlayerEntry.TABLE_NAME;
    }

    public static class StackEntry implements BaseColumns{
        public static final String TABLE_NAME = "stack";
        public static final String COLUMN_NAME_CARD1 = "card1";
        public static final String COLUMN_NAME_CARD2 = "card2";
        public static final String COLUMN_NAME_CARD3 = "card3";
        public static final String COLUMN_NAME_CARD4 = "card4";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + StackEntry.TABLE_NAME + " (" +
                        StackEntry._ID + " INTEGER PRIMARY KEY," +
                        StackEntry.COLUMN_NAME_CARD1 + " INTEGER," +
                        StackEntry.COLUMN_NAME_CARD2 + " INTEGER," +
                        StackEntry.COLUMN_NAME_CARD3 + " INTEGER," +
                        StackEntry.COLUMN_NAME_CARD4 + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DELETE FROM " + StackEntry.TABLE_NAME;

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + StackEntry.TABLE_NAME;
    }

}
