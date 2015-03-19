package ru.zzsdeo.money.model;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import ru.zzsdeo.money.db.DatabaseContentProvider;
import ru.zzsdeo.money.db.TableAccounts;
import ru.zzsdeo.money.db.TableCategories;

public class CategoryCollection extends LinkedHashMap<Long, Category> {

    private final ContentResolver contentResolver;
    private final Context context;

    public CategoryCollection(Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_CATEGORIES,
                new String[]{
                        TableCategories.COLUMN_ID
                },
                null,
                null,
                null
        );
        long id;
        if (c.moveToFirst()) {
            do {
                id = c.getLong(c.getColumnIndex(TableCategories.COLUMN_ID));
                put(id, new Category(context, id));
            } while (c.moveToNext());
        }
        c.close();
    }

    public CategoryCollection(Context context, String[] params) {
        String[] args;
        if (params.length > 2) {
            ArrayList<String> argsList = new ArrayList<>();
            argsList.addAll(Arrays.asList(params).subList(2, params.length));
            args = (String[]) argsList.toArray();
        } else {
            args = null;
        }
        this.context = context;
        contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_CATEGORIES,
                new String[]{
                        TableCategories.COLUMN_ID
                },
                params[0],
                args,
                params[1]
        );
        long id;
        if (c.moveToFirst()) {
            do {
                id = c.getLong(c.getColumnIndex(TableCategories.COLUMN_ID));
                put(id, new Category(context, id));
            } while (c.moveToNext());
        }
        c.close();
    }

    public void addCategory(String name, float budget) {
        ContentValues cv = new ContentValues();
        cv.put(TableCategories.COLUMN_NAME, name);
        cv.put(TableCategories.COLUMN_BUDGET, budget);
        Uri uri = contentResolver.insert(DatabaseContentProvider.CONTENT_URI_CATEGORIES, cv);
        long id = Long.valueOf(uri.getLastPathSegment());
        put(id, new Category(context, id));
    }

    public void removeCategory(long id) {
        int deletedRows = contentResolver.delete(
                DatabaseContentProvider.CONTENT_URI_CATEGORIES,
                TableCategories.COLUMN_ID + "=" + id,
                null
        );
        if (deletedRows > 0) remove(id);
    }
}