package ru.zzsdeo.money.core.interfaces;

public interface ITransaction {

    public IAccount getAccount();

    public long getDateInMill();

    public float getAmount();

    public String getComment();

    public boolean approved();

    public boolean isTransfer();

    

}
