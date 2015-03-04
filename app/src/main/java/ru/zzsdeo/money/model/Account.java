package ru.zzsdeo.money.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.zzsdeo.money.db.DatabaseContentProvider;
import ru.zzsdeo.money.db.TableAccounts;

public class Account {

    private final ContentResolver contentResolver;
    private final long id;

    public Account(Context context, long id) {
        contentResolver = context.getContentResolver();
        this.id = id;
    }

    public long getAccountId() {
        return id;
    }

    public void setName(String name) {
        ContentValues cv = new ContentValues();
        cv.put(TableAccounts.COLUMN_NAME, name);
        updateDb(cv);
    }

    public String getName() {
        Cursor c = getCursor(TableAccounts.COLUMN_NAME);
        String name = c.getString(c.getColumnIndex(TableAccounts.COLUMN_NAME));
        c.close();
        return name;
    }

    public void setCardNumber(int cardNumber) {
        ContentValues cv = new ContentValues();
        cv.put(TableAccounts.COLUMN_CARD_NUMBER, cardNumber);
        updateDb(cv);
    }

    public int getCardNumber() {
        Cursor c = getCursor(TableAccounts.COLUMN_CARD_NUMBER);
        int cardNumber = c.getInt(c.getColumnIndex(TableAccounts.COLUMN_CARD_NUMBER));
        c.close();
        return cardNumber;
    }

    public void setBalance(float balance) {
        ContentValues cv = new ContentValues();
        cv.put(TableAccounts.COLUMN_BALANCE, balance);
        updateDb(cv);
    }

    public float getBalance() {
        Cursor c = getCursor(TableAccounts.COLUMN_BALANCE);
        float balance = c.getFloat(c.getColumnIndex(TableAccounts.COLUMN_BALANCE));
        c.close();
        return balance;
    }

    private Cursor getCursor (String column) {
        return contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_ACCOUNTS,
                new String[]{
                        column
                },
                TableAccounts.COLUMN_ID + "=" + id,
                null,
                null
        );
    }

    private void updateDb(ContentValues cv) {
        contentResolver.update(
                DatabaseContentProvider.CONTENT_URI_ACCOUNTS,
                cv,
                TableAccounts.COLUMN_ID + "=" + id,
                null
        );
    }
}