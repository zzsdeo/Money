package ru.zzsdeo.money;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import ru.zzsdeo.money.core.Account;
import ru.zzsdeo.money.core.AccountCollection;
import ru.zzsdeo.money.core.Transaction;
import ru.zzsdeo.money.core.TransactionCollection;
import ru.zzsdeo.money.core.interfaces.IAccount;
import ru.zzsdeo.money.core.interfaces.ITransaction;

public class MainActivity extends Activity {

    private AccountCollection accounts;
    private TransactionCollection transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accounts = getAccountCollection();

        transactions = getTransactionCollection(accounts);

        transactions.getTransaction(100l).approve();

        ArrayList<ITransaction> transactionList = transactions.getAllTransactions();

        for (int i = 0; i < transactionList.size(); i++) {
            String name = transactionList.get(i).getAccount().getName();
            String amount = String.valueOf(transactionList.get(i).getAmount());
            String comment = transactionList.get(i).getComment();
            String isApproved = String.valueOf(transactionList.get(i).isApproved());
            String date = String.valueOf(transactionList.get(i).getDateInMill());

            Log.d("my", "Карта: " + name + " --- Сумма: " + amount + " --- Комментарий: " + comment + " --- Подтверждено: " + isApproved + " --- Дата: " + date);
        }
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
        transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Зарплатная"), 100l)
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
                .build());
        return transactions;
    }
}
