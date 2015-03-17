package ru.zzsdeo.money;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import ru.zzsdeo.money.adapters.MainActivityBalanceRecyclerViewAdapter;
import ru.zzsdeo.money.adapters.ManageAccountsRecyclerViewAdapter;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.RepeatingTypes;

public class MainActivity extends ActionBarActivity implements Dialogs.DialogListener {

    private AccountCollection accountCollection;
    private MainActivityBalanceRecyclerViewAdapter mainActivityBalanceRecyclerViewAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manage_accounts:
                startActivityForResult(new Intent(this, ManageAccountsActivity.class), Constants.MANAGE_ACCOUNTS_REQUEST_CODE);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accountCollection = new AccountCollection(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_accounts_main);
        mainActivityBalanceRecyclerViewAdapter = new MainActivityBalanceRecyclerViewAdapter(this);
        recyclerView.setAdapter(mainActivityBalanceRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (accountCollection.isEmpty()) {
            Dialogs dialog = new Dialogs();
            Bundle bundle = new Bundle();
            bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.YOU_MUST_ADD_ACCOUNT);
            dialog.setArguments(bundle);
            dialog.setCancelable(false);
            dialog.show(getFragmentManager(), Dialogs.DIALOGS_TAG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            accountCollection = new AccountCollection(this);
            mainActivityBalanceRecyclerViewAdapter.refreshDataSet();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType) {
        startActivityForResult(new Intent(this, AddAccountActivity.class), Constants.ADD_ACCOUNT_REQUEST_CODE);
        dialog.dismiss();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType, long id) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int dialogType) {

    }
}
