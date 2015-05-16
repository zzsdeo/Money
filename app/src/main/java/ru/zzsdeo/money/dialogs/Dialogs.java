package ru.zzsdeo.money.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;

public class Dialogs extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String DIALOGS_TAG = "dialogs_tag";

    public static final String DIALOG_TYPE = "dialog_type";
    public static final String ID = "id";
    public static final String DATE_IN_MILL = "date_in_mill";

    public static final int DATE_PICKER = 10;
    public static final int TIME_PICKER = 20;
    public static final int DELETE_SCHEDULED_TRANSACTION = 30;
    public static final int SETTINGS = 40;

    private Bundle bundle;
    private DialogListener dialogListener;

    public interface DialogListener {

        void onDialogPositiveClick(DialogFragment dialog, int dialogType, long id);

        void onDialogNegativeClick(DialogFragment dialog, int dialogType);

        void onDateSet(DatePicker view, int dialogType, int year, int monthOfYear, int dayOfMonth);

        void onTimeSet(TimePicker view, int dialogType, int hourOfDay, int minute);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle == null) throw new NullPointerException(" must call setArguments() in calling activity with DIALOG_TYPE as a key and int as a value");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dialogListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Calendar c = Calendar.getInstance();
        long dateInMill;
        switch (bundle.getInt(DIALOG_TYPE)) {

            case DATE_PICKER:
                dateInMill = bundle.getLong(DATE_IN_MILL, 0);
                if (dateInMill != 0) c.setTimeInMillis(dateInMill);

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                return new DatePickerDialog(getActivity(), this, year, month, day);

            case TIME_PICKER:
                dateInMill = bundle.getLong(DATE_IN_MILL, 0);
                if (dateInMill != 0) c.setTimeInMillis(dateInMill);

                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));

            case DELETE_SCHEDULED_TRANSACTION:
                builder.setMessage("Вы уверены?");
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(Dialogs.this, DELETE_SCHEDULED_TRANSACTION);
                    }
                });
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, DELETE_SCHEDULED_TRANSACTION, bundle.getLong(ID));
                    }
                });
                return builder.create();

            case SETTINGS:
                builder.setTitle("Настройки");
                builder.setIcon(R.mipmap.ic_action_action_account_balance_wallet);
                View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_settings, null);
                builder.setView(v);
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(Dialogs.this, SETTINGS);
                    }
                });
                builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, SETTINGS, 0);
                    }
                });

                EditText etBalance = (EditText) v.findViewById(R.id.balance);
                EditText etCardNumber = (EditText) v.findViewById(R.id.card_number);
                final TextView tvSeekBar = (TextView) v.findViewById(R.id.seek_bar_title);
                SeekBar seekBar = (SeekBar) v.findViewById(R.id.seek_bar);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

                etBalance.setText(sharedPreferences.getString(Constants.BALANCE, ""));
                etCardNumber.setText(sharedPreferences.getString(Constants.CARD_NUMBER, ""));
                int progress = sharedPreferences.getInt(Constants.NUMBER_OF_MONTHS, Constants.DEFAULT_NUM_OF_MONTHS);
                tvSeekBar.append(String.valueOf(progress));
                seekBar.setMax(Constants.MAX_NUM_OF_MONTHS - 1);
                seekBar.setProgress(progress - 1);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        tvSeekBar.setText("Количество месяцев для планирования: " + (progress + 1));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        seekBar.requestFocusFromTouch();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                return builder.create();

            default:
                return super.onCreateDialog(savedInstanceState);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dialogListener.onDateSet(view, DATE_PICKER, year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        dialogListener.onTimeSet(view, TIME_PICKER, hourOfDay, minute);
    }
}