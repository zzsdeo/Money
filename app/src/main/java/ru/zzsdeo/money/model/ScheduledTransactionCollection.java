package ru.zzsdeo.money.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Arrays;

import ru.zzsdeo.money.db.DatabaseContentProvider;
import ru.zzsdeo.money.db.TableScheduledTransactions;

public class ScheduledTransactionCollection extends ArrayList<ScheduledTransaction> {

    private final ContentResolver contentResolver;

    public static final String[] SORTED_BY_DATE_DESC = {
            null,
            TableScheduledTransactions.COLUMN_DATE_IN_MILL + " DESC, " + TableScheduledTransactions.COLUMN_ID + " DESC"
    };

    public ScheduledTransactionCollection(Context context) {
        contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS,
                new String[]{
                        TableScheduledTransactions.COLUMN_ID
                },
                null,
                null,
                null
        );
        long id;
        if (c.moveToFirst()) {
            do {
                id = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_ID));
                add(new ScheduledTransaction(context, id));
            } while (c.moveToNext());
        }
        c.close();
    }

    public ScheduledTransactionCollection(Context context, String[] params) {
        String[] args;
        if (params.length > 2) {
            ArrayList<String> argsList = new ArrayList<>();
            argsList.addAll(Arrays.asList(params).subList(2, params.length));
            args = (String[]) argsList.toArray();
        } else {
            args = null;
        }
        contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS,
                new String[]{
                        TableScheduledTransactions.COLUMN_ID
                },
                params[0],
                args,
                params[1]
        );
        long id;
        if (c.moveToFirst()) {
            do {
                id = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_ID));
                add(new ScheduledTransaction(context, id));
            } while (c.moveToNext());
        }
        c.close();
    }

    public void addScheduledTransaction(
            long dateInMill,
            float amount,
            String comment,
            boolean needApprove,
            int repeatingTypeId) {

        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_DATE_IN_MILL, dateInMill);
        cv.put(TableScheduledTransactions.COLUMN_AMOUNT, amount);
        cv.put(TableScheduledTransactions.COLUMN_COMMENT, comment);
        if (needApprove) {
            cv.put(TableScheduledTransactions.COLUMN_NEED_APPROVE, 1);
        } else {
            cv.put(TableScheduledTransactions.COLUMN_NEED_APPROVE, 0);
        }
        cv.put(TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID, repeatingTypeId);
        contentResolver.insert(DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS, cv);
    }

    public void removeScheduledTransaction(long id) {
        int deletedRows = contentResolver.delete(
                DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS,
                TableScheduledTransactions.COLUMN_ID + " = " + id,
                null
        );
        if (deletedRows > 0) remove(id);
    }
}
