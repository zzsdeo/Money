package ru.zzsdeo.money.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.zzsdeo.money.db.DatabaseContentProvider;
import ru.zzsdeo.money.db.TableTransactions;

public class Transaction {

    private final ContentResolver contentResolver;
    private final long id;

    public Transaction(Context context, long id) {
        contentResolver = context.getContentResolver();
        this.id = id;
    }

    public long getTransactionId() {
        return id;
    }

    public void setAccountId(long accountId) {
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_ACCOUNT_ID, accountId);
        updateDb(cv);
    }

    public long getAccountId() {
        Cursor c = getCursor(TableTransactions.COLUMN_ACCOUNT_ID);
        c.moveToFirst();
        long accountId = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_ACCOUNT_ID));
        c.close();
        return accountId;
    }

    public void setDateInMill(long dateInMill) {
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_DATE_IN_MILL, dateInMill);
        updateDb(cv);
    }

    public long getDateInMill() {
        Cursor c = getCursor(TableTransactions.COLUMN_DATE_IN_MILL);
        c.moveToFirst();
        long dateInMill = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_DATE_IN_MILL));
        c.close();
        return dateInMill;
    }

    public void setAmount(float amount) {
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_AMOUNT, amount);
        updateDb(cv);
    }

    public float getAmount() {
        Cursor c = getCursor(TableTransactions.COLUMN_AMOUNT);
        c.moveToFirst();
        float amount = c.getFloat(c.getColumnIndex(TableTransactions.COLUMN_AMOUNT));
        c.close();
        return amount;
    }

    public void setCommission(float commission) {
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_COMMISSION, commission);
        updateDb(cv);
    }

    public float getCommission() {
        Cursor c = getCursor(TableTransactions.COLUMN_COMMISSION);
        c.moveToFirst();
        float commission = c.getFloat(c.getColumnIndex(TableTransactions.COLUMN_COMMISSION));
        c.close();
        return commission;
    }

    public void setComment(String comment) {
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_COMMENT, comment);
        updateDb(cv);
    }

    public String getComment() {
        Cursor c = getCursor(TableTransactions.COLUMN_COMMENT);
        c.moveToFirst();
        String comment = c.getString(c.getColumnIndex(TableTransactions.COLUMN_COMMENT));
        c.close();
        return comment;
    }

    public void setDestinationAccountId(long destinationAccountId) {
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_DESTINATION_ACCOUNT_ID, destinationAccountId);
        updateDb(cv);
    }

    public long getDestinationAccountId() {
        Cursor c = getCursor(TableTransactions.COLUMN_DESTINATION_ACCOUNT_ID);
        c.moveToFirst();
        long destinationAccountId = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_DESTINATION_ACCOUNT_ID));
        c.close();
        return destinationAccountId;
    }

    public void setCategoryId(long categoryId) {
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_CATEGORY_ID, categoryId);
        updateDb(cv);
    }

    public long getCategoryId() {
        Cursor c = getCursor(TableTransactions.COLUMN_CATEGORY_ID);
        c.moveToFirst();
        long categoryId = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_CATEGORY_ID));
        c.close();
        return categoryId;
    }

    public void setLinkedTransactionId(long linkedTransactionId) {
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_LINKED_TRANSACTION_ID, linkedTransactionId);
        updateDb(cv);
    }

    public long getLinkedTransactionId() {
        Cursor c = getCursor(TableTransactions.COLUMN_LINKED_TRANSACTION_ID);
        c.moveToFirst();
        long linkedTransactionId = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_LINKED_TRANSACTION_ID));
        c.close();
        return linkedTransactionId;
    }

    private Cursor getCursor (String column) {
        return contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_TRANSACTIONS,
                new String[]{
                        column
                },
                TableTransactions.COLUMN_ID + "=" + id,
                null,
                null
        );
    }

    private void updateDb(ContentValues cv) {
        contentResolver.update(
                DatabaseContentProvider.CONTENT_URI_TRANSACTIONS,
                cv,
                TableTransactions.COLUMN_ID + "=" + id,
                null
        );
    }
}