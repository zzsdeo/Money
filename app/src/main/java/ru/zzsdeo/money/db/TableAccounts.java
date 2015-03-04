package ru.zzsdeo.money.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableAccounts {

    public static final String TABLE_NAME = "accounts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CARD_NUMBER = "card_number";
    public static final String COLUMN_BALANCE = "balance";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_CARD_NUMBER + " integer not null, "
            + COLUMN_BALANCE + " real not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TableAccounts.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

}
