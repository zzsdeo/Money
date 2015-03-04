package ru.zzsdeo.money.model;


import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;


/*public class AccountCollection extends HashMap<Long, String> {

    private long id;
    private String name;

    public AccountCollection () {
        put(id, name);
    }

    public void addAccount(String name, int cardNumber, float balance) {
    }

    public void removeAccount(String name) {
    }

    public void removeAccount(long id) {
    }*/

public class AccountCollection extends HashMap<Long, Account> {

    public AccountCollection (Context context) {
        Cursor c = context.getContentResolver().query()
        put(id, account);
    }

    public void addAccount(String name, int cardNumber, float balance) {
    }

    public void removeAccount(String name) {
    }

    public void removeAccount(long id) {
    }
}