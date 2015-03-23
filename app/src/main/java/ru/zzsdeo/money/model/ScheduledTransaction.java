package ru.zzsdeo.money.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.zzsdeo.money.db.DatabaseContentProvider;
import ru.zzsdeo.money.db.TableScheduledTransactions;
import ru.zzsdeo.money.db.TableTransactions;

public class ScheduledTransaction {

    private final ContentResolver contentResolver;
    private final long id;

    public ScheduledTransaction(Context context, long id) {
        contentResolver = context.getContentResolver();
        this.id = id;
    }

    public long getScheduledTransactionId() {
        return id;
    }

    public void setAccountId(long accountId) {
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_ACCOUNT_ID, accountId);
        updateDb(cv);
    }

    public long getAccountId() {
        Cursor c = getCursor(TableScheduledTransactions.COLUMN_ACCOUNT_ID);
        c.moveToFirst();
        long accountId = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_ACCOUNT_ID));
        c.close();
        return accountId;
    }

    public void setDateInMill(long dateInMill) {
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_DATE_IN_MILL, dateInMill);
        updateDb(cv);
    }

    public long getDateInMill() {
        Cursor c = getCursor(TableScheduledTransactions.COLUMN_DATE_IN_MILL);
        c.moveToFirst();
        long dateInMill = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_DATE_IN_MILL));
        c.close();
        return dateInMill;
    }

    public void setAmount(float amount) {
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_AMOUNT, amount);
        updateDb(cv);
    }

    public float getAmount() {
        Cursor c = getCursor(TableScheduledTransactions.COLUMN_AMOUNT);
        c.moveToFirst();
        float amount = c.getFloat(c.getColumnIndex(TableScheduledTransactions.COLUMN_AMOUNT));
        c.close();
        return amount;
    }

    public void setComment(String comment) {
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_COMMENT, comment);
        updateDb(cv);
    }

    public String getComment() {
        Cursor c = getCursor(TableScheduledTransactions.COLUMN_COMMENT);
        c.moveToFirst();
        String comment = c.getString(c.getColumnIndex(TableScheduledTransactions.COLUMN_COMMENT));
        c.close();
        return comment;
    }

    public void setDestinationAccountId(long destinationAccountId) {
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_DESTINATION_ACCOUNT_ID, destinationAccountId);
        updateDb(cv);
    }

    public long getDestinationAccountId() {
        Cursor c = getCursor(TableScheduledTransactions.COLUMN_DESTINATION_ACCOUNT_ID);
        c.moveToFirst();
        long destinationAccountId = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_DESTINATION_ACCOUNT_ID));
        c.close();
        return destinationAccountId;
    }

    public boolean isApproved() {
        Cursor c = getCursor(TableScheduledTransactions.COLUMN_IS_APPROVED);
        c.moveToFirst();
        int isApproved = c.getInt(c.getColumnIndex(TableScheduledTransactions.COLUMN_IS_APPROVED));
        c.close();
        return isApproved != 0;
    }

    public void setNeedApprove(boolean needApprove) {
        ContentValues cv = new ContentValues();
        if (needApprove) {
            cv.put(TableScheduledTransactions.COLUMN_NEED_APPROVE, 1);
        } else {
            cv.put(TableScheduledTransactions.COLUMN_NEED_APPROVE, 0);
        }
        updateDb(cv);
    }

    public boolean getNeedApprove() {
        Cursor c = getCursor(TableScheduledTransactions.COLUMN_NEED_APPROVE);
        c.moveToFirst();
        int needApprove = c.getInt(c.getColumnIndex(TableScheduledTransactions.COLUMN_NEED_APPROVE));
        c.close();
        return needApprove != 0;
    }

    public void approve() {
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_IS_APPROVED, 1);
        updateDb(cv);
    }

    public void setRepeatingTypeId(int id) {
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID, id);
        updateDb(cv);
    }

    public int getRepeatingTypeId() {
        Cursor c = getCursor(TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID);
        c.moveToFirst();
        int repeatingTypeId = c.getInt(c.getColumnIndex(TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID));
        c.close();
        return repeatingTypeId;
    }

    public void setCategoryId(long categoryId) {
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_CATEGORY_ID, categoryId);
        updateDb(cv);
    }

    public long getCategoryId() {
        Cursor c = getCursor(TableScheduledTransactions.COLUMN_CATEGORY_ID);
        c.moveToFirst();
        long categoryId = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_CATEGORY_ID));
        c.close();
        return categoryId;
    }

    private Cursor getCursor (String column) {
        return contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS,
                new String[]{
                        column
                },
                TableScheduledTransactions.COLUMN_ID + "=" + id,
                null,
                null
        );
    }

    private void updateDb(ContentValues cv) {
        contentResolver.update(
                DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS,
                cv,
                TableScheduledTransactions.COLUMN_ID + "=" + id,
                null
        );
    }
}