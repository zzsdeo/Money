package ru.zzsdeo.money.core;

import java.io.Serializable;

import ru.zzsdeo.money.core.interfaces.IAccount;

public class Account implements IAccount, Serializable {

    private final String name;
    private final boolean hasSmsNotification;
    private final String smsId;
    private float balance;

    public static class Builder {
        private final String name;

        private boolean hasSmsNotification = false;
        private String smsId = "";
        private float balance = 0;

        public Builder(String name) {
            this.name = name;
        }

        public Builder hasSmsNotification(boolean val) {
            hasSmsNotification = val;
            return this;
        }

        public Builder smsId(String val) {
            smsId = val;
            return this;
        }

        public Builder balance(float val) {
            balance = val;
            return this;
        }

        public Account build() {
            return new Account(this);
        }

    }

    private Account(Builder builder) {
        name = builder.name;
        hasSmsNotification = builder.hasSmsNotification;
        smsId = builder.smsId;
        balance = builder.balance;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasSmsNotification() {
        return hasSmsNotification;
    }

    @Override
    public String getSmsId() {
        return smsId;
    }

    @Override
    public void setBalance(float balance) {
        this.balance = balance;
    }

    @Override
    public float getBalance() {
        return balance;
    }

    @Override
    public void makeTransaction(float amount) {
        balance += amount;
    }

    @Override
    public void makeTransfer(float amount, IAccount account) {
        balance += amount;
        account.makeTransaction(amount);
    }
}
