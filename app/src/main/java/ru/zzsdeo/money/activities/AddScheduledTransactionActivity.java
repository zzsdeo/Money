package ru.zzsdeo.money.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.model.RepeatingTypes;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;

public class AddScheduledTransactionActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        Dialogs.DialogListener,
        AdapterView.OnItemSelectedListener {

    private EditText amount, comment;
    private Spinner repeatingTypeId;
    private TextView date, time, repeatingTextView;
    private final Calendar calendar = Calendar.getInstance();
    private CheckBox needApprove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scheduled_transaction);

        needApprove = (CheckBox) findViewById(R.id.checkBox1);
        date = (TextView) findViewById(R.id.textView);
        time = (TextView) findViewById(R.id.textView2);
        amount = (EditText) findViewById(R.id.amount);
        comment = (EditText) findViewById(R.id.comment);
        repeatingTypeId = (Spinner) findViewById(R.id.spinner4);
        repeatingTextView = (TextView) findViewById(R.id.repeatingTextView);
        Button addBtn = (Button) findViewById(R.id.addBtn);

        needApprove.setChecked(true);

        Date dateTime = calendar.getTime();
        date.setText(new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(dateTime) + "   ");
        date.setOnClickListener(this);
        time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateTime) + "   ");
        time.setOnClickListener(this);

        ArrayAdapter<String> repeatingAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.textView, new RepeatingTypes(this));
        repeatingTypeId.setAdapter(repeatingAdapter);
        repeatingTypeId.setOnItemSelectedListener(this);

        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.textView:
                Dialogs date = new Dialogs();
                bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.DATE_PICKER);
                bundle.putLong(Dialogs.DATE_IN_MILL, calendar.getTimeInMillis());
                date.setArguments(bundle);
                date.show(getFragmentManager(), Dialogs.DIALOGS_TAG);
                break;
            case R.id.textView2:
                Dialogs time = new Dialogs();
                bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.TIME_PICKER);
                bundle.putLong(Dialogs.DATE_IN_MILL, calendar.getTimeInMillis());
                time.setArguments(bundle);
                time.show(getFragmentManager(), Dialogs.DIALOGS_TAG);
                break;
            case R.id.addBtn:
                String amountString = amount.getText().toString();
                String commentString = comment.getText().toString();
                float amountFloat;
                if (amountString.isEmpty()) {
                    Toast.makeText(this, getString(R.string.need_amount), Toast.LENGTH_LONG).show();
                    return;
                } else {
                    amountFloat = Float.parseFloat(amountString);
                }

                new ScheduledTransactionCollection(this).addScheduledTransaction(
                        calendar.getTimeInMillis(),
                        amountFloat,
                        commentString,
                        needApprove.isChecked(),
                        repeatingTypeId.getSelectedItemPosition()
                );

                amount.setText("");
                comment.setText("");
                Toast.makeText(this, getString(R.string.transaction_successfully_added), Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                break;
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType, long id) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int dialogType) {

    }

    @Override
    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
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
    public void onTimeSet(int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        Date dateTime = calendar.getTime();
        time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateTime) + "   ");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
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
}