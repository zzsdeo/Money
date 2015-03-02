package ru.zzsdeo.money.core;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import ru.zzsdeo.money.core.storage.DataStore;

public class CoreActivity extends ActionBarActivity {

    private TransactionCollection transactions;
    private DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataStore = new DataStore(this);
        transactions = (TransactionCollection) dataStore.loadData(DataStore.TRANSACTION_COLLECTION_FILE_NAME);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        transactions = (TransactionCollection) dataStore.loadData(DataStore.TRANSACTION_COLLECTION_FILE_NAME);
    }

    @Override
    protected void onStop() {
        if (transactions != null) {
            dataStore.saveData(transactions, DataStore.TRANSACTION_COLLECTION_FILE_NAME);
        }
        super.onStop();
    }

    public TransactionCollection getTransactions() {
        return transactions;
    }
}
