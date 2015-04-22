package ru.zzsdeo.money.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.astuetz.PagerSlidingTabStrip;

import java.util.Calendar;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.adapters.ExpensesRecyclerViewAdapter;
import ru.zzsdeo.money.adapters.NeedCategoryRecyclerViewAdapter;
import ru.zzsdeo.money.adapters.NeedConfirmRecyclerViewAdapter;
import ru.zzsdeo.money.adapters.SchedulerRecyclerViewAdapter;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.adapters.HistoryRecyclerViewAdapter;
import ru.zzsdeo.money.adapters.MainActivityBalanceRecyclerViewAdapter;
import ru.zzsdeo.money.adapters.MainPagerAdapter;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.TransactionCollection;
import ru.zzsdeo.money.services.BootStartUpReceiver;
import ru.zzsdeo.money.services.ServiceReceiver;
import ru.zzsdeo.money.services.UpdateTransactionsIntentService;

public class MainActivity extends ActionBarActivity implements Dialogs.DialogListener {

    private AccountCollection accountCollection;
    public MainActivityBalanceRecyclerViewAdapter mainActivityBalanceRecyclerViewAdapter;
    public HistoryRecyclerViewAdapter historyRecyclerViewAdapter;
    public SchedulerRecyclerViewAdapter schedulerRecyclerViewAdapter;
    public NeedConfirmRecyclerViewAdapter needConfirmRecyclerViewAdapter;
    public NeedCategoryRecyclerViewAdapter needCategoryRecyclerViewAdapter;
    public ExpensesRecyclerViewAdapter expensesRecyclerViewAdapter;
    private ServiceReceiver serviceReceiver;

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
                startActivityForResult(new Intent(this, ManageCategoriesActivity.class), Constants.MANAGE_CATEGORIES_REQUEST_CODE);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // слушаем интент сервисы

        serviceReceiver = new ServiceReceiver();
        registerReceiver(serviceReceiver, new IntentFilter(ServiceReceiver.BROADCAST_ACTION));

        // проверяем и запускаем периодическое обновление запланированных транзакций
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String versionName = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!sharedPreferences.getString(Constants.VERSION_NAME, "").equals(versionName)) {
            Intent i = new Intent(this, UpdateTransactionsIntentService.class);
            PendingIntent pi = PendingIntent.getService(this, Constants.UPDATE_TRANSACTIONS_INTENT_SERVICE_REQUEST_CODE, i, 0);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(), 4*AlarmManager.INTERVAL_HOUR, pi);

            // TODO sharedPreferences.edit().putString(Constants.VERSION_NAME, versionName).apply();
        }

        accountCollection = new AccountCollection(this);

        mainActivityBalanceRecyclerViewAdapter = new MainActivityBalanceRecyclerViewAdapter(this);
        historyRecyclerViewAdapter = new HistoryRecyclerViewAdapter(this);
        schedulerRecyclerViewAdapter = new SchedulerRecyclerViewAdapter(this);
        needConfirmRecyclerViewAdapter = new NeedConfirmRecyclerViewAdapter(this);
        needCategoryRecyclerViewAdapter = new NeedCategoryRecyclerViewAdapter(this);
        expensesRecyclerViewAdapter = new ExpensesRecyclerViewAdapter(this);

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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.MANAGE_ACCOUNTS_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    accountCollection = new AccountCollection(this);
                    mainActivityBalanceRecyclerViewAdapter.refreshDataSet();
                    historyRecyclerViewAdapter.refreshDataSet();
                    schedulerRecyclerViewAdapter.refreshDataSet();
                    needConfirmRecyclerViewAdapter.refreshDataSet();
                }
                break;
            case Constants.ADD_ACCOUNT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    accountCollection = new AccountCollection(this);
                    mainActivityBalanceRecyclerViewAdapter.refreshDataSet();
                }
                break;
            case Constants.MANAGE_CATEGORIES_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    historyRecyclerViewAdapter.refreshDataSet();
                    schedulerRecyclerViewAdapter.refreshDataSet();
                    needConfirmRecyclerViewAdapter.refreshDataSet();
                    needCategoryRecyclerViewAdapter.refreshDataSet();
                }
                break;
            case Constants.EDIT_TRANSACTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    historyRecyclerViewAdapter.refreshDataSet();
                    mainActivityBalanceRecyclerViewAdapter.refreshDataSet();
                    needCategoryRecyclerViewAdapter.refreshDataSet();
                }
                break;
            case Constants.EDIT_SCHEDULED_TRANSACTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    schedulerRecyclerViewAdapter.refreshDataSet();
                    needConfirmRecyclerViewAdapter.refreshDataSet();
                }
                break;
            case Constants.ADD_TO_HISTORY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    historyRecyclerViewAdapter.refreshDataSet();
                    mainActivityBalanceRecyclerViewAdapter.refreshDataSet();
                    needConfirmRecyclerViewAdapter.refreshDataSet();
                    schedulerRecyclerViewAdapter.refreshDataSet();
                    needCategoryRecyclerViewAdapter.refreshDataSet();
                }
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
                mainActivityBalanceRecyclerViewAdapter.refreshDataSet();
                dialog.dismiss();
                break;
            case Dialogs.DELETE_SCHEDULED_TRANSACTION:
                schedulerRecyclerViewAdapter.removeItem(id);
                needConfirmRecyclerViewAdapter.refreshDataSet();
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
            case Dialogs.DELETE_SCHEDULED_TRANSACTION:
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
