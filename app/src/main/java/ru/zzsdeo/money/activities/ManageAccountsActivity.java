package ru.zzsdeo.money.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.Dialogs;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.adapters.ManageAccountsRecyclerViewAdapter;

public class ManageAccountsActivity extends ActionBarActivity implements Dialogs.DialogListener {

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
                startActivityForResult(new Intent(this, AddAccountActivity.class), Constants.ADD_ACCOUNT_REQUEST_CODE);
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

        if (resultCode == RESULT_OK) {
            manageAccountsRecyclerViewAdapter.refreshDataSet();
            setResult(RESULT_OK);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType, long id) {
        manageAccountsRecyclerViewAdapter.removeItem(id);
        setResult(RESULT_OK);
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int dialogType) {
        dialog.dismiss();
    }
}