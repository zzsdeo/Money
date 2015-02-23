package ru.zzsdeo.money;

import android.app.Activity;
import android.os.Bundle;

import ru.zzsdeo.money.core.Account;
import ru.zzsdeo.money.core.AccountCollection;
import ru.zzsdeo.money.core.Transaction;
import ru.zzsdeo.money.core.TransactionCollection;

public class MainActivity extends Activity {

    private AccountCollection accounts;
    private TransactionCollection transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accounts = getAccountCollection();

        transactions = getTransactionCollection(accounts);

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
        transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Кредитная"), 100l)
                .amount(-100)
                .comment("Лента")
                .build());
        transactions.addTransaction(new Transaction.Builder(accounts.getAccount("Наличные"), 100l)
                .amount(-100)
                .comment("Музей")
                .build());
        return transactions;
    }
}
