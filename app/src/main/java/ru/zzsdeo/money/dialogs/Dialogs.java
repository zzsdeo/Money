package ru.zzsdeo.money.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class Dialogs extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String DIALOGS_TAG = "dialogs_tag";

    public static final String DIALOG_TYPE = "dialog_type";
    public static final String ID = "id";
    public static final String DATE_IN_MILL = "date_in_mill";

    public static final int DELETE_ACCOUNT = 10;
    public static final int YOU_MUST_ADD_ACCOUNT = 20;
    public static final int DATE_PICKER = 30;
    public static final int TIME_PICKER = 40;
    public static final int DELETE_CATEGORY = 50;
    public static final int DELETE_TRANSACTION = 60;
    public static final int DELETE_SCHEDULED_TRANSACTION = 70;

    private Bundle bundle;
    private DialogListener dialogListener;
    private int selection;

    public interface DialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int dialogType);
        public void onDialogPositiveClick(DialogFragment dialog, int dialogType, long id, int selection);

        public void onDialogNegativeClick(DialogFragment dialog, int dialogType);

        public void onDateSet(DatePicker view, int dialogType, int year, int monthOfYear, int dayOfMonth);

        public void onTimeSet(TimePicker view, int dialogType, int hourOfDay, int minute);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle == null) throw new NullPointerException("must call setArguments() in calling activity with DIALOG_TYPE as a key and int as a value");
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
            case DELETE_ACCOUNT:
                builder.setTitle("Вы уверены?");
                builder.setMessage("Это приведет к удаленнию всех транзакций связанных с данным счетом.");
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(Dialogs.this, DELETE_ACCOUNT);
                    }
                });
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, DELETE_ACCOUNT, bundle.getLong(ID), 0);
                    }
                });
                return builder.create();
            case YOU_MUST_ADD_ACCOUNT:
                builder.setMessage("Для работы приложения необходимо создать хотя бы один счет.");
                builder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, YOU_MUST_ADD_ACCOUNT);
                    }
                });
                return builder.create();
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
            case DELETE_CATEGORY:
                builder.setMessage("Вы уверены?");
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(Dialogs.this, DELETE_CATEGORY);
                    }
                });
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, DELETE_CATEGORY, bundle.getLong(ID), 0);
                    }
                });
                return builder.create();
            case DELETE_TRANSACTION:
                builder.setMessage("Вы уверены?");
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(Dialogs.this, DELETE_TRANSACTION);
                    }
                });
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, DELETE_TRANSACTION, bundle.getLong(ID), 0);
                    }
                });
                return builder.create();
            case DELETE_SCHEDULED_TRANSACTION:
                selection = 0;
                builder.setSingleChoiceItems(new CharSequence[] {"Удалить выбранную транзакцию", "Удалить все транзакции серии"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection = which;
                    }
                });
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(Dialogs.this, DELETE_SCHEDULED_TRANSACTION);
                    }
                });
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, DELETE_SCHEDULED_TRANSACTION, bundle.getLong(ID), selection);
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