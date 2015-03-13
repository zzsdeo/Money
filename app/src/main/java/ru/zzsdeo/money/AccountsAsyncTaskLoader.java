package ru.zzsdeo.money;

import android.content.AsyncTaskLoader;
import android.content.Context;

import ru.zzsdeo.money.model.AccountCollection;

public class AccountsAsyncTaskLoader extends AsyncTaskLoader<AccountCollection> {

    private Context context;

    public AccountsAsyncTaskLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public AccountCollection loadInBackground() {
        return new AccountCollection(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (takeContentChanged()) forceLoad();
    }
}