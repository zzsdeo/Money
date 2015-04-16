package ru.zzsdeo.money.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.adapters.AbstractSpinnerAdapter;
import ru.zzsdeo.money.adapters.HistoryRecyclerViewAdapter;
import ru.zzsdeo.money.adapters.ManageAccountsRecyclerViewAdapter;
import ru.zzsdeo.money.db.TableTransactions;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.Category;
import ru.zzsdeo.money.model.CategoryCollection;
import ru.zzsdeo.money.model.Transaction;
import ru.zzsdeo.money.model.TransactionCollection;

public class EditTransactionActivity extends ActionBarActivity
        implements
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        Dialogs.DialogListener,
        AdapterView.OnItemSelectedListener {

    private EditText amount, commission, comment;
    private Spinner destinationAccountId, accountId, categoryId;
    private TextView date, time;
    private final Calendar calendar = Calendar.getInstance();
    private SharedPreferences sharedPreferences;
    private CheckBox isDefaultAccount, isTransfer;
    private AccountSpinnerAdapter accountSpinnerAdapter, destinationAccountSpinnerAdapter;
    private Transaction transaction;
    private AccountCollection accountCollection;
    private long destinationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) finish();
        assert bundle != null;
        transaction = new TransactionCollection(this).get(bundle.getLong(HistoryRecyclerViewAdapter.TRANSACTION_ID));

        accountCollection = new AccountCollection(this);
        CategoryCollection categoryCollection = new CategoryCollection(this);
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        accountId = (Spinner) findViewById(R.id.spinner1);
        isDefaultAccount = (CheckBox) findViewById(R.id.checkBox1);
        date = (TextView) findViewById(R.id.textView);
        time = (TextView) findViewById(R.id.textView2);
        amount = (EditText) findViewById(R.id.amount);
        commission = (EditText) findViewById(R.id.commission);
        comment = (EditText) findViewById(R.id.comment);
        isTransfer = (CheckBox) findViewById(R.id.checkBox2);
        destinationAccountId = (Spinner) findViewById(R.id.spinner2);
        categoryId = (Spinner) findViewById(R.id.spinner3);
        Button addBtn = (Button) findViewById(R.id.addBtn);

        accountSpinnerAdapter = new AccountSpinnerAdapter(this, accountCollection);
        accountId.setAdapter(accountSpinnerAdapter);
        accountId.setOnItemSelectedListener(this);
        ArrayList<Object> ids = accountSpinnerAdapter.getAllItemsIds();
        long accId = transaction.getAccountId();
        int selection = ids.indexOf(accId);
        accountId.setSelection(selection);

        isDefaultAccount.setOnCheckedChangeListener(this);

        long dateTime = transaction.getDateInMill();
        calendar.setTimeInMillis(dateTime);
        date.setText(new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(dateTime) + "   ");
        date.setOnClickListener(this);
        time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateTime) + "   ");
        time.setOnClickListener(this);

        amount.setText(String.valueOf(transaction.getAmount()));

        commission.setText(String.valueOf(-transaction.getCommission()));

        comment.setText(transaction.getComment());

        if (accountCollection.size() == 1) isTransfer.setVisibility(View.GONE);
        isTransfer.setOnCheckedChangeListener(this);

        destinationAccountId.setVisibility(View.GONE);
        destinationAccountSpinnerAdapter = new AccountSpinnerAdapter(this, accountCollection);
        destinationAccountId.setAdapter(destinationAccountSpinnerAdapter);
        destinationId = transaction.getDestinationAccountId();
        if (destinationId != 0) {
            isTransfer.setChecked(true);
            destinationAccountId.setVisibility(View.VISIBLE);
            ids.clear();
            ids = destinationAccountSpinnerAdapter.getAllItemsIds();
            selection = ids.indexOf(destinationId);
            destinationAccountId.setSelection(selection);
        }

        AbstractSpinnerAdapter categoryAdapter = new AbstractSpinnerAdapter(this, categoryCollection, new String[] {"Без категории"}) {
            @Override
            public CharSequence getTitle(Object object) {
                return ((Category)object).getName();
            }

            @Override
            public long getObjectId(Object object) {
                return ((Category)object).getCategoryId();
            }
        };
        categoryId.setAdapter(categoryAdapter);
        ids.clear();
        ids = categoryAdapter.getAllItemsIds();
        selection = ids.indexOf(transaction.getCategoryId());
        categoryId.setSelection(selection);

        addBtn.setText("Сохранить");
        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.textView:
                Dialogs date = new Dialogs();
                bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.DATE_PICKER);
                bundle.putLong(Dialogs.DATE_IN_MILL, transaction.getDateInMill());
                date.setArguments(bundle);
                date.show(getFragmentManager(), Dialogs.DIALOGS_TAG);
                break;
            case R.id.textView2:
                Dialogs time = new Dialogs();
                bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.TIME_PICKER);
                bundle.putLong(Dialogs.DATE_IN_MILL, transaction.getDateInMill());
                time.setArguments(bundle);
                time.show(getFragmentManager(), Dialogs.DIALOGS_TAG);
                break;
            case R.id.addBtn:
                if (destinationId != 0) {
                    TransactionCollection linkedTransactions = new TransactionCollection(this,
                            new String[] {
                                    TableTransactions.COLUMN_LINKED_TRANSACTION_ID + "=" + transaction.getTransactionId(),
                                    null
                            });
                    Iterator<Transaction> it = linkedTransactions.values().iterator();
                    Transaction linkedTransaction = it.next();
                    long id = linkedTransaction.getTransactionId();
                    linkedTransactions.removeTransaction(id);
                }

                String amountString = amount.getText().toString();
                String commissionString = commission.getText().toString();
                String commentString = comment.getText().toString();
                float amountFloat, commissionFloat;
                long destination;
                Account destAcc = null;
                if (amountString.isEmpty()) {
                    Toast.makeText(this, "Необходимо ввести сумму", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    amountFloat = Float.parseFloat(amountString);
                }
                if (commissionString.isEmpty()) {
                    commissionFloat = 0;
                } else {
                    commissionFloat = Float.parseFloat(commissionString);
                }
                if (isTransfer.isChecked()) {
                    destination = destinationAccountId.getSelectedItemId();
                    if (amountFloat > 0) {
                        Toast.makeText(this, "Сумма должна быть отрицательной", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        destAcc = accountCollection.get(destination);
                    }
                } else {
                    destination = 0;
                }

                long accId = accountId.getSelectedItemId();

                transaction.setAccountId(accId);
                transaction.setDateInMill(calendar.getTimeInMillis());
                transaction.setAmount(amountFloat);
                transaction.setCommission(-commissionFloat);
                transaction.setComment(commentString);
                transaction.setDestinationAccountId(destination);
                transaction.setCategoryId(categoryId.getSelectedItemId());
                transaction.setLinkedTransactionId(0);

                // Обновление баланса
                TransactionCollection transactionCollection = new TransactionCollection(this,
                        new String[] {
                                TableTransactions.COLUMN_ACCOUNT_ID + "=" + accId,
                                null
                        });
                float balance = 0;
                for (Transaction transaction : transactionCollection.values()) {
                    balance = balance + transaction.getAmount() + transaction.getCommission();
                }
                accountCollection.get(accId).setBalance(balance);

                // Создание связанной транзакции
                if (destAcc != null) {
                    long destAccId = destAcc.getAccountId();
                    new TransactionCollection(this).addTransaction(
                            destAccId,
                            calendar.getTimeInMillis(),
                            -amountFloat,
                            0,
                            "Перевод с: " + accountCollection.get(accId).getName(),
                            0,
                            categoryId.getSelectedItemId(),
                            transaction.getTransactionId()
                    );

                    // Обновление баланса связанного счета
                    transactionCollection = new TransactionCollection(this,
                            new String[] {
                                    TableTransactions.COLUMN_ACCOUNT_ID + "=" + destAccId,
                                    null
                            });
                    balance = 0;
                    for (Transaction transaction : transactionCollection.values()) {
                        balance = balance + transaction.getAmount() + transaction.getCommission();
                    }
                    accountCollection.get(destAccId).setBalance(balance);
                }

                Toast.makeText(this, "Сохранено", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox1:
                if (isChecked) {
                    sharedPreferences.edit().putLong(AddTransactionActivity.DEFAULT_ACCOUNT_ID, accountId.getSelectedItemId()).apply();
                } else {
                    if (sharedPreferences.getLong(AddTransactionActivity.DEFAULT_ACCOUNT_ID, 0) == accountId.getSelectedItemId()) {
                        sharedPreferences.edit().remove(AddTransactionActivity.DEFAULT_ACCOUNT_ID).apply();
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
                if (id == sharedPreferences.getLong(AddTransactionActivity.DEFAULT_ACCOUNT_ID, 0)) {
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