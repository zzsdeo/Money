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

    private String name;
    private String cardNumber;
    private float balance;

    public Account(Context context, long id) {
        contentResolver = context.getContentResolver();
        this.id = id;

        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_ACCOUNTS,
                null,
                TableAccounts.COLUMN_ID + " = " + id,
                null,
                null
        );
        c.moveToFirst();
        name = c.getString(c.getColumnIndex(TableAccounts.COLUMN_NAME));
        cardNumber = c.getString(c.getColumnIndex(TableAccounts.COLUMN_CARD_NUMBER));
        balance = c.getFloat(c.getColumnIndex(TableAccounts.COLUMN_BALANCE));
        c.close();
    }

    public long getAccountId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
        ContentValues cv = new ContentValues();
        cv.put(TableAccounts.COLUMN_NAME, name);
        updateDb(cv);
    }

    public String getName() {
        return name;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        ContentValues cv = new ContentValues();
        cv.put(TableAccounts.COLUMN_CARD_NUMBER, cardNumber);
        updateDb(cv);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setBalance(float balance) {
        this.balance = balance;
        ContentValues cv = new ContentValues();
        cv.put(TableAccounts.COLUMN_BALANCE, balance);
        updateDb(cv);
    }

    public float getBalance() {
        return balance;
    }

    private void updateDb(ContentValues cv) {
        contentResolver.update(
                DatabaseContentProvider.CONTENT_URI_ACCOUNTS,
                cv,
                TableAccounts.COLUMN_ID + " = " + id,
                null
        );
    }
}