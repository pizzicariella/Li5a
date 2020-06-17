package de.htw.ai.ema.persistence.database;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.core.app.ActivityCompat;


public class Li5aDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Li5a.db";
    private final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    public Li5aDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        /*ActivityCompat.requestPermissions(activity, new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);*/
        //context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
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
        db.execSQL(Li5aContract.AccountEntry.SQL_DELETE_ENTRIES);
        db.execSQL(Li5aContract.CardEntry.SQL_DELETE_ENTRIES);
        db.execSQL(Li5aContract.CycleEntry.SQL_DELETE_ENTRIES);
        db.execSQL(Li5aContract.GameEntry.SQL_DELETE_ENTRIES);
        db.execSQL(Li5aContract.GameRoundEntry.SQL_DELETE_ENTRIES);
        db.execSQL(Li5aContract.HandEntry.SQL_DELETE_ENTRIES);
        db.execSQL(Li5aContract.PlayerEntry.SQL_DELETE_ENTRIES);
        db.execSQL(Li5aContract.StackEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}
