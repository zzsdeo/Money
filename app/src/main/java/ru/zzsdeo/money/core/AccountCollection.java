package ru.zzsdeo.money.core;

import java.util.ArrayList;
import java.util.Iterator;

import ru.zzsdeo.money.core.interfaces.IAccount;
import ru.zzsdeo.money.core.interfaces.IAccountCollection;

public class AccountCollection implements IAccountCollection {

    private ArrayList<IAccount> accounts;

    public AccountCollection () {
        accounts = new ArrayList<>();
    }

    @Override
    public void addAccount(IAccount account) {
        accounts.add(account);
    }

    @Override
    public void removeAccount(String name) {
        for (int i = 1; i <= accounts.size(); i++) {
            if (accounts.get(i).getName().equals(name)) {
                accounts.remove(i);
                return;
            }
        }
    }

    @Override
    public IAccount getAccount(String name) {
        for (int i = 1; i <= accounts.size(); i++) {
            if (accounts.get(i).getName().equals(name)) {
                return accounts.get(i);
            }
        }
        return null;
    }

    @Override
    public ArrayList<IAccount> getAllAccounts() {
        return accounts;
    }
}
