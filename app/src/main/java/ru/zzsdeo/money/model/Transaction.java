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

    private long accountId;
    private long dateInMill;
    private float amount;
    private float commission;
    private String comment;
    private long destinationAccountId;
    private long categoryId;
    private long linkedTransactionId;

    public Transaction(Context context, long id) {
        contentResolver = context.getContentResolver();
        this.id = id;

        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_TRANSACTIONS,
                null,
                TableTransactions.COLUMN_ID + " = " + id,
                null,
                null
        );
        c.moveToFirst();
        accountId = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_ACCOUNT_ID));
        dateInMill = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_DATE_IN_MILL));
        amount = c.getFloat(c.getColumnIndex(TableTransactions.COLUMN_AMOUNT));
        commission = c.getFloat(c.getColumnIndex(TableTransactions.COLUMN_COMMISSION));
        comment = c.getString(c.getColumnIndex(TableTransactions.COLUMN_COMMENT));
        destinationAccountId = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_DESTINATION_ACCOUNT_ID));
        categoryId = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_CATEGORY_ID));
        linkedTransactionId = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_LINKED_TRANSACTION_ID));
        c.close();
    }

    public long getTransactionId() {
        return id;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_ACCOUNT_ID, accountId);
        updateDb(cv);
    }

    public long getAccountId() {
        return accountId;
    }

    public void setDateInMill(long dateInMill) {
        this.dateInMill = dateInMill;
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_DATE_IN_MILL, dateInMill);
        updateDb(cv);
    }

    public long getDateInMill() {
        return dateInMill;
    }

    public void setAmount(float amount) {
        this.amount = amount;
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_AMOUNT, amount);
        updateDb(cv);
    }

    public float getAmount() {
        return amount;
    }

    public void setCommission(float commission) {
        this.commission = commission;
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_COMMISSION, commission);
        updateDb(cv);
    }

    public float getCommission() {
        return commission;
    }

    public void setComment(String comment) {
        this.comment = comment;
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_COMMENT, comment);
        updateDb(cv);
    }

    public String getComment() {
        return comment;
    }

    public void setDestinationAccountId(long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_DESTINATION_ACCOUNT_ID, destinationAccountId);
        updateDb(cv);
    }

    public long getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_CATEGORY_ID, categoryId);
        updateDb(cv);
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setLinkedTransactionId(long linkedTransactionId) {
        this.linkedTransactionId = linkedTransactionId;
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_LINKED_TRANSACTION_ID, linkedTransactionId);
        updateDb(cv);
    }

    public long getLinkedTransactionId() {
        return linkedTransactionId;
    }

    private void updateDb(ContentValues cv) {
        contentResolver.update(
                DatabaseContentProvider.CONTENT_URI_TRANSACTIONS,
                cv,
                TableTransactions.COLUMN_ID + " = " + id,
                null
        );
    }
}