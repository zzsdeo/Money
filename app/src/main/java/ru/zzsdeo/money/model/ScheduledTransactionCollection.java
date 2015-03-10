package ru.zzsdeo.money.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.HashMap;

import ru.zzsdeo.money.db.DatabaseContentProvider;
import ru.zzsdeo.money.db.TableScheduledTransactions;
import ru.zzsdeo.money.db.TableTransactions;

public class ScheduledTransactionCollection extends HashMap<Long, ScheduledTransaction> {

    private final ContentResolver contentResolver;
    private final Context context;

    public ScheduledTransactionCollection(Context context) {
        this.context = context;
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
                put(id, new ScheduledTransaction(context, id));
            } while (c.moveToNext());
        }
        c.close();
    }

    public void addScheduledTransaction(
            long accountId,
            long dateInMill,
            float amount,
            float commission,
            String comment,
            long destinationAccountId,
            boolean needApprove,
            int repeatingTypeId,
            long categoryId) {

        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_ACCOUNT_ID, accountId);
        cv.put(TableScheduledTransactions.COLUMN_DATE_IN_MILL, dateInMill);
        cv.put(TableScheduledTransactions.COLUMN_AMOUNT, amount);
        cv.put(TableScheduledTransactions.COLUMN_COMMISSION, commission);
        cv.put(TableScheduledTransactions.COLUMN_COMMENT, comment);
        cv.put(TableScheduledTransactions.COLUMN_IS_APPROVED, 0);
        cv.put(TableScheduledTransactions.COLUMN_DESTINATION_ACCOUNT_ID, destinationAccountId);
        if (needApprove) {
            cv.put(TableScheduledTransactions.COLUMN_NEED_APPROVE, 1);
        } else {
            cv.put(TableScheduledTransactions.COLUMN_NEED_APPROVE, 0);
        }
        cv.put(TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID, repeatingTypeId);
        cv.put(TableScheduledTransactions.COLUMN_CATEGORY_ID, categoryId);
        Uri uri = contentResolver.insert(DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS, cv);
        long id = Long.valueOf(uri.getLastPathSegment());
        put(id, new ScheduledTransaction(context, id));
    }

    public void removeScheduledTransaction(long id) {
        int deletedRows = contentResolver.delete(
                DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS,
                TableScheduledTransactions.COLUMN_ID + "=" + id,
                null
        );
        if (deletedRows > 0) remove(id);
    }
}
