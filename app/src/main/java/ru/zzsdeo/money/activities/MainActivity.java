package ru.zzsdeo.money.activities;

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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TimePicker;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.melnykov.fab.FloatingActionButton;

import java.util.Calendar;

import ru.zzsdeo.money.Constants;

import ru.zzsdeo.money.adapters.SchedulerRecyclerViewAdapter;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;
import ru.zzsdeo.money.services.ServiceReceiver;
import ru.zzsdeo.money.services.UpdateTransactionsIntentService;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, Dialogs.DialogListener, ObservableScrollViewCallbacks {

    public SchedulerRecyclerViewAdapter schedulerRecyclerViewAdapter;
    private SharedPreferences sharedPreferences;
    private ServiceReceiver serviceReceiver;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Dialogs dialog = new Dialogs();
                Bundle bundle = new Bundle();
                bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.SETTINGS);
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), Dialogs.DIALOGS_TAG);
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

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String versionName = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!versionName.equals(sharedPreferences.getString(Constants.VERSION_NAME, ""))) {
            Intent i = new Intent(this, UpdateTransactionsIntentService.class);
            PendingIntent pi = PendingIntent.getService(this, Constants.UPDATE_TRANSACTIONS_INTENT_SERVICE_REQUEST_CODE, i, 0);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(), 4*AlarmManager.INTERVAL_HOUR, pi);

            //sharedPreferences.edit().putString(Constants.VERSION_NAME, versionName).apply();
        }

        // адаптеры

        schedulerRecyclerViewAdapter = new SchedulerRecyclerViewAdapter(this);

        // заголовок - баланс

        setTitleAsBalance();

        // вьюхи

        ObservableRecyclerView recyclerView = (ObservableRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(schedulerRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setScrollViewCallbacks(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceReceiver);
    }

    @Override
    public void onClick(View v) {
        startActivityForResult(
                new Intent(this, AddScheduledTransactionActivity.class),
                Constants.ADD_SCHEDULED_TRANSACTION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.ADD_SCHEDULED_TRANSACTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    schedulerRecyclerViewAdapter.refreshDataSet();
                }
                break;
            case Constants.EDIT_SCHEDULED_TRANSACTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    schedulerRecyclerViewAdapter.refreshDataSet();
                }
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType, long id) {
        switch (dialogType) {
            case Dialogs.DELETE_SCHEDULED_TRANSACTION:
                new ScheduledTransactionCollection(this).removeScheduledTransaction(id);
                schedulerRecyclerViewAdapter.refreshDataSet();
                dialog.dismiss();
                break;
            case Dialogs.SETTINGS:
                EditText etBalance = (EditText) dialog.getDialog().findViewById(R.id.balance);
                EditText etCardNumber = (EditText) dialog.getDialog().findViewById(R.id.card_number);
                SeekBar seekBar = (SeekBar) dialog.getDialog().findViewById(R.id.seek_bar);

                sharedPreferences.edit()
                        .putString(Constants.BALANCE, etBalance.getText().toString().trim())
                        .putString(Constants.CARD_NUMBER, etCardNumber.getText().toString().trim())
                        .putInt(Constants.NUMBER_OF_MONTHS, seekBar.getProgress() + 1)
                        .apply();

                setTitleAsBalance();
                schedulerRecyclerViewAdapter.refreshDataSet();
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int dialogType) {
        switch (dialogType) {
            case Dialogs.DELETE_SCHEDULED_TRANSACTION:
                dialog.dismiss();
                break;
            case Dialogs.SETTINGS:
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

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getSupportActionBar();
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }
    }

    public void setTitleAsBalance () {
        String title = sharedPreferences.getString(Constants.BALANCE, getString(R.string.app_name));
        if (title != null) {
            if (title.isEmpty()) title = getString(R.string.app_name);
        }
        getSupportActionBar().setTitle(title);
    }
}