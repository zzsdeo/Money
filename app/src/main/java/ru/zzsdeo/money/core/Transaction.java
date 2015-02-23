package ru.zzsdeo.money.core;

import ru.zzsdeo.money.core.interfaces.IAccount;
import ru.zzsdeo.money.core.interfaces.ITransaction;

public class Transaction implements ITransaction {

    private final IAccount account;
    private final long dateInMill;
    private final float amount;
    private final String comment;

    public static class Builder {

        private final IAccount account;
        private final long dateInMill;

        private float amount = 0;
        private String comment = "";

        public Builder(IAccount account, long dateInMill) {
            this.dateInMill = dateInMill;
            this.account = account;
        }

        public Builder amount(float val) {
            amount = val;
            return this;
        }

        public Builder comment(String val) {
            comment = val;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }

    }

    private Transaction(Builder builder) {
        dateInMill = builder.dateInMill;
        account = builder.account;
        amount = builder.amount;
        comment = builder.comment;
    }

    @Override
    public IAccount getAccount() {
        return account;
    }

    @Override
    public long getDateInMill() {
        return dateInMill;
    }

    @Override
    public float getAmount() {
        return amount;
    }

    @Override
    public String getComment() {
        return comment;
    }
}