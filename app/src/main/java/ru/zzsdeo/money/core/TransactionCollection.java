package ru.zzsdeo.money.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import ru.zzsdeo.money.core.interfaces.ITransaction;
import ru.zzsdeo.money.core.interfaces.ITransactionCollection;

public class TransactionCollection implements ITransactionCollection, Serializable {

    private ArrayList<ITransaction> transactions;

    public TransactionCollection() {
        transactions = new ArrayList<>();
    }

    @Override
    public void addTransaction(ITransaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public void removeTransaction(long dateInMill) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getDateInMill() == dateInMill) {
                transactions.remove(i);
                return;
            }
        }
    }

    /*@Override
    public void updateTransaction(long dateInMill, ITransaction updatedTransaction) {
        removeTransaction(dateInMill);
        addTransaction(updatedTransaction);
    }*/

    @Override
    public ITransaction getTransaction(long dateInMill) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getDateInMill() == dateInMill) {
                return transactions.get(i);
            }
        }
        return null;
    }

    @Override
    public ArrayList<ITransaction> getAllTransactions() {
        // TODO Collections.sort();
        return transactions;
    }
}
