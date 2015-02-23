package ru.zzsdeo.money.core.interfaces;

import java.util.ArrayList;

public interface ITransactionCollection {

    public void addTransaction(ITransaction transaction);

    public void removeTransaction(long dateInMill);

    public ITransaction getTransaction(long dateInMill);

    public ArrayList<ITransaction> getAllTransactions();

}
