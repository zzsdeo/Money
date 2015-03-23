package ru.zzsdeo.money.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.astuetz.PagerSlidingTabStrip;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.adapters.HistoryRecyclerViewAdapter;
import ru.zzsdeo.money.adapters.MainActivityBalanceRecyclerViewAdapter;
import ru.zzsdeo.money.adapters.MainPagerAdapter;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.TransactionCollection;

public class MainActivity extends ActionBarActivity implements Dialogs.DialogListener {

    private AccountCollection accountCollection;
    public MainActivityBalanceRecyclerViewAdapter mainActivityBalanceRecyclerViewAdapter;
    public HistoryRecyclerViewAdapter historyRecyclerViewAdapter;

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
            case R.id.manage_categories:
                startActivity(new Intent(this, ManageCategoriesActivity.class));
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

        mainActivityBalanceRecyclerViewAdapter = new MainActivityBalanceRecyclerViewAdapter(this);
        historyRecyclerViewAdapter = new HistoryRecyclerViewAdapter(this);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mainPagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        pager.setCurrentItem(1);

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

        switch (requestCode) {
            case Constants.MANAGE_ACCOUNTS_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    accountCollection = new AccountCollection(this);
                    mainActivityBalanceRecyclerViewAdapter.refreshDataSet();
                }
                break;
            case Constants.ADD_ACCOUNT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    accountCollection = new AccountCollection(this);
                    mainActivityBalanceRecyclerViewAdapter.refreshDataSet();
                }
                break;
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType) {
        switch (dialogType) {
            case Dialogs.YOU_MUST_ADD_ACCOUNT:
                startActivityForResult(new Intent(this, AddAccountActivity.class), Constants.ADD_ACCOUNT_REQUEST_CODE);
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType, long id) {
        switch (dialogType) {
            case Dialogs.DELETE_TRANSACTION:
                historyRecyclerViewAdapter.removeItem(id);
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int dialogType) {
        switch (dialogType) {
            case Dialogs.DELETE_TRANSACTION:
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int dialogType, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(TimePicker view, int dialogType, int hourOfDay, int minute) {

    }
}
