package ru.zzsdeo.money.model;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import ru.zzsdeo.money.db.DatabaseContentProvider;
import ru.zzsdeo.money.db.TableAccounts;
import ru.zzsdeo.money.db.TableScheduledTransactions;
import ru.zzsdeo.money.db.TableTransactions;

public class AccountCollection extends LinkedHashMap<Long, Account> {

    private final ContentResolver contentResolver;
    private final Context context;

    public static final String[] SORTED_BY_NAME = {
            null,
            TableAccounts.COLUMN_NAME
    };

    public AccountCollection (Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_ACCOUNTS,
                new String[]{
                        TableAccounts.COLUMN_ID
                },
                null,
                null,
                null
        );
        long id;
        if (c.moveToFirst()) {
            do {
                id = c.getLong(c.getColumnIndex(TableAccounts.COLUMN_ID));
                put(id, new Account(context, id));
            } while (c.moveToNext());
        }
        c.close();
    }

    public AccountCollection (Context context, String[] params) {
        String[] args;
        if (params.length > 2) {
            ArrayList<String> argsList = new ArrayList<>();
            argsList.addAll(Arrays.asList(params).subList(2, params.length));
            args = (String[]) argsList.toArray();
        } else {
            args = null;
        }
        this.context = context;
        contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_ACCOUNTS,
                new String[]{
                        TableAccounts.COLUMN_ID
                },
                params[0],
                args,
                params[1]
        );
        long id;
        if (c.moveToFirst()) {
            do {
                id = c.getLong(c.getColumnIndex(TableAccounts.COLUMN_ID));
                put(id, new Account(context, id));
            } while (c.moveToNext());
        }
        c.close();
    }

    public long addAccount(String name, String cardNumber, float balance) {
        ContentValues cv = new ContentValues();
        cv.put(TableAccounts.COLUMN_NAME, name);
        cv.put(TableAccounts.COLUMN_CARD_NUMBER, cardNumber);
        cv.put(TableAccounts.COLUMN_BALANCE, balance);
        Uri uri = contentResolver.insert(DatabaseContentProvider.CONTENT_URI_ACCOUNTS, cv);
        long id = Long.valueOf(uri.getLastPathSegment());
        put(id, new Account(context, id));
        return id;
    }

    public void removeAccount(long id) {
        int deletedRows = contentResolver.delete(
                DatabaseContentProvider.CONTENT_URI_ACCOUNTS,
                TableAccounts.COLUMN_ID + "=" + id,
                null
        );
        if (deletedRows > 0) remove(id);

        /*// удаление транзакций, ассоциированных с этим аккаунтом
        TransactionCollection transactionsWithRemovedAccount = new TransactionCollection(context,
                new String[]{TableTransactions.COLUMN_ACCOUNT_ID + "=" + id,
                        null});
        Set<Long> ids = transactionsWithRemovedAccount.keySet();
        for (long idToRemove : ids) {
            transactionsWithRemovedAccount.removeTransaction(idToRemove);
        }

        // удаление запланированных транзакций, ассоциированных с этим аккаунтом
        ScheduledTransactionCollection scheduledTransactionsWithRemovedAccount = new ScheduledTransactionCollection(context,
                new String[]{TableScheduledTransactions.COLUMN_ACCOUNT_ID + "=" + id,
                        null});
        ids = scheduledTransactionsWithRemovedAccount.keySet();
        for (long idToRemove : ids) {
            scheduledTransactionsWithRemovedAccount.removeScheduledTransaction(idToRemove);
        }

        // удаление ссылок на аккаунт
        TransactionCollection transactionsWithDestinationAccount = new TransactionCollection(context,
                new String[]{TableTransactions.COLUMN_DESTINATION_ACCOUNT_ID + "=" + id,
                        null});
        for (Transaction transaction : transactionsWithDestinationAccount.values()) {
            transaction.setDestinationAccountId(0);
        }

        // удаление ссылок на аккаунт
        ScheduledTransactionCollection scheduledTransactionsWithDestinationAccount = new ScheduledTransactionCollection(context,
                new String[]{TableScheduledTransactions.COLUMN_DESTINATION_ACCOUNT_ID + "=" + id,
                        null});
        for (ScheduledTransaction transaction : scheduledTransactionsWithDestinationAccount.values()) {
            transaction.setDestinationAccountId(0);
        }*/




        TransactionCollection transactionCollection = new TransactionCollection(context);
        ArrayList<Long> transactionIdsToRemove = new ArrayList<>();
        for (Transaction transaction : transactionCollection.values()) {
            if (transaction.getDestinationAccountId() == id) {
                transaction.setDestinationAccountId(0);
            }
            if (transaction.getAccountId() == id) {
                transactionIdsToRemove.add(transaction.getTransactionId());
            }
        }
        for (long trId : transactionIdsToRemove) {
            transactionCollection.removeTransaction(trId);
        }

        ScheduledTransactionCollection scheduledTransactionCollection = new ScheduledTransactionCollection(context);
        ArrayList<Long> scheduledTransactionIdsToRemove = new ArrayList<>();
        for (ScheduledTransaction scheduledTransaction : scheduledTransactionCollection.values()) {
            if (scheduledTransaction.getDestinationAccountId() == id) {
                scheduledTransaction.setDestinationAccountId(0);
            }
            if (scheduledTransaction.getAccountId() == id) {
                scheduledTransactionIdsToRemove.add(scheduledTransaction.getScheduledTransactionId());
            }
        }
        for (long trId : scheduledTransactionIdsToRemove) {
            scheduledTransactionCollection.removeScheduledTransaction(trId);
        }
    }
}