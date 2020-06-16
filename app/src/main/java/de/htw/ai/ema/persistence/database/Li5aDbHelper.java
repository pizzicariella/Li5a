package de.htw.ai.ema.persistence.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Li5aDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Li5a.db";

    public Li5aDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Li5aContract.AccountEntry.SQL_CREATE_ENTRIES);
        db.execSQL(Li5aContract.CardEntry.SQL_CREATE_ENTRIES);
        db.execSQL(Li5aContract.CycleEntry.SQL_CREATE_ENTRIES);
        db.execSQL(Li5aContract.GameEntry.SQL_CREATE_ENTRIES);
        db.execSQL(Li5aContract.GameRoundEntry.SQL_CREATE_ENTRIES);
        db.execSQL(Li5aContract.HandEntry.SQL_CREATE_ENTRIES);
        db.execSQL(Li5aContract.PlayerEntry.SQL_CREATE_ENTRIES);
        db.execSQL(Li5aContract.StackEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //TODO
    }
}
