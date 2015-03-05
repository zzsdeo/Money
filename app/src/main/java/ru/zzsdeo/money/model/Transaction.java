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

    //public static final String[] repeatingTypes = {getString(R.string.once), "Каждый день", "Каждый будний день", "Каждое определенное число", "Каждый определенный день недели", "Каждый последний день месяца"};

    public Transaction(Context context, long id) {
        contentResolver = context.getContentResolver();
        this.id = id;
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

    public boolean isApproved() {
        Cursor c = getCursor(TableTransactions.COLUMN_IS_APPROVED);
        c.moveToFirst();
        int isApproved = c.getInt(c.getColumnIndex(TableTransactions.COLUMN_IS_APPROVED));
        c.close();
        return isApproved != 0;
    }

    /*public boolean isTransfer() {
        return isTransfer;
    }

    public IAccount getDestinationAccount() {
        return destinationAccount;
    }*/

    public void setNeedApprove(boolean needApprove) {
        ContentValues cv = new ContentValues();
        if (needApprove) {
            cv.put(TableTransactions.COLUMN_NEED_APPROVE, 1);
        } else {
            cv.put(TableTransactions.COLUMN_NEED_APPROVE, 0);
        }
        updateDb(cv);
    }

    public boolean getNeedApprove() {
        Cursor c = getCursor(TableTransactions.COLUMN_NEED_APPROVE);
        c.moveToFirst();
        int needApprove = c.getInt(c.getColumnIndex(TableTransactions.COLUMN_NEED_APPROVE));
        c.close();
        return needApprove != 0;
    }

    public void approve() {
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_IS_APPROVED, 1);
        updateDb(cv);
    }

    /*public boolean isRepeating() {
        return isRepeating;
    }*/

    public void setRepeatingTypeId(long id) {
        ContentValues cv = new ContentValues();
        cv.put(TableTransactions.COLUMN_REPEATING_TYPE_ID, id);
        updateDb(cv);
    }

    public long getRepeatingTypeId() {
        Cursor c = getCursor(TableTransactions.COLUMN_REPEATING_TYPE_ID);
        c.moveToFirst();
        long repeatingTypeId = c.getLong(c.getColumnIndex(TableTransactions.COLUMN_REPEATING_TYPE_ID));
        c.close();
        return repeatingTypeId;
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