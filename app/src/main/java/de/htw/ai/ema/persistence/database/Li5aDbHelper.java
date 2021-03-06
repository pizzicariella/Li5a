package de.htw.ai.ema.persistence.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//TODO eventuell muss diese Klasse nun doch kein Singleton sein?
public class Li5aDbHelper extends SQLiteOpenHelper {

    private static Li5aDbHelper instance = null;
    public static final int DATABASE_VERSION = 1;
    String databaseName;

    private Li5aDbHelper(Context context, String databaseName){
        super(context, databaseName, null, DATABASE_VERSION);
        this.databaseName = databaseName;
    }

    public static synchronized Li5aDbHelper getInstance(Context context, String databaseName){
        if (instance == null){
            instance = new Li5aDbHelper(context, databaseName);
        }
        return instance;
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
        db.execSQL(Li5aContract.AccountEntry.SQL_DELETE_TABLE);
        db.execSQL(Li5aContract.CardEntry.SQL_DELETE_TABLE);
        db.execSQL(Li5aContract.CycleEntry.SQL_DELETE_TABLE);
        db.execSQL(Li5aContract.GameEntry.SQL_DELETE_TABLE);
        db.execSQL(Li5aContract.GameRoundEntry.SQL_DELETE_TABLE);
        db.execSQL(Li5aContract.HandEntry.SQL_DELETE_TABLE);
        db.execSQL(Li5aContract.PlayerEntry.SQL_DELETE_TABLE);
        db.execSQL(Li5aContract.StackEntry.SQL_DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}
