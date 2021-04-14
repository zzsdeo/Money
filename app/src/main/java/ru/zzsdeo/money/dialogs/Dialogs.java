package ru.zzsdeo.money.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.activities.MainActivity;
import ru.zzsdeo.money.adapters.SchedulerRecyclerViewAdapter;
import ru.zzsdeo.money.model.RepeatingTypes;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;

public class Dialogs extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String DIALOGS_TAG = "dialogs_tag";

    public static final String DIALOG_TYPE = "dialog_type";
    public static final String ID = "id";
    public static final String DATE_IN_MILL = "date_in_mill";

    public static final int DATE_PICKER = 10;
    public static final int TIME_PICKER = 20;
    public static final int DELETE_SCHEDULED_TRANSACTION = 30;
    public static final int SETTINGS = 40;
    public static final int DETAILS = 50;

    private Bundle bundle;
    private DialogListener dialogListener;

    public interface DialogListener {

        void onDialogPositiveClick(DialogFragment dialog, int dialogType, long id);

        void onDialogNegativeClick(DialogFragment dialog, int dialogType);

        void onDateSet(int year, int monthOfYear, int dayOfMonth);

        void onTimeSet(int hourOfDay, int minute);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle == null) throw new NullPointerException(" must call setArguments() in calling activity with DIALOG_TYPE as a key and int as a value");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dialogListener = (DialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement DialogListener");
        }
    }

    @NonNull
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
                builder.setMessage(getActivity().getString(R.string.are_you_sure));
                builder.setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(Dialogs.this, DELETE_SCHEDULED_TRANSACTION);
                    }
                });
                builder.setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, DELETE_SCHEDULED_TRANSACTION, bundle.getLong(ID));
                    }
                });
                return builder.create();

            case SETTINGS:
                builder.setTitle(getActivity().getString(R.string.settings));
                builder.setIcon(R.mipmap.ic_action_action_account_balance_wallet);
                View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_settings, null);
                builder.setView(v);
                builder.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(Dialogs.this, SETTINGS);
                    }
                });
                builder.setPositiveButton(getActivity().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, SETTINGS, 0);
                    }
                });

                EditText etBalance = (EditText) v.findViewById(R.id.balance);
                EditText etCardNumber = (EditText) v.findViewById(R.id.card_number);
                final TextView tvSeekBar = (TextView) v.findViewById(R.id.seek_bar_title);
                SeekBar seekBar = (SeekBar) v.findViewById(R.id.seek_bar);
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

                etBalance.setText(sharedPreferences.getString(Constants.BALANCE, ""));
                etCardNumber.setText(sharedPreferences.getString(Constants.CARD_NUMBER, ""));
                int progress = sharedPreferences.getInt(Constants.NUMBER_OF_MONTHS, Constants.DEFAULT_NUM_OF_MONTHS);
                tvSeekBar.append(" " + progress);
                seekBar.setMax(Constants.MAX_NUM_OF_MONTHS - 1);
                seekBar.setProgress(progress - 1);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        tvSeekBar.setText(String.format(Locale.getDefault(),"%s %d", getActivity().getString(R.string.num_of_months_to_planning), progress + 1));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        seekBar.requestFocusFromTouch();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                checkBox.setChecked(sharedPreferences.getBoolean(Constants.DISPLAY_DATE_TIME, true));
                return builder.create();

            case DETAILS:
                ArrayList<ScheduledTransactionCollection.TransactionsHolder> holders = ((MainActivity) getActivity()).schedulerRecyclerViewAdapter.mTransactions;
                ScheduledTransactionCollection.TransactionsHolder holder = holders.get(bundle.getInt(ID));

                builder.setTitle(getActivity().getString(R.string.details));
                v = getActivity().getLayoutInflater().inflate(R.layout.dialog_details, null);
                builder.setView(v);
                builder.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(Dialogs.this, DETAILS);
                    }
                });

                TextView comment = (TextView) v.findViewById(R.id.comment);
                TextView amount = (TextView) v.findViewById(R.id.amount);
                TextView balance = (TextView) v.findViewById(R.id.bal);
                TextView repeatingType = (TextView) v.findViewById(R.id.repeating_type);
                TextView needConfirm = (TextView) v.findViewById(R.id.need_confirm);
                TextView dateTime = (TextView) v.findViewById(R.id.date_time);

                comment.setText(holder.scheduledTransaction.getComment());

                amount.setText(String.valueOf(holder.scheduledTransaction.getAmount()));

                balance.setText(String.valueOf(holder.getBalance()));

                int rt = holder.scheduledTransaction.getRepeatingTypeId();
                repeatingType.setText(new RepeatingTypes(getActivity()).get(rt));
                if (rt == 0) {
                    repeatingType.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_av_repeat_one, 0, 0, 0);
                } else {
                    repeatingType.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_av_repeat, 0, 0, 0);
                }

                if (holder.scheduledTransaction.getNeedApprove()) {
                    needConfirm.setText(getActivity().getString(R.string.need_confirm));
                    needConfirm.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_action_alarm_on, 0, 0, 0);
                } else {
                    needConfirm.setText(getActivity().getString(R.string.not_need_confirm));
                    needConfirm.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_action_alarm_off, 0, 0, 0);
                }

                long dt = holder.dateTime;
                dateTime.setText(new SimpleDateFormat(SchedulerRecyclerViewAdapter.DATE_FORMAT, Locale.getDefault()).format(new Date(dt)));
                if (dt <= c.getTimeInMillis()) {
                    dateTime.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_social_notifications_on, 0, 0, 0);
                } else {
                    dateTime.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_action_event, 0, 0, 0);
                }

                return builder.create();

            default:
                return super.onCreateDialog(savedInstanceState);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dialogListener.onDateSet(year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        dialogListener.onTimeSet(hourOfDay, minute);
    }
}