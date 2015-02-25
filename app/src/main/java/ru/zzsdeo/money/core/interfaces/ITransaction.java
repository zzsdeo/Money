package ru.zzsdeo.money.core.interfaces;

public interface ITransaction {

    public IAccount getAccount();

    public long getDateInMill();

    public float getAmount();

    public String getComment();

    public boolean isApproved();

    public boolean isTransfer();

    public IAccount getDestinationAccount();

    public boolean getNeedApprove();

    public void approve();

    public boolean isRepeating();

    public int getRepeatingType();

}
