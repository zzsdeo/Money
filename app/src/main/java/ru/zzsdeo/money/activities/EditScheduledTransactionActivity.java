package ru.zzsdeo.money.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.adapters.AbstractSpinnerAdapter;
import ru.zzsdeo.money.adapters.HistoryRecyclerViewAdapter;
import ru.zzsdeo.money.adapters.SchedulerRecyclerViewAdapter;
import ru.zzsdeo.money.db.TableScheduledTransactions;
import ru.zzsdeo.money.db.TableTransactions;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.Category;
import ru.zzsdeo.money.model.CategoryCollection;
import ru.zzsdeo.money.model.RepeatingTypes;
import ru.zzsdeo.money.model.ScheduledTransaction;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;
import ru.zzsdeo.money.model.Transaction;
import ru.zzsdeo.money.model.TransactionCollection;

public class EditScheduledTransactionActivity extends ActionBarActivity
        implements
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        Dialogs.DialogListener,
        AdapterView.OnItemSelectedListener {

    private EditText amount, commission, comment;
    private Spinner destinationAccountId, accountId, categoryId, repeatingTypeId;
    private TextView date, time, repeatingTextView;
    private final Calendar calendar = Calendar.getInstance();
    private CheckBox isTransfer, needApprove;
    private AccountSpinnerAdapter accountSpinnerAdapter, destinationAccountSpinnerAdapter;
    private AccountCollection accountCollection;
    private ScheduledTransaction scheduledTransaction;
    private long destinationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scheduled_transaction);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) finish();
        assert bundle != null;
        scheduledTransaction = new ScheduledTransactionCollection(this).get(bundle.getLong(SchedulerRecyclerViewAdapter.SCHEDULED_TRANSACTION_ID));

        accountCollection = new AccountCollection(this);
        CategoryCollection categoryCollection = new CategoryCollection(this);

        accountId = (Spinner) findViewById(R.id.spinner1);
        needApprove = (CheckBox) findViewById(R.id.checkBox1);
        date = (TextView) findViewById(R.id.textView);
        time = (TextView) findViewById(R.id.textView2);
        amount = (EditText) findViewById(R.id.amount);
        commission = (EditText) findViewById(R.id.commission);
        comment = (EditText) findViewById(R.id.comment);
        isTransfer = (CheckBox) findViewById(R.id.checkBox2);
        destinationAccountId = (Spinner) findViewById(R.id.spinner2);
        categoryId = (Spinner) findViewById(R.id.spinner3);
        repeatingTypeId = (Spinner) findViewById(R.id.spinner4);
        repeatingTextView = (TextView) findViewById(R.id.repeatingTextView);
        Button addBtn = (Button) findViewById(R.id.addBtn);

        accountSpinnerAdapter = new AccountSpinnerAdapter(this, accountCollection);
        accountId.setAdapter(accountSpinnerAdapter);
        accountId.setOnItemSelectedListener(this);
        ArrayList<Object> ids = accountSpinnerAdapter.getAllItemsIds();
        long accId = scheduledTransaction.getAccountId();
        int selection = ids.indexOf(accId);
        accountId.setSelection(selection);

        needApprove.setChecked(scheduledTransaction.getNeedApprove());

        long dateTime = scheduledTransaction.getDateInMill();
        calendar.setTimeInMillis(dateTime);
        date.setText(new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(dateTime) + "   ");
        date.setOnClickListener(this);
        time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateTime) + "   ");
        time.setOnClickListener(this);

        amount.setText(String.valueOf(scheduledTransaction.getAmount()));

        commission.setText(String.valueOf(scheduledTransaction.getCommission()));

        comment.setText(scheduledTransaction.getComment());

        if (accountCollection.size() == 1) isTransfer.setVisibility(View.GONE);
        isTransfer.setOnCheckedChangeListener(this);

        destinationAccountId.setVisibility(View.GONE);
        destinationAccountSpinnerAdapter = new AccountSpinnerAdapter(this, accountCollection);
        destinationAccountId.setAdapter(destinationAccountSpinnerAdapter);
        destinationId = scheduledTransaction.getDestinationAccountId();
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
        selection = ids.indexOf(scheduledTransaction.getCategoryId());
        categoryId.setSelection(selection);

        ArrayAdapter<String> repeatingAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.textView, new RepeatingTypes(this).getTypes());
        repeatingTypeId.setAdapter(repeatingAdapter);
        repeatingTypeId.setOnItemSelectedListener(this);
        selection = scheduledTransaction.getRepeatingTypeId();
        repeatingTypeId.setSelection(selection);

        switch (selection) {
            case 3:
                repeatingTextView.setText(new SimpleDateFormat("dd", Locale.getDefault()).format(calendar.getTime()));
                break;
            case 4:
                repeatingTextView.setText(new SimpleDateFormat("E", Locale.getDefault()).format(calendar.getTime()));
                break;
        }

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
                bundle.putLong(Dialogs.DATE_IN_MILL, scheduledTransaction.getDateInMill());
                date.setArguments(bundle);
                date.show(getFragmentManager(), Dialogs.DIALOGS_TAG);
                break;
            case R.id.textView2:
                Dialogs time = new Dialogs();
                bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.TIME_PICKER);
                bundle.putLong(Dialogs.DATE_IN_MILL, scheduledTransaction.getDateInMill());
                time.setArguments(bundle);
                time.show(getFragmentManager(), Dialogs.DIALOGS_TAG);
                break;
            case R.id.addBtn:
                if (destinationId != 0) {
                    ScheduledTransactionCollection linkedTransactions = new ScheduledTransactionCollection(this,
                            new String[] {
                                    TableScheduledTransactions.COLUMN_LINKED_TRANSACTION_ID + "=" + scheduledTransaction.getScheduledTransactionId(),
                                    null
                            });
                    Iterator<ScheduledTransaction> it = linkedTransactions.values().iterator();
                    ScheduledTransaction linkedTransaction = it.next();
                    long id = linkedTransaction.getScheduledTransactionId();
                    linkedTransactions.removeScheduledTransaction(id);
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

                scheduledTransaction.setAccountId(accId);
                scheduledTransaction.setDateInMill(calendar.getTimeInMillis());
                scheduledTransaction.setAmount(amountFloat);
                scheduledTransaction.setCommission();


                long linkedTransactionId = new ScheduledTransactionCollection(this).addScheduledTransaction(
                        accId,
                        calendar.getTimeInMillis(),
                        amountFloat,
                        -commissionFloat,
                        commentString,
                        destination,
                        needApprove.isChecked(),
                        repeatingTypeId.getSelectedItemPosition(),
                        categoryId.getSelectedItemId(),
                        0
                );

                // Создание связанной транзакции
                if (destAcc != null) {
                    long destAccId = destAcc.getAccountId();
                    new ScheduledTransactionCollection(this).addScheduledTransaction(
                            destAccId,
                            calendar.getTimeInMillis(),
                            -amountFloat,
                            0,
                            "Перевод с: " + accountCollection.get(accId).getName(),
                            0,
                            needApprove.isChecked(),
                            repeatingTypeId.getSelectedItemPosition(),
                            categoryId.getSelectedItemId(),
                            linkedTransactionId
                    );
                }

                Toast.makeText(this, "Сохранено", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
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
        switch (repeatingTypeId.getSelectedItemPosition()) {
            case 3:
                repeatingTextView.setText(new SimpleDateFormat("dd", Locale.getDefault()).format(calendar.getTime()));
                break;
            case 4:
                repeatingTextView.setText(new SimpleDateFormat("E", Locale.getDefault()).format(calendar.getTime()));
                break;
            default:
                repeatingTextView.setText("");
                break;
        }
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
                ArrayList<Object> ids = accountSpinnerAdapter.getAllItemsIds();
                int pos = ids.indexOf(id);
                destinationAccountSpinnerAdapter.removeItem(pos);
                break;
            case R.id.spinner4:
                switch (position) {
                    case 3:
                        repeatingTextView.setText(new SimpleDateFormat("dd", Locale.getDefault()).format(calendar.getTime()));
                        break;
                    case 4:
                        repeatingTextView.setText(new SimpleDateFormat("E", Locale.getDefault()).format(calendar.getTime()));
                        break;
                    default:
                        repeatingTextView.setText("");
                        break;
                }
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