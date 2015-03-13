package ru.zzsdeo.money;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import ru.zzsdeo.money.adapters.ManageAccountsRecyclerViewAdapter;
import ru.zzsdeo.money.model.AccountCollection;

public class ManageAccountsActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<AccountCollection> {

    private ManageAccountsRecyclerViewAdapter manageAccountsRecyclerViewAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_accounts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_account:
                startActivity(new Intent(this, AddActivity.class));
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_accounts);

        AccountCollection accountCollection = new AccountCollection(this);
        getLoaderManager().initLoader(0, null, this);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_accounts);
        manageAccountsRecyclerViewAdapter = new ManageAccountsRecyclerViewAdapter(this);
        recyclerView.setAdapter(manageAccountsRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public Loader<AccountCollection> onCreateLoader(int id, Bundle args) {
        return new AccountsAsyncTaskLoader(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<AccountCollection> loader, AccountCollection data) {
        manageAccountsRecyclerViewAdapter.setData(data);
        //getLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public void onLoaderReset(Loader<AccountCollection> loader) {
        manageAccountsRecyclerViewAdapter.clearData();
    }
}
