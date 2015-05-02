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

    private long accountId;
    private long dateInMill;
    private float amount;
    private float commission;
    private String comment;
    private long destinationAccountId;
    private boolean needApprove;
    private int repeatingTypeId;
    private long categoryId;
    private long linkedTransactionId;


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
        accountId = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_ACCOUNT_ID));
        dateInMill = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_DATE_IN_MILL));
        amount = c.getFloat(c.getColumnIndex(TableScheduledTransactions.COLUMN_AMOUNT));
        commission = c.getFloat(c.getColumnIndex(TableScheduledTransactions.COLUMN_COMMISSION));
        comment = c.getString(c.getColumnIndex(TableScheduledTransactions.COLUMN_COMMENT));
        destinationAccountId = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_DESTINATION_ACCOUNT_ID));
        needApprove = c.getInt(c.getColumnIndex(TableScheduledTransactions.COLUMN_NEED_APPROVE)) == 1;
        repeatingTypeId = c.getInt(c.getColumnIndex(TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID));
        categoryId = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_CATEGORY_ID));
        linkedTransactionId = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_LINKED_TRANSACTION_ID));
        c.close();
    }

    public long getScheduledTransactionId() {
        return id;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_ACCOUNT_ID, accountId);
        updateDb(cv);
    }

    public long getAccountId() {
        return accountId;
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

    public void setCommission(float commission) {
        this.commission = commission;
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_COMMISSION, commission);
        updateDb(cv);
    }

    public float getCommission() {
        return commission;
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

    public void setDestinationAccountId(long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_DESTINATION_ACCOUNT_ID, destinationAccountId);
        updateDb(cv);
    }

    public long getDestinationAccountId() {
        return destinationAccountId;
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

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_CATEGORY_ID, categoryId);
        updateDb(cv);
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setLinkedTransactionId(long linkedTransactionId) {
        this.linkedTransactionId = linkedTransactionId;
        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_LINKED_TRANSACTION_ID, linkedTransactionId);
        updateDb(cv);
    }

    public long getLinkedTransactionId() {
        return linkedTransactionId;
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