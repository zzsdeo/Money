package ru.zzsdeo.money.core;

import java.io.Serializable;
import java.util.ArrayList;

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.core.interfaces.IAccount;
import ru.zzsdeo.money.core.interfaces.ITransaction;

public class Transaction implements ITransaction, Serializable {

    private final IAccount account;
    private final long dateInMill;
    private final float amount;
    private final String comment;
    private boolean isApproved;
    private final boolean isTransfer;
    private final IAccount destinationAccount;
    private final boolean needApprove;
    private final boolean isRepeating;
    private final int repeatingType;

    //public static final String[] repeatingTypes = {getString(R.string.once), "Каждый день", "Каждый будний день", "Каждое определенное число", "Каждый определенный день недели", "Каждый последний день месяца"};

    public static class Builder {

        private final IAccount account;
        private final long dateInMill;

        private float amount = 0;
        private String comment = "";
        private boolean isApproved = false;
        private boolean isTransfer = false;
        private IAccount destinationAccount = null;
        private boolean needApprove = false;
        private boolean isRepeating = false;
        private int repeatingType = 0;

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

        public Builder isApproved(boolean val) {
            isApproved = val;
            return this;
        }

        public Builder destinationAccount(IAccount val) {
            destinationAccount = val;
            if (val != null) isTransfer = true;
            return this;
        }

        public Builder needApprove(boolean val) {
            needApprove = val;
            return this;
        }

        public Builder repeatingType(int val) {
            repeatingType = val;
            if (val != 0) isRepeating = true;
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
        isApproved = builder.isApproved;
        isTransfer = builder.isTransfer;
        destinationAccount = builder.destinationAccount;
        needApprove = builder.needApprove;
        isRepeating = builder.isRepeating;
        repeatingType = builder.repeatingType;
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

    @Override
    public boolean isApproved() {
        return isApproved;
    }

    @Override
    public boolean isTransfer() {
        return isTransfer;
    }

    @Override
    public IAccount getDestinationAccount() {
        return destinationAccount;
    }

    @Override
    public boolean getNeedApprove() {
        return needApprove;
    }

    @Override
    public void approve() {
        isApproved = true;
    }

    @Override
    public boolean isRepeating() {
        return isRepeating;
    }

    @Override
    public int getRepeatingType() {
        return repeatingType;
    }
}