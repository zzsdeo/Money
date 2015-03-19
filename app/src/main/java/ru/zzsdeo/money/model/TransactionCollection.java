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
import ru.zzsdeo.money.db.TableTransactions;

public class TransactionCollection extends LinkedHashMap<Long, Transaction> {

    private final ContentResolver contentResolver;
    private final Context context;

    public TransactionCollection(Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_TRANSACTIONS,
                new String[]{
                        TableTransactions.COLUMN_ID
                },
                null,
                null,
                null
        );
        long id;
        if (c.moveToFirst()) {
            do {
                id = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_ID));
                put(id, new Transaction(context, id));
            } while (c.moveToNext());
        }
        c.close();
    }

    public TransactionCollection(Context context, String[] params) {
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
                DatabaseContentProvider.CONTENT_URI_TRANSACTIONS,
                new String[]{
                        TableTransactions.COLUMN_ID
                },
                params[0],
                args,
                params[1]
        );
        long id;
        if (c.moveToFirst()) {
            do {
                id = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_ID));
                put(id, new Transaction(context, id));
            } while (c.moveToNext());
        }
        c.close();
    }

    public void addTransaction(
            long accountId,
            long dateInMill,
            float amount,
            float commission,
            String comment,
            long destinationAccountId,
            long categoryId) {

        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_ACCOUNT_ID, accountId);
        cv.put(TableTransactions.COLUMN_DATE_IN_MILL, dateInMill);
        cv.put(TableTransactions.COLUMN_AMOUNT, amount);
        cv.put(TableTransactions.COLUMN_COMMISSION, commission);
        cv.put(TableTransactions.COLUMN_COMMENT, comment);
        cv.put(TableTransactions.COLUMN_DESTINATION_ACCOUNT_ID, destinationAccountId);
        cv.put(TableTransactions.COLUMN_CATEGORY_ID, categoryId);
        Uri uri = contentResolver.insert(DatabaseContentProvider.CONTENT_URI_TRANSACTIONS, cv);
        long id = Long.valueOf(uri.getLastPathSegment());
        put(id, new Transaction(context, id));
    }

    public void removeTransaction(long id) {
        int deletedRows = contentResolver.delete(
                DatabaseContentProvider.CONTENT_URI_TRANSACTIONS,
                TableTransactions.COLUMN_ID + "=" + id,
                null
        );
        if (deletedRows > 0) remove(id);
    }
}