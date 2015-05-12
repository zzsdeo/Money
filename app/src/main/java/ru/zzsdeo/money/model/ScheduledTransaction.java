package ru.zzsdeo.money.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.zzsdeo.money.db.DatabaseContentProvider;
import ru.zzsdeo.money.db.TableScheduledTransactions;

public class ScheduledTransaction {

    private final ContentResolver contentResolver;
    private final long id;

    private long dateInMill;
    private float amount;
    private String comment;
    private boolean needApprove;
    private int repeatingTypeId;


    public ScheduledTransaction(Context context, long id) {
        contentResolver = context.getContentResolver();
        this.id = id;

        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS,
                null,
                TableScheduledTransactions.COLUMN_ID + " = " + id,
                null,
                null
        );
        c.moveToFirst();
        dateInMill = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_DATE_IN_MILL));
        amount = c.getFloat(c.getColumnIndex(TableScheduledTransactions.COLUMN_AMOUNT));
        comment = c.getString(c.getColumnIndex(TableScheduledTransactions.COLUMN_COMMENT));
        needApprove = c.getInt(c.getColumnIndex(TableScheduledTransactions.COLUMN_NEED_APPROVE)) == 1;
        repeatingTypeId = c.getInt(c.getColumnIndex(TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID));
        c.close();
    }

    public long getScheduledTransactionId() {
        return id;
    }

    public void setDateInMill(long dateInMill) {
        this.dateInMill = dateInMill;
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_DATE_IN_MILL, dateInMill);
        updateDb(cv);
    }

    public long getDateInMill() {
        return dateInMill;
    }

    public void setAmount(float amount) {
        this.amount = amount;
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_AMOUNT, amount);
        updateDb(cv);
    }

    public float getAmount() {
        return amount;
    }

    public void setComment(String comment) {
        this.comment = comment;
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_COMMENT, comment);
        updateDb(cv);
    }

    public String getComment() {
        return comment;
    }

    public void setNeedApprove(boolean needApprove) {
        this.needApprove = needApprove;
        ContentValues cv = new ContentValues();
        if (needApprove) {
            cv.put(TableScheduledTransactions.COLUMN_NEED_APPROVE, 1);
        } else {
            cv.put(TableScheduledTransactions.COLUMN_NEED_APPROVE, 0);
        }
        updateDb(cv);
    }

    public boolean getNeedApprove() {
        return needApprove;
    }

    public void setRepeatingTypeId(int repeatingTypeId) {
        this.repeatingTypeId = repeatingTypeId;
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID, repeatingTypeId);
        updateDb(cv);
    }

    public int getRepeatingTypeId() {
        return repeatingTypeId;
    }

    private void updateDb(ContentValues cv) {
        contentResolver.update(
                DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS,
                cv,
                TableScheduledTransactions.COLUMN_ID + " = " + id,
                null
        );
    }
}