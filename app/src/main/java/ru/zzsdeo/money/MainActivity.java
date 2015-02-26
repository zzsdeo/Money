package ru.zzsdeo.money;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ru.zzsdeo.money.adapters.RecyclerViewAdapter;
import ru.zzsdeo.money.core.Account;
import ru.zzsdeo.money.core.AccountCollection;
import ru.zzsdeo.money.core.Transaction;
import ru.zzsdeo.money.core.TransactionCollection;
import ru.zzsdeo.money.core.interfaces.IAccount;
import ru.zzsdeo.money.core.interfaces.ITransaction;
import ru.zzsdeo.money.core.storage.DataStore;

public class MainActivity extends Activity {

    private AccountCollection accounts;
    private TransactionCollection transactions;
    private DataStore dataStore;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStore = new DataStore(this);

        accounts = getAccountCollection();

        transactions = (TransactionCollection) dataStore.loadData(DataStore.TRANSACTION_COLLECTION_FILE_NAME);
        if (transactions == null) transactions = getTransactionCollection(accounts);

        //transactions.getTransaction(100l).approve();

        ArrayList<ITransaction> transactionList = transactions.getAllTransactions();

        for (int i = 0; i < transactionList.size(); i++) {
            String name = transactionList.get(i).getAccount().getName();
            String amount = String.valueOf(transactionList.get(i).getAmount());
            String comment = transactionList.get(i).getComment();
            String isApproved = String.valueOf(transactionList.get(i).isApproved());
            String date = String.valueOf(transactionList.get(i).getDateInMill());

            Log.d("my", "Карта: " + name + " --- Сумма: " + amount + " --- Комментарий: " + comment + " --- Подтверждено: " + isApproved + " --- Дата: " + date);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        items = new ArrayList<>();

        mAdapter = new RecyclerViewAdapter(items);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        for(ITransaction t : transactionList) {
            String name = t.getAccount().getName();
            String amount = String.valueOf(t.getAmount());
            String comment = t.getComment();
            String isApproved = String.valueOf(t.isApproved());
            String date = String.valueOf(t.getDateInMill());
            mAdapter.add("Карта: " + name + " --- Сумма: " + amount + " --- Комментарий: " + comment + " --- Подтверждено: " + isApproved + " --- Дата: " + date);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        transactions = (TransactionCollection) dataStore.loadData(DataStore.TRANSACTION_COLLECTION_FILE_NAME);
        if (transactions == null) transactions = getTransactionCollection(accounts);
    }

    @Override
    protected void onStop() {
        dataStore.saveData(transactions, DataStore.TRANSACTION_COLLECTION_FILE_NAME);
        super.onStop();
    }

    private AccountCollection getAccountCollection() {
        AccountCollection accounts = new AccountCollection();
        accounts.addAccount(new Account.Builder("Кредитная")
                .hasSmsNotification(true)
                .smsId("Card0115")
                .build());
        accounts.addAccount(new Account.Builder("Зарплатная")
                .hasSmsNotification(true)
                .smsId("Card2485")
                .build());
        accounts.addAccount(new Account.Builder("Наличные")
                .hasSmsNotification(false)
                .build());
        return accounts;
    }

    private TransactionCollection getTransactionCollection(AccountCollection accounts) {
        TransactionCollection transactions = new TransactionCollection();
        /*transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Зарплатная"), 100l)
                .amount(100)
                .comment("Зарплата")
                .build());
        transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Кредитная"), 200l)
                .amount(-100)
                .comment("Лента")
                .build());
        transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Наличные"), 300l)
                .amount(-100)
                .comment("Музей")
                .build());
        transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Зарплатная"), 400l)
                .amount(100)
                .comment("Зарплата")
                .build());
        transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Кредитная"), 500l)
                .amount(-100)
                .comment("Лента")
                .build());
        transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Наличные"), 600l)
                .amount(-100)
                .comment("Музей")
                .build());
        transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Зарплатная"), 700l)
                .amount(100)
                .comment("Зарплата")
                .build());
        transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Кредитная"), 800l)
                .amount(-100)
                .comment("Лента")
                .build());
        transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Наличные"), 900l)
                .amount(-100)
                .comment("Музей")
                .build());*/
        for (long i = 100l; i <= 10000l; i += 100l) {
            transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Зарплатная"), i)
                    .amount(100)
                    .comment("Зарплата")
                    .build());
        }
        return transactions;
    }
}
