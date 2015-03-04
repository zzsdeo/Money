package ru.zzsdeo.money.model;

import java.io.Serializable;

import ru.zzsdeo.money.model.interfaces.IAccount;
import ru.zzsdeo.money.model.interfaces.ITransaction;

public class Transaction {

    private final long id;
    private long accountId;
    private long dateInMill;
    private float amount;
    private String comment;
    private boolean isApproved;
    private boolean isTransfer;
    private IAccount destinationAccount;
    private boolean needApprove;
    private boolean isRepeating;
    private int repeatingType;

    //public static final String[] repeatingTypes = {getString(R.string.once), "Каждый день", "Каждый будний день", "Каждое определенное число", "Каждый определенный день недели", "Каждый последний день месяца"};

    public Transaction(long id) {
        this.id = id;
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

    public IAccount getAccount() {
        return account;
    }

    public long getDateInMill() {
        return dateInMill;
    }

    public float getAmount() {
        return amount;
    }

    public String getComment() {
        return comment;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public boolean isTransfer() {
        return isTransfer;
    }

    public IAccount getDestinationAccount() {
        return destinationAccount;
    }

    public boolean getNeedApprove() {
        return needApprove;
    }

    public void approve() {
        isApproved = true;
    }

    public boolean isRepeating() {
        return isRepeating;
    }

    public int getRepeatingType() {
        return repeatingType;
    }
}