package ru.zzsdeo.money.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.zzsdeo.money.db.DatabaseContentProvider;
import ru.zzsdeo.money.db.TableCategories;

public class Category {
    private final ContentResolver contentResolver;
    private final long id;

    public Category(Context context, long id) {
        contentResolver = context.getContentResolver();
        this.id = id;
    }

    public long getCategoryId() {
        return id;
    }

    public void setCategoryName(String name) {
        ContentValues cv = new ContentValues();
        cv.put(TableCategories.COLUMN_NAME, name);
        updateDb(cv);
    }

    public String getCategoryName() {
        Cursor c = getCursor(TableCategories.COLUMN_NAME);
        c.moveToFirst();
        String name = c.getString(c.getColumnIndex(TableCategories.COLUMN_NAME));
        c.close();
        return name;
    }

    public void setBudget(float budget) {
        ContentValues cv = new ContentValues();
        cv.put(TableCategories.COLUMN_BUDGET, budget);
        updateDb(cv);
    }

    public float getBudget() {
        Cursor c = getCursor(TableCategories.COLUMN_BUDGET);
        c.moveToFirst();
        float budget = c.getFloat(c.getColumnIndex(TableCategories.COLUMN_NAME));
        c.close();
        return budget;
    }

    private Cursor getCursor(String column) {
        return contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_CATEGORIES,
                new String[]{
                        column
                },
                TableCategories.COLUMN_ID + "=" + id,
                null,
                null
        );
    }

    private void updateDb(ContentValues cv) {
        contentResolver.update(
                DatabaseContentProvider.CONTENT_URI_CATEGORIES,
                cv,
                TableCategories.COLUMN_ID + "=" + id,
                null
        );
    }
}