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

    private String name;
    private float budget;

    public Category(Context context, long id) {
        contentResolver = context.getContentResolver();
        this.id = id;

        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_CATEGORIES,
                null,
                TableCategories.COLUMN_ID + "=" + id,
                null,
                null
        );
        c.moveToFirst();
        name = c.getString(c.getColumnIndex(TableCategories.COLUMN_NAME));
        budget = c.getFloat(c.getColumnIndex(TableCategories.COLUMN_BUDGET));
        c.close();
    }

    public long getCategoryId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
        ContentValues cv = new ContentValues();
        cv.put(TableCategories.COLUMN_NAME, name);
        updateDb(cv);
    }

    public String getName() {
        return name;
    }

    public void setBudget(float budget) {
        this.budget = budget;
        ContentValues cv = new ContentValues();
        cv.put(TableCategories.COLUMN_BUDGET, budget);
        updateDb(cv);
    }

    public float getBudget() {
        return budget;
    }

    private void updateDb(ContentValues cv) {
        contentResolver.update(
                DatabaseContentProvider.CONTENT_URI_CATEGORIES,
                cv,
                TableCategories.COLUMN_ID + " = " + id,
                null
        );
    }
}