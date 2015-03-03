package ru.zzsdeo.money;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import ru.zzsdeo.money.core.CoreActivity;
import ru.zzsdeo.money.core.Transaction;
import ru.zzsdeo.money.core.TransactionCollection;
import ru.zzsdeo.money.core.interfaces.IAccount;
import ru.zzsdeo.money.core.interfaces.ITransaction;
import ru.zzsdeo.money.core.storage.DataStore;

public class MainActivity extends CoreActivity {

    private AccountCollection accounts;
    private TransactionCollection transactions;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<String> items;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);*/

        /* TODO toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                return true;
            }
        });*/

        accounts = getAccountCollection();

        transactions = super.getTransactions();
        if (transactions == null) transactions = getTransactionCollection(accounts);

        //transactions.getTransaction(100l).approve();

        ArrayList<ITransaction> transactionList = transactions.getAllTransactions();

        /*for (int i = 0; i < transactionList.size(); i++) {
            String name = transactionList.get(i).getAccount().getName();
            String amount = String.valueOf(transactionList.get(i).getAmount());
            String comment = transactionList.get(i).getComment();
            String isApproved = String.valueOf(transactionList.get(i).isApproved());
            String date = String.valueOf(transactionList.get(i).getDateInMill());

            Log.d("my", "Карта: " + name + " --- Сумма: " + amount + " --- Комментарий: " + comment + " --- Подтверждено: " + isApproved + " --- Дата: " + date);
        }*/


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(transactions);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /*for(ITransaction t : transactionList) {
            String name = t.getAccount().getName();
            String amount = String.valueOf(t.getAmount());
            String comment = t.getComment();
            String isApproved = String.valueOf(t.isApproved());
            String date = String.valueOf(t.getDateInMill());
            adapter.add("Карта: " + name + " --- Сумма: " + amount + " --- Комментарий: " + comment + " --- Подтверждено: " + isApproved + " --- Дата: " + date);
        }*/

    }

    private AccountCollection getAccountCollection() {
        AccountCollection accounts = new AccountCollection();
        accounts.addAccount(new Account.Builder("Кредитная")
                .smsId("Card0115")
                .build());
        accounts.addAccount(new Account.Builder("Зарплатная")
                .smsId("Card2485")
                .build());
        accounts.addAccount(new Account.Builder("Наличные")
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
