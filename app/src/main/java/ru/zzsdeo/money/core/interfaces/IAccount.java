package ru.zzsdeo.money.core.interfaces;

public interface IAccount {

    public String getName();

    public boolean hasSmsNotification();

    public String getSmsId();

    public void setBalance(float balance);

    public float getBalance();

    public void makeTransaction(float amount);

    public void makeTransfer(float amount, IAccount account);

}