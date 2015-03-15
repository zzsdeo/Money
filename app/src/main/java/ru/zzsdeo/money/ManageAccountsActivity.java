package ru.zzsdeo.money;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ru.zzsdeo.money.adapters.ManageAccountsRecyclerViewAdapter;
import ru.zzsdeo.money.model.AccountCollection;

public class ManageAccountsActivity extends ActionBarActivity implements Dialogs.DialogListener {

    public static final int ADD_ACCOUNT_REQUEST_CODE = 10;

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
                startActivityForResult(new Intent(this, AddActivity.class), ADD_ACCOUNT_REQUEST_CODE);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_accounts);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_accounts);
        manageAccountsRecyclerViewAdapter = new ManageAccountsRecyclerViewAdapter(this);
        recyclerView.setAdapter(manageAccountsRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) manageAccountsRecyclerViewAdapter.refreshDataSet();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, long id) {
        manageAccountsRecyclerViewAdapter.removeItem(id);
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}