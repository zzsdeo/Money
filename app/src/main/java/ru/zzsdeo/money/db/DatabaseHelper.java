package ru.zzsdeo.money.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "money.db";
    private static final String FILE_DIR = "database";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context,
                context.getExternalFilesDir(null)
                        + File.separator
                        + FILE_DIR
                        + File.separator
                        + DATABASE_NAME,
                null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        TableScheduledTransactions.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        TableScheduledTransactions.onUpgrade(database, oldVersion, newVersion);
    }
}
