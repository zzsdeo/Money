package ru.zzsdeo.money.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.adapters.AbstractSpinnerAdapter;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.Category;
import ru.zzsdeo.money.model.CategoryCollection;

public class AddTransactionActivity extends ActionBarActivity
        implements
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        Dialogs.DialogListener,
        AdapterView.OnItemSelectedListener {

    public static final String DEFAULT_ACCOUNT_ID = "default_account_id";

    private EditText amount, commission, comment;
    private Spinner destinationAccountId, accountId;
    private TextView date, time;
    private final Calendar calendar = Calendar.getInstance();
    private SharedPreferences sharedPreferences;
    private CheckBox isDefaultAccount;
    private AccountSpinnerAdapter accountSpinnerAdapter, destinationAccountSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        AccountCollection accountCollection = new AccountCollection(this);
        CategoryCollection categoryCollection = new CategoryCollection(this);
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        accountId = (Spinner) findViewById(R.id.spinner1);
        isDefaultAccount = (CheckBox) findViewById(R.id.checkBox1);
        date = (TextView) findViewById(R.id.textView);
        time = (TextView) findViewById(R.id.textView2);
        amount = (EditText) findViewById(R.id.amount);
        commission = (EditText) findViewById(R.id.commission);
        comment = (EditText) findViewById(R.id.comment);
        CheckBox isTransfer = (CheckBox) findViewById(R.id.checkBox2);
        destinationAccountId = (Spinner) findViewById(R.id.spinner2);
        Spinner categoryId = (Spinner) findViewById(R.id.spinner3);
        Button addBtn = (Button) findViewById(R.id.addBtn);

        accountSpinnerAdapter = new AccountSpinnerAdapter(this, accountCollection);
        accountId.setAdapter(accountSpinnerAdapter);
        accountId.setOnItemSelectedListener(this);
        ArrayList<Object> ids = accountSpinnerAdapter.getAllItemsIds();
        int selection = ids.indexOf(sharedPreferences.getLong(DEFAULT_ACCOUNT_ID, 0));
        accountId.setSelection(selection);

        isDefaultAccount.setOnCheckedChangeListener(this);

        Date dateTime = calendar.getTime();
        date.setText(new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(dateTime) + "   ");
        date.setOnClickListener(this);
        time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateTime) + "   ");
        time.setOnClickListener(this);

        if (accountCollection.size() == 1) isTransfer.setVisibility(View.GONE);
        isTransfer.setOnCheckedChangeListener(this);

        destinationAccountId.setVisibility(View.GONE);
        destinationAccountSpinnerAdapter = new AccountSpinnerAdapter(this, accountCollection);
        destinationAccountId.setAdapter(destinationAccountSpinnerAdapter);

        categoryId.setAdapter(new AbstractSpinnerAdapter(this, categoryCollection, new String[] {"Без категории"}) {
            @Override
            public CharSequence getTitle(Object object) {
                return ((Category)object).getName();
            }

            @Override
            public long getObjectId(Object object) {
                return ((Category)object).getCategoryId();
            }
        });

        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.textView:
                Dialogs date = new Dialogs();
                bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.DATE_PICKER);
                date.setArguments(bundle);
                date.show(getFragmentManager(), Dialogs.DIALOGS_TAG);
                break;
            case R.id.textView2:
                Dialogs time = new Dialogs();
                bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.TIME_PICKER);
                time.setArguments(bundle);
                time.show(getFragmentManager(), Dialogs.DIALOGS_TAG);
                break;
            case R.id.addBtn:
                /*String nameString = name.getText().toString();
                String cardNumberString = cardNumber.getText().toString();
                String balanceString = balance.getText().toString();
                float balanceFloat;
                if (nameString.isEmpty()) {
                    Toast.makeText(this, "Необходимо ввести название", Toast.LENGTH_LONG).show();
                    return;
                }
                if (balanceString.isEmpty()) {
                    balanceFloat = 0;
                } else {
                    balanceFloat = Float.parseFloat(balanceString);
                }
                new AccountCollection(this).addAccount(nameString, cardNumberString, balanceFloat);
                name.setText("");
                cardNumber.setText("");
                balance.setText("");
                Toast.makeText(this, "Счет успешно добавлен", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);*/
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox1:
                if (isChecked) {
                    sharedPreferences.edit().putLong(DEFAULT_ACCOUNT_ID, accountId.getSelectedItemId()).apply();
                } else {
                    if (sharedPreferences.getLong(DEFAULT_ACCOUNT_ID, 0) == accountId.getSelectedItemId()) {
                        sharedPreferences.edit().remove(DEFAULT_ACCOUNT_ID).apply();
                    }
                }
                break;
            case R.id.checkBox2:
                if (isChecked) {
                    destinationAccountId.setVisibility(View.VISIBLE);
                } else {
                    destinationAccountId.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType, long id) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int dialogType) {

    }

    @Override
    public void onDateSet(DatePicker view, int dialogType, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        Date dateTime = calendar.getTime();
        date.setText(new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(dateTime) + "   ");
    }

    @Override
    public void onTimeSet(TimePicker view, int dialogType, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        Date dateTime = calendar.getTime();
        time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateTime) + "   ");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner1:
                if (id == sharedPreferences.getLong(DEFAULT_ACCOUNT_ID, 0)) {
                    isDefaultAccount.setChecked(true);
                } else {
                    isDefaultAccount.setChecked(false);
                }
                ArrayList<Object> ids = accountSpinnerAdapter.getAllItemsIds();
                int pos = ids.indexOf(id);
                destinationAccountSpinnerAdapter.removeItem(pos);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class AccountSpinnerAdapter extends AbstractSpinnerAdapter {

        public AccountSpinnerAdapter(Context context, LinkedHashMap collection) {
            super(context, collection);
        }

        @Override
        public CharSequence getTitle(Object object) {
            return ((Account)object).getName();
        }

        @Override
        public long getObjectId(Object object) {
            return ((Account)object).getAccountId();
        }
    }
}