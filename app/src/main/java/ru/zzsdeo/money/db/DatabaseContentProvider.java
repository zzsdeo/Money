package ru.zzsdeo.money.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class DatabaseContentProvider extends ContentProvider {

    public static final Uri CONTENT_URI_ACCOUNTS = Uri.parse("content://");
    public static final Uri CONTENT_URI_TRANSACTIONS = Uri.parse("content://");
    public static final Uri CONTENT_URI_SCHEDULED_TRANSACTIONS = Uri.parse("content://");
    public static final Uri CONTENT_URI_CATEGORIES = Uri.parse("content://");


    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
