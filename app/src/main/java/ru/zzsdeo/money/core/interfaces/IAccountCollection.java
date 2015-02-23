package ru.zzsdeo.money.core.interfaces;

import java.util.ArrayList;

public interface IAccountCollection {

    public void addAccount(IAccount account);

    public void removeAccount(String name);

    public IAccount getAccount(String name);

    public ArrayList<IAccount> getAllAccounts();

}
