package ru.zzsdeo.money.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableTransactions {

    public static final String TABLE_NAME = "transactions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ACCOUNT_ID = "account_id";
    public static final String COLUMN_DATE_IN_MILL = "date_in_mill";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_COMMISSION = "commission";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_IS_APPROVED = "is_approved";
    public static final String COLUMN_DESTINATION_ACCOUNT_ID = "destination_account_id";
    public static final String COLUMN_NEED_APPROVE = "need_approve";
    public static final String COLUMN_REPEATING_TYPE_ID = "repeating_type_id";
    public static final String COLUMN_CATEGORY_ID = "category_id";


    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ACCOUNT_ID + " integer not null, "
            + COLUMN_DATE_IN_MILL + " integer not null, "
            + COLUMN_AMOUNT + " real not null, "
            + COLUMN_COMMISSION + " real not null, "
            + COLUMN_COMMENT + " text not null, "
            + COLUMN_IS_APPROVED + " integer not null, "
            + COLUMN_DESTINATION_ACCOUNT_ID + " integer not null, "
            + COLUMN_NEED_APPROVE + " integer not null, "
            + COLUMN_REPEATING_TYPE_ID + " integer not null, "
            + COLUMN_CATEGORY_ID + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TableTransactions.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

}
